package unimelb.mf.client.sync;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

import arc.xml.XmlDoc;
import arc.xml.XmlStringWriter;
import unimelb.mf.client.file.PosixAttributes;
import unimelb.mf.client.sync.check.AssetItem;
import unimelb.mf.client.sync.check.ChecksumType;
import unimelb.mf.client.sync.settings.Action;
import unimelb.mf.client.sync.settings.Job;
import unimelb.mf.client.sync.task.AssetDownloadTask;
import unimelb.mf.client.sync.task.AssetSetCheckTask;
import unimelb.mf.client.sync.task.DataTransferListener;
import unimelb.mf.client.sync.task.FileSetCheckTask;
import unimelb.mf.client.sync.task.FileSetUploadTask;
import unimelb.mf.client.sync.task.SyncDeleteAssetsTask;
import unimelb.mf.client.sync.task.SyncDeleteFilesTask;
import unimelb.mf.client.task.AbstractMFApp;
import unimelb.mf.client.util.MailUtils;
import unimelb.mf.client.util.TimeUtils;

public abstract class MFSyncApp extends AbstractMFApp<unimelb.mf.client.sync.settings.Settings> implements Runnable {

    private unimelb.mf.client.sync.settings.Settings _settings;
    private ThreadPoolExecutor _workers;
    private ThreadPoolExecutor _queriers;
    private Timer _daemonTimer;
    private Thread _daemonListener;
    private ServerSocket _daemonListenerSocket;

    private AtomicInteger _nbUploadedFiles = new AtomicInteger(0);
    private AtomicInteger _nbSkippedFiles = new AtomicInteger(0);
    private AtomicInteger _nbFailedFiles = new AtomicInteger(0);
    private AtomicLong _nbUploadedBytes = new AtomicLong(0);
    private AtomicInteger _nbDeletedFiles = new AtomicInteger(0);

    private AtomicInteger _nbDownloadedAssets = new AtomicInteger(0);
    private AtomicInteger _nbSkippedAssets = new AtomicInteger(0);
    private AtomicInteger _nbFailedAssets = new AtomicInteger(0);
    private AtomicLong _nbDownloadedBytes = new AtomicLong(0);
    private AtomicInteger _nbDeletedAssets = new AtomicInteger(0);

    private DataTransferListener<Path, String> _ul;
    private DataTransferListener<String, Path> _dl;

    private Long _stimeLast = null;
    private Long _stime = null;

    private Long _execStartTime = null;

    protected MFSyncApp() {
        super();
        _settings = new unimelb.mf.client.sync.settings.Settings();
        _ul = new DataTransferListener<Path, String>() {

            @Override
            public void transferStarted(Path src, String dst) {
                // TODO
            }

            @Override
            public void transferFailed(Path src, String dst) {
                _nbFailedFiles.getAndIncrement();
                // TODO record failed files.
            }

            @Override
            public void transferCompleted(Path src, String dst) {
                _nbUploadedFiles.getAndIncrement();
            }

            @Override
            public void transferSkipped(Path src, String dst) {
                _nbSkippedFiles.getAndIncrement();
            }

            @Override
            public void transferProgressed(Path src, String dst, long increment) {
                _nbUploadedBytes.getAndAdd(increment);
            }
        };
        _dl = new DataTransferListener<String, Path>() {

            @Override
            public void transferStarted(String src, Path dst) {
                // TODO
            }

            @Override
            public void transferFailed(String src, Path dst) {
                _nbFailedAssets.getAndIncrement();
                // TODO record failed files.
            }

            @Override
            public void transferCompleted(String src, Path dst) {
                _nbDownloadedAssets.getAndIncrement();
            }

            @Override
            public void transferSkipped(String src, Path dst) {
                _nbSkippedAssets.getAndIncrement();
            }

            @Override
            public void transferProgressed(String src, Path dst, long increment) {
                _nbDownloadedBytes.getAndAdd(increment);
            }
        };
    }

    @Override
    public String description() {
        return "The Mediaflux client application to upload, download or check data.";
    }

    protected void preExecute() throws Throwable {

    }

    @Override
    public void run() {
        try {
            execute();
        } catch (Throwable e) {
            if (e instanceof InterruptedException) {
                logger().log(Level.WARNING, e.getMessage());
                Thread.currentThread().interrupt();
            } else {
                logger().log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    @Override
    public void execute() throws Throwable {

        preExecute();

        // take down the current system time.
        _execStartTime = System.currentTimeMillis();

        if (!settings().hasJobs()) {
            throw new Exception("No job found!");
        }
        if (settings().hasOnlyCheckJobs()) {
            // Check jobs do not need to run into daemon mode.
            settings().setDaemon(false);
        }

        _queriers = new ThreadPoolExecutor(settings().numberOfWorkers(), settings().numberOfWorkers(), 0,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, applicationName() + ".querier");
                    }
                }, new ThreadPoolExecutor.CallerRunsPolicy());
        _workers = new ThreadPoolExecutor(settings().numberOfWorkers(), settings().numberOfWorkers(), 0,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, applicationName() + ".worker");
                    }
                }, new ThreadPoolExecutor.CallerRunsPolicy());

        try {

            // starts the daemon regardless whether is recurring execution or
            // not. As the listener socket is useful for the one-off execution
            // during the execution. Note: the daemon timer will not started for
            // one-off execution.
            startDaemon();

            submitJobs();

            if (!settings().daemon() && (settings().needToDeleteAssets() || settings().needToDeleteFiles())) {
                // waiting until the threadpools are clear. This is
                // required for sync jobs.
                while (_queriers.getActiveCount() > 0 || !_queriers.getQueue().isEmpty()
                        || _workers.getActiveCount() > 0 || !_workers.getQueue().isEmpty()) {
                    Thread.sleep(1000);
                }
                if (settings().deleteAssets()) {
                    syncDeleteAssets();
                }
                if (settings().deleteFiles()) {
                    syncDeleteFiles();
                }
            }
        } catch (Throwable e) {
            logger().log(Level.SEVERE, e.getMessage(), e);
        } finally {
            if (!settings().daemon()) {
                // shutdown the thread pools and wait until they complete
                shutdown(true);
                // stop the daemon listener (socket). The daemon timer was not
                // started.
                stopDaemon();
                // post execution procedure to handle the results etc.
                postExecute();
            }
        }
    }

    public void startDaemon() throws Throwable {
        if (settings().daemon()) {
            // Recurring execution requires a timer.
            if (_daemonTimer == null) {
                _daemonTimer = new Timer();
                _daemonTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        if (!settings().hasJobs() || settings().hasOnlyCheckJobs()) {
                            logger().info("No transfer jobs found! Stopping...");
                            interrupt();
                        }
                        if (_queriers.getActiveCount() == 0 && _queriers.getQueue().isEmpty()
                                && _workers.getActiveCount() == 0 && _workers.getQueue().isEmpty()) {
                            try {
                                if (settings().deleteAssets()) {
                                    syncDeleteAssets();
                                }
                                if (settings().deleteFiles()) {
                                    syncDeleteFiles();
                                }
                                // waiting until the thread pools are clear.
                                // This is required for sync jobs, and the
                                // situation, where there are both download and
                                // upload jobs
                                while (_queriers.getActiveCount() > 0 || !_queriers.getQueue().isEmpty()
                                        || _workers.getActiveCount() > 0 || !_workers.getQueue().isEmpty()) {
                                    // wait until threadpools are clear.
                                    Thread.sleep(1000);
                                }
                                submitJobs();
                            } catch (Throwable e) {
                                logger().log(Level.SEVERE, e.getMessage(), e);
                            }
                        }
                    }
                }, settings().daemonScanInterval(), settings().daemonScanInterval());
            }
        }
        if (_daemonListener == null) {
            // Both recurring execution and one-off execution can benefit from
            // the listener.
            _daemonListener = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        _daemonListenerSocket = new ServerSocket(_settings.daemonListenerPort(), 0,
                                InetAddress.getByName(null));
                        try {
                            outerloop: while (!Thread.interrupted() && !_daemonListenerSocket.isClosed()) {
                                Socket client = _daemonListenerSocket.accept();
                                try {
                                    BufferedReader in = new BufferedReader(
                                            new InputStreamReader(client.getInputStream()));
                                    while (!Thread.interrupted()) {
                                        String cmd = in.readLine();
                                        if ("stop".equalsIgnoreCase(cmd)) {
                                            interrupt();
                                            break outerloop;
                                        } else if ("status".equalsIgnoreCase(cmd)) {
                                            printSummary(new PrintStream(client.getOutputStream(), true));
                                            break;
                                        } else {
                                            break;
                                        }
                                    }
                                } finally {
                                    client.close();
                                }
                            }
                        } catch (SocketException se) {
                            if (settings().verbose()) {
                                logger().info("Listening socket closed!");
                            }
                        } finally {
                            _daemonListenerSocket.close();
                        }
                    } catch (Throwable e) {
                        logger().log(Level.SEVERE, e.getMessage(), e);
                    }
                }

            }, applicationName().toLowerCase() + ".daemon.listener");
            _daemonListener.start();
        }
    }

    protected void printSummary(PrintStream ps) {
        long durationMillis = System.currentTimeMillis() - _execStartTime;
        List<Job> jobs = settings().jobs();
        if (jobs != null && !jobs.isEmpty()) {
            ps.println();
            if (settings().hasSyncJobs()) {
                ps.println("Sync:");
                for (Job job : jobs) {
                    if (job.action() == Action.SYNC) {
                        ps.println("    src(directory):" + job.directory().toString());
                        ps.println("    dst(mediaflux):" + job.namespace());
                    }
                }
            }
            if (settings().hasDownloadJobs()) {
                ps.println("Download:");
                for (Job job : jobs) {
                    if (job.action() == Action.DOWNLOAD) {
                        ps.println("    src(mediaflux):" + job.namespace());
                        ps.println("    dst(directory):" + job.directory().toString());
                    }
                }
            }
            if (settings().hasUploadJobs()) {
                ps.println("Upload:");
                for (Job job : jobs) {
                    if (job.action() == Action.UPLOAD) {
                        ps.println("    src(directory):" + job.directory().toString());
                        ps.println("    dst(mediaflux):" + job.namespace());
                    }
                }
            }
            ps.println();
        }
        ps.println();
        ps.println("Summary:");
        if (_settings.daemon()) {
            ps.println(String.format("           Up time: %s", TimeUtils.humanReadableDuration(durationMillis)));
        } else {
            ps.println(String.format("         Exec time: %s", TimeUtils.humanReadableDuration(durationMillis)));
        }
        ps.println();
        int totalFiles = _nbUploadedFiles.get() + _nbSkippedFiles.get() + _nbFailedFiles.get();
        if (totalFiles > 0) {
            ps.println(String.format("    Uploaded files: %,32d files", _nbUploadedFiles.get()));
            ps.println(String.format("     Skipped files: %,32d files", _nbSkippedFiles.get()));
            ps.println(String.format("      Failed files: %,32d files", _nbFailedFiles.get()));
            ps.println(String.format("    Uploaded bytes: %,32d bytes", _nbUploadedBytes.get()));
            if (!_settings.daemon()) {
                // @formatter:off
                ps.println(String.format("      Upload speed: %,32.3f MB/s", (double) _nbUploadedBytes.get() / 1000.0 / ((double)durationMillis)));
                // @formatter:on
            }
            ps.println();
        }
        int deletedAssets = _nbDeletedAssets.get();
        if (deletedAssets > 0) {
            ps.println(String.format("    Deleted Assets: %,32d assets", _nbDeletedAssets.get()));
            ps.println();
        }
        int totalAssets = _nbDownloadedAssets.get() + _nbSkippedAssets.get() + _nbFailedAssets.get();
        if (totalAssets > 0) {
            ps.println(String.format(" Downloaded assets: %,32d files", _nbDownloadedAssets.get()));
            ps.println(String.format("    Skipped assets: %,32d files", _nbSkippedAssets.get()));
            ps.println(String.format("     Failed assets: %,32d files", _nbFailedAssets.get()));
            ps.println(String.format(" Downloaded  bytes: %,32d bytes", _nbDownloadedBytes.get()));
            if (!_settings.daemon()) {
                // @formatter:off
                ps.println(String.format("    Download speed: %,32.3f MB/s", (double) _nbDownloadedBytes.get() / 1000.0 / ((double)durationMillis)));
                // @formatter:on
            }
            ps.println();
        }
        int deletedFiles = _nbDeletedFiles.get();
        if (deletedFiles > 0) {
            ps.println(String.format("     Deleted files: %,32d files", _nbDeletedFiles.get()));
            ps.println();
        }
    }

    public void stopDaemon() {
        if (_daemonTimer != null) {
            _daemonTimer.cancel();
            _daemonTimer = null;
        }
        if (_daemonListener != null) {
            try {
                if (_daemonListenerSocket != null) {
                    _daemonListenerSocket.close();
                }
            } catch (IOException e) {
                logger().log(Level.SEVERE, e.getMessage(), e);
            } finally {
                _daemonListener.interrupt();
            }
        }
    }

    private void submitJobs() throws Throwable {
        if (_stime != null) {
            _stimeLast = _stime;
        }
        _stime = session().execute("server.clock.time").longValue("stime", null);
        List<Job> jobs = settings().jobs();
        if (jobs != null) {
            for (Job job : jobs) {
                switch (job.action()) {
                case DOWNLOAD:
                    submitDownloadJob(job);
                    break;
                case UPLOAD:
                    submitUploadJob(job);
                    break;
                case SYNC:
                    submitDownloadJob(job);
                    submitUploadJob(job);
                    break;
                case CHECK_DOWNLOAD:
                    submitDownloadCheckJob(job);
                    break;
                case CHECK_UPLOAD:
                    submitUploadCheckJob(job);
                    break;
                case CHECK_SYNC:
                    submitDownloadCheckJob(job);
                    submitUploadCheckJob(job);
                    break;
                default:
                    break;
                }
            }
        }
    }

    private void submitDownloadCheckJob(Job job) throws Throwable {
        int idx = 1;
        boolean completed = false;
        do {
            XmlStringWriter w = new XmlStringWriter();
            w.add("where", "namespace>='" + job.namespace() + "' and asset has content");
            w.add("action", "get-value");
            w.add("count", true);
            w.add("size", settings().batchSize());
            w.add("idx", idx);
            w.add("xpath", new String[] { "ename", "path" },
                    "string.format('%s/%s', xvalue('namespace'), choose(equals(xvalue('name'),null()), string.format('__asset_id__%s',xvalue('@id')),xvalue('name')))");
            w.add("xpath", new String[] { "ename", "csize" }, "content/size");
            w.add("xpath", new String[] { "ename", "csum" }, "content/csum");
            w.add("xpath", new String[] { "ename", "mtime" }, "meta/" + PosixAttributes.DOC_TYPE + "/mtime");
            XmlDoc.Element re = session().execute("asset.query", w.document());
            List<XmlDoc.Element> aes = re.elements("asset");
            if (aes != null && !aes.isEmpty()) {
                List<AssetItem> ais = new ArrayList<AssetItem>(aes.size());
                for (XmlDoc.Element ae : aes) {
                    String assetPath = ae.value("path");
                    long assetContentSize = ae.longValue("csize", -1);
                    String assetCsum = ae.value("csum");
                    AssetItem ai = new AssetItem(assetPath, job.namespace(), assetContentSize, assetCsum,
                            ChecksumType.CRC32);
                    ais.add(ai);
                }
                _queriers.submit(new AssetSetCheckTask(session(), logger(), ais, job, settings().csumCheck(),
                        settings().checkHandler(), _workers));
            }
            completed = re.longValue("cursor/remaining") == 0;
            idx += settings().batchSize();
        } while (!completed && !Thread.interrupted());
    }

    private void submitUploadCheckJob(Job job) throws Throwable {
        List<Path> files = new ArrayList<Path>(settings().batchSize());
        Files.walkFileTree(job.directory(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                try {
                    if (job.sourcePathMatches(file)) {
                        files.add(file);
                        if (files.size() >= settings().batchSize()) {
                            // check files
                            _queriers.submit(new FileSetCheckTask(session(), logger(), new ArrayList<Path>(files), job,
                                    settings().csumCheck(), settings().checkHandler(), _workers));
                            files.clear();
                        }
                    }
                } catch (Throwable e) {
                    if (e instanceof InterruptedException) {
                        Thread.currentThread().interrupt();
                        return FileVisitResult.TERMINATE;
                    }
                    logger().log(Level.SEVERE, e.getMessage(), e);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException ioe) {
                logger().log(Level.SEVERE, "Failed to access file: " + file, ioe);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException ioe) {
                if (ioe != null) {
                    logger().log(Level.SEVERE, ioe.getMessage(), ioe);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return super.preVisitDirectory(dir, attrs);
            }
        });
        if (!files.isEmpty()) {
            // check files
            _queriers.submit(new FileSetCheckTask(session(), logger(), new ArrayList<Path>(files), job,
                    settings().csumCheck(), settings().checkHandler(), _workers));
            files.clear();
        }
    }

    private void submitDownloadJob(Job job) throws Throwable {
        int idx = 1;
        boolean completed = false;
        do {
            XmlStringWriter w = new XmlStringWriter();
            String where = "namespace>='" + job.namespace() + "' and asset has content";
            if (_stimeLast != null) {
                where += " and stime>" + _stimeLast;
            }
            w.add("where", where);
            w.add("action", "get-value");
            w.add("count", true);
            w.add("size", settings().batchSize());
            w.add("idx", idx);
            w.add("xpath", new String[] { "ename", "path" },
                    "string.format('%s/%s', xvalue('namespace'), choose(equals(xvalue('name'),null()), string.format('__asset_id__%s',xvalue('@id')),xvalue('name')))");
            w.add("xpath", new String[] { "ename", "csize" }, "content/size");
            w.add("xpath", new String[] { "ename", "csum" }, "content/csum");
            w.add("xpath", new String[] { "ename", "mtime" }, "meta/" + PosixAttributes.DOC_TYPE + "/mtime");
            XmlDoc.Element re = session().execute("asset.query", w.document());
            List<XmlDoc.Element> aes = re.elements("asset");
            if (aes != null) {
                for (XmlDoc.Element ae : aes) {
                    String assetId = ae.value("@id");
                    String assetPath = ae.value("path");
                    long assetContentSize = ae.longValue("csize", -1);
                    if (assetContentSize < 0) {
                        _nbSkippedAssets.getAndIncrement();
                        logger().info("Skipped asset " + assetId + ": '" + assetPath + "' No asset content found.");
                        continue;
                    }
                    Path file = job.computeFilePath(assetPath);
                    if (Files.exists(file)) {
                        if (settings().overwrite()) {
                            PosixAttributes fileAttrs = PosixAttributes.read(file);
                            long fileSize = Files.size(file);
                            long assetPosixMTime = ae.longValue("mtime", -1);
                            if (fileSize == assetContentSize && assetPosixMTime > 0
                                    && assetPosixMTime == fileAttrs.mtime()) {
                                _nbSkippedAssets.getAndIncrement();
                                logger().info("Skipped asset " + assetId + ": '" + assetPath
                                        + "' Already exists. Both file sizes and mtimes match.");
                                continue;
                            }
                        } else {
                            _nbSkippedAssets.getAndIncrement();
                            logger().info(
                                    "Skipped asset " + assetId + ": '" + assetPath + "' Already exists. Skipped.");
                            continue;
                        }
                    }
                    _workers.submit(
                            new AssetDownloadTask(session(), logger(), assetPath, file, settings().unarchive(), _dl));
                }
            }
            idx += settings().batchSize();
            completed = re.longValue("cursor/remaining") == 0;
        } while (!completed && !Thread.interrupted());
    }

    private void submitUploadJob(Job job) throws Throwable {

        List<Path> files = new ArrayList<Path>(settings().batchSize());
        Files.walkFileTree(job.directory(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                try {
                    if (job.sourcePathMatches(file)) {
                        files.add(file);
                        if (files.size() >= settings().batchSize()) {
                            _queriers.submit(new FileSetUploadTask(session(), logger(), new ArrayList<Path>(files), job,
                                    settings().csumCheck(), settings().retry(), _ul, _workers));
                            files.clear();
                        }
                    }
                } catch (Throwable e) {
                    if (e instanceof InterruptedException) {
                        Thread.currentThread().interrupt();
                        return FileVisitResult.TERMINATE;
                    }
                    logger().log(Level.SEVERE, e.getMessage(), e);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException ioe) {
                logger().log(Level.SEVERE, "Failed to access file: " + file, ioe);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException ioe) {
                if (ioe != null) {
                    logger().log(Level.SEVERE, ioe.getMessage(), ioe);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return super.preVisitDirectory(dir, attrs);
            }
        });
        if (!files.isEmpty()) {
            _queriers.submit(new FileSetUploadTask(session(), logger(), new ArrayList<Path>(files), job,
                    settings().csumCheck(), settings().retry(), _ul, _workers));
            files.clear();
        }
    }

    private void syncDeleteAssets() throws Throwable {
        List<Job> jobs = settings().jobs();
        for (Job job : jobs) {
            if (job.action() == Action.UPLOAD || job.action() == Action.SYNC) {
                _queriers.submit(new SyncDeleteAssetsTask(session(), logger(), job, settings(), _nbDeletedAssets));
            }
        }
    }

    private void syncDeleteFiles() throws Throwable {
        List<Job> jobs = settings().jobs();
        for (Job job : jobs) {
            if (job.action() == Action.DOWNLOAD || job.action() == Action.SYNC) {
                _queriers.submit(
                        new SyncDeleteFilesTask(session(), logger(), job, settings(), _workers, _nbDeletedFiles));
            }
        }
    }

    public void interrupt() {
        if (_queriers != null && !_queriers.isShutdown()) {
            _queriers.shutdownNow();
        }
        if (_workers != null && !_workers.isShutdown()) {
            _workers.shutdownNow();
        }
        stopDaemon();
        session().stopPingServerPeriodically();
        session().discard();
    }

    public void shutdown(boolean wait) {
        try {
            if (_queriers != null) {
                if (!_queriers.isShutdown()) {
                    // now all jobs have been submitted
                    _queriers.shutdown();
                }
                if (wait) {
                    // wait until all tasks are submitted by queriers.
                    _queriers.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
                }
            }

            if (_workers != null) {
                if (!_workers.isShutdown()) {
                    // now all tasks have been submitted
                    _workers.shutdown();
                }
                if (wait) {
                    // wait until all tasks are processed by workers.
                    _workers.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger().info("Interrupted '" + Thread.currentThread().getName() + "' thread(id="
                    + Thread.currentThread().getId() + ").");
        } catch (Throwable e) {
            logger().log(Level.SEVERE, e.getMessage(), e);
        }
    }

    protected void postExecute() {
        if (!settings().hasOnlyCheckJobs()) {
            printSummary(System.out);
            notifySummary();
        }
    }

    @Override
    public unimelb.mf.client.sync.settings.Settings settings() {
        return _settings;
    }

    public void notifySummary() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        try {
            printSummary(ps);
            String summary = baos.toString();
            String subject = applicationName() + " results [" + new Date() + "]";
            notify(subject, summary);
        } catch (Throwable e) {
            logger().log(Level.SEVERE, e.getMessage(), e);
        } finally {
            ps.close();
        }
    }

    public void notify(String subject, String message) throws Throwable {
        if (settings().hasRecipients()) {
            MailUtils.sendMail(session(), settings().recipients(), subject, message, true);
        }
    }

}
