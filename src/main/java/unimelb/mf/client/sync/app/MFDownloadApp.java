package unimelb.mf.client.sync.app;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import arc.xml.XmlDoc;
import arc.xml.XmlStringWriter;
import unimelb.mf.client.file.PosixAttributes;
import unimelb.mf.client.session.MFSession;
import unimelb.mf.client.sync.settings.Job;
import unimelb.mf.client.sync.settings.MFDownloadSettings;
import unimelb.mf.client.sync.task.AssetDownloadTask;
import unimelb.mf.client.util.PathUtils;

public class MFDownloadApp extends AbstractSyncApp<MFDownloadSettings> {

    public static final String APP_NAME = "unimelb-mf-download";

    private ThreadPoolExecutor _workerThreads;
    private Thread _queryThread;

    protected MFDownloadApp() {
        super(new MFDownloadSettings());
    }

    public void execute(MFSession session, MFDownloadSettings settings) throws Throwable {
        _workerThreads = new ThreadPoolExecutor(settings().numberOfWorkers(), settings().numberOfWorkers(), 0,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, applicationName() + ".worker");
                    }
                }, new ThreadPoolExecutor.CallerRunsPolicy());
        _queryThread = new Thread(() -> {
            try {
                List<Job> jobs = settings.jobs();
                if (jobs != null) {
                    for (Job job : jobs) {
                        submitJob(session, settings, job);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger().info("Interrupted '" + Thread.currentThread().getName() + "' thread(id="
                        + Thread.currentThread().getId() + ").");
            } catch (Throwable e) {
                logger().log(Level.SEVERE, e.getMessage(), e);
                e.printStackTrace();
            } finally {
                _workerThreads.shutdown();
            }
        }, applicationName() + ".query");
        _queryThread.start();
    }

    private void submitJob(MFSession session, MFDownloadSettings settings, Job job) throws Throwable {
        int idx = 1;
        boolean completed = false;
        do {
            XmlStringWriter w = new XmlStringWriter();
            w.add("where", "namespace>='" + job.namespace() + "'");
            w.add("action", "get-value");
            w.add("size", settings.queryResultPageSize());
            w.add("idx", idx);
            w.add("xpath", new String[] { "ename", "path" },
                    "string.format('%s/%s', xvalue('namespace'), choose(equals(xvalue('name'),null()), string.format('__asset_id__%s',xvalue('@id')),xvalue('name')))");
            w.add("xpath", new String[] { "ename", "csize" }, "content/size");
            w.add("xpath", new String[] { "ename", "csum" }, "content/csum");
            w.add("xpath", new String[] { "ename", "mtime" }, "meta/" + PosixAttributes.DOC_TYPE + "/mtime");
            XmlDoc.Element re = session.execute("asset.query", w.document());
            List<XmlDoc.Element> aes = re.elements("asset");
            if (aes != null) {
                for (XmlDoc.Element ae : aes) {
                    String assetId = ae.value("@id");
                    String assetPath = ae.value("path");
                    long assetContentSize = ae.longValue("csize", -1);
                    if (assetContentSize < 0) {
                        logger().info("Skipped asset " + assetId + ": '" + assetPath + "' No asset content found.");
                        continue;
                    }
                    Path file = job.isDestinationParent()
                            ? Paths.get(PathUtils.toSystemDependent(job.directory().toString()),
                                    PathUtils.getLastComponent(job.namespace()),
                                    PathUtils.getRelativePathSD(job.namespace(), assetPath))
                            : Paths.get(PathUtils.toSystemDependent(job.directory().toString()),
                                    PathUtils.getRelativePathSD(job.namespace(), assetPath));
                    if (Files.exists(file)) {
                        if (settings.overwrite()) {
                            PosixAttributes fileAttrs = PosixAttributes.read(file);
                            long fileSize = Files.size(file);
                            long assetPosixMTime = ae.longValue("mtime", -1);
                            if (fileSize == assetContentSize && assetPosixMTime > 0
                                    && assetPosixMTime == fileAttrs.mtime()) {
                                logger().info("Skipped asset " + assetId + ": '" + assetPath
                                        + "' Already exists. Both file sizes and mtimes match.");
                                continue;
                            }
                        } else {
                            logger().info(
                                    "Skipped asset " + assetId + ": '" + assetPath + "' Already exists. No overwrite.");
                            continue;
                        }
                    }
                    _workerThreads.submit(new AssetDownloadTask(this, assetPath, file));
                }
            }
            completed = re.longValue("cursor/remaining") == 0;
        } while (!completed && !Thread.interrupted());

    }

    public void abort() {
        if (_queryThread != null && !_queryThread.isAlive()) {
            _queryThread.interrupt();
        }
        if (_workerThreads != null && !_workerThreads.isShutdown()) {
            _workerThreads.shutdownNow();
        }
    }

    @Override
    public final String applicationName() {
        return APP_NAME;
    }

    @Override
    public final String description() {
        return "Download files from Mediaflux server to local file system.";
    }

}
