package unimelb.mf.client.sync.task;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import arc.xml.XmlDoc;
import arc.xml.XmlStringWriter;
import unimelb.mf.client.session.MFSession;
import unimelb.mf.client.sync.settings.Job;
import unimelb.mf.client.sync.settings.Settings;
import unimelb.mf.client.task.AbstractMFTask;

public class SyncDeleteAssetsTask extends AbstractMFTask {

    public static final int DEFAULT_BATCH_SIZE = 1000;

    private Path _dir;
    private String _ns;
    private boolean _hardDestroy = false;
    private int _batchSize = DEFAULT_BATCH_SIZE;
    private boolean _verbose = true;
    private AtomicLong _counter;

    public SyncDeleteAssetsTask(MFSession session, Logger logger, Path dir, String ns, boolean hardDestroy,
            int batchSize, boolean verbose, AtomicLong counter) {
        super(session, logger);
        _dir = dir;
        _ns = ns;
        _hardDestroy = hardDestroy;
        _batchSize = batchSize;
        _verbose = verbose;
        _counter = counter;
    }

    public SyncDeleteAssetsTask(MFSession session, Logger logger, Job job, Settings settings, AtomicLong counter) {
        this(session, logger, job.directory(), job.namespace(), settings.hardDestroy(), settings.batchSize(),
                settings.verbose(), counter);
    }

    @Override
    public void execute() throws Throwable {
        int idx = 1;
        boolean completed = false;
        do {
            XmlStringWriter w = new XmlStringWriter();
            w.add("where", "namespace>='" + _ns + "' and asset has content");
            w.add("action", "get-path");
            w.add("count", true);
            w.add("size", _batchSize);
            w.add("idx", idx);
            XmlDoc.Element re = session().execute("asset.query", w.document());
            List<XmlDoc.Element> pes = re.elements("path");
            if (pes != null && !pes.isEmpty()) {
                Set<String> toDestroy = new LinkedHashSet<String>();
                for (XmlDoc.Element pe : pes) {
                    String assetPath = pe.value();
                    String assetId = pe.value("@id");
                    Path file = Job.computeFilePath(assetPath, _ns, _dir);
                    if (!Files.exists(file)) {
                        toDestroy.add(assetId);
                        if (_verbose) {
                            logger().info("submitted asset: '" + assetPath + "' to destroy");
                        }
                    }
                }
                if (!toDestroy.isEmpty()) {
                    destroyAssets(toDestroy);
                    if (_counter != null) {
                        _counter.getAndAdd(toDestroy.size());
                    }
                    if (_verbose) {
                        logger().info("deleted " + toDestroy.size() + " assets...");
                    }
                    idx -= toDestroy.size();
                }
            }
            idx += _batchSize;
            completed = re.longValue("cursor/remaining") == 0;
        } while (!completed && !Thread.interrupted());
    }

    private void destroyAssets(Collection<String> assetIds) throws Throwable {
        XmlStringWriter w1 = new XmlStringWriter();
        w1.add("members", false);
        for (String assetId : assetIds) {
            w1.add("id", assetId);
        }
        String service = _hardDestroy ? "asset.hard.destroy" : "asset.soft.destroy";
        session().execute(service, w1.document());
    }
}
