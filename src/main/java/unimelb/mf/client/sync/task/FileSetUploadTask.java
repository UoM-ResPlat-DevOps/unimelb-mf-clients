package unimelb.mf.client.sync.task;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

import arc.xml.XmlDoc;
import arc.xml.XmlStringWriter;
import unimelb.mf.client.session.MFSession;
import unimelb.mf.client.sync.settings.Job;
import unimelb.mf.client.task.AbstractMFTask;

public class FileSetUploadTask extends AbstractMFTask {

    private Map<Path, String> _fileAssets;
    private Map<String, Path> _assetFiles;
    private boolean _csumCheck = false;
    private int _retry = 0;
    private ExecutorService _workers;
    private DataTransferListener<Path, String> _ul;

    public FileSetUploadTask(MFSession session, Logger logger, List<Path> files, Job job, boolean csumCheck, int retry,
            DataTransferListener<Path, String> ul, ExecutorService workers) {
        super(session, logger);
        _workers = workers;
        _csumCheck = csumCheck;
        _retry = retry;
        _ul = ul;
        _fileAssets = new LinkedHashMap<Path, String>();
        _assetFiles = new LinkedHashMap<String, Path>();

        if (files != null) {
            for (Path file : files) {
                String assetPath = job.computeAssetPath(file);
                _fileAssets.put(file, assetPath);
                _assetFiles.put(assetPath, file);
            }
        }
    }

    @Override
    public void execute() throws Throwable {
        if (_assetFiles.isEmpty()) {
            return;
        }
        setTotalOperations(_assetFiles.size());
        logger().info("Checking " + _assetFiles.size() + " files...");
        XmlStringWriter w1 = new XmlStringWriter();
        Collection<String> assetPaths = _fileAssets.values();
        for (String assetPath : assetPaths) {
            w1.add("id", "path=" + assetPath);
        }
        List<XmlDoc.Element> ees = session().execute("asset.exists", w1.document()).elements("exists");

        int nbExists = 0;
        XmlStringWriter w2 = new XmlStringWriter();
        for (XmlDoc.Element ee : ees) {
            String assetPath = ee.value("@id").replaceFirst("^path=", "");
            boolean exists = ee.booleanValue();
            if (!exists) {
                _workers.submit(new FileUploadTask(session(), logger(), _assetFiles.get(assetPath), assetPath,
                        _csumCheck, _retry, _ul));
                incCompletedOperations();
            } else {
                w2.add("id", "path=" + assetPath);
                nbExists++;
            }
        }
        if (nbExists > 0) {
            List<XmlDoc.Element> aes = session().execute("asset.get", w2.document()).elements("asset");
            for (XmlDoc.Element ae : aes) {

                String assetPath = ae.value("path");

                Long contentSize = ae.longValue("content/size", null);

                Path file = _assetFiles.get(assetPath);

                boolean fileSizesMatch = contentSize == null ? false : (contentSize == Files.size(file));

                if (_csumCheck || !fileSizesMatch) {
                    _workers.submit(new FileUploadTask(session(), logger(), file, assetPath, _csumCheck, _retry, _ul));
                } else {
                    if (_ul != null) {
                        _ul.transferSkipped(file, assetPath);
                    }
                    logger().info("Asset " + assetPath + " already exists.");
                }
                incCompletedOperations();

                // @formatter:off
//                PosixAttributes contentAttrs = ae.elementExists("meta/" + PosixAttributes.DOC_TYPE)
//                        ? new PosixAttributes(ae.element("meta/" + PosixAttributes.DOC_TYPE))
//                        : null;
//                PosixAttributes fileAttrs = PosixAttributes.read(file);
//                if (contentAttrs != null && fileAttrs != null && fileAttrs.mtimeEquals(contentAttrs)) {
                    // 
//                }
                // @formatter:on
            }
        }
    }

}
