package unimelb.mf.client.sync.task;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import arc.xml.XmlDoc;
import arc.xml.XmlStringWriter;
import unimelb.mf.client.session.MFSession;
import unimelb.mf.client.sync.settings.Job;
import unimelb.mf.client.sync.settings.Settings;
import unimelb.mf.client.task.AbstractMFTask;

public class SyncDeleteFilesTask extends AbstractMFTask {

    public static final int DEFAULT_BATCH_SIZE = 1000;

    private Path _dir;
    private String _ns;
    private int _batchSize = DEFAULT_BATCH_SIZE;
    private boolean _verbose = true;
    private ExecutorService _workers;
    private AtomicLong _counter;

    protected SyncDeleteFilesTask(MFSession session, Logger logger, String ns, Path dir, int batchSize,
            ExecutorService workers, boolean verbose, AtomicLong counter) {
        super(session, logger);
        _ns = ns;
        _dir = dir;
        _batchSize = batchSize;
        _workers = workers;
        _verbose = verbose;
        _counter = counter;
    }

    public SyncDeleteFilesTask(MFSession session, Logger logger, Job job, Settings settings, ExecutorService workers,
            AtomicLong counter) {
        this(session, logger, job.namespace(), job.directory(), settings.batchSize(), workers, settings.verbose(),
                counter);
    }

    @Override
    public void execute() throws Throwable {
        if (_verbose) {
            logger().info("checking files in directory: '" + _dir.toString() + "'");
        }
        List<Path> files = new ArrayList<Path>(_batchSize);
        Files.walkFileTree(_dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                try {
                    files.add(file);
                    if (files.size() >= _batchSize) {
                        // check files
                        final List<Path> toCheck = new ArrayList<Path>(files);
                        _workers.submit(new Callable<Void>() {

                            @Override
                            public Void call() throws Exception {
                                checkAndDeleteFiles(toCheck);
                                return null;
                            }
                        });
                        files.clear();
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
            _workers.submit(new Callable<Void>() {

                @Override
                public Void call() throws Exception {
                    checkAndDeleteFiles(files);
                    return null;
                }
            });
            files.clear();
        }
    }

    private void checkAndDeleteFiles(List<Path> files) throws Exception {
        try {
            XmlStringWriter w1 = new XmlStringWriter();
            for (Path file : files) {
                String assetPath = Job.computeAssetPath(file, _dir, _ns);
                w1.add("id", "path=" + assetPath);
            }
            List<XmlDoc.Element> ees = session().execute("asset.exists", w1.document()).elements("exists");
            if (ees != null) {
                for (int i = 0; i < ees.size(); i++) {
                    Path file = files.get(i);
                    boolean exists = ees.get(i).booleanValue();
                    if (!exists) {
                        logger().info("deleting file: '" + file.toString() + "'");
                        if (Files.exists(file)) {
                            Files.delete(file);
                        }
                        if (_counter != null) {
                            _counter.getAndIncrement();
                        }
                    }
                }
            }
        } catch (Throwable e) {
            if (e instanceof Exception) {
                throw (Exception) e;
            } else {
                throw new Exception(e);
            }
        }
    }

}
