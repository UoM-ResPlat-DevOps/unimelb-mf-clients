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
import unimelb.mf.client.sync.check.AssetItem;
import unimelb.mf.client.sync.check.CheckHandler;
import unimelb.mf.client.sync.check.CheckResult;
import unimelb.mf.client.sync.settings.Job;
import unimelb.mf.client.task.AbstractMFTask;
import unimelb.mf.client.util.ChecksumUtils.ChecksumType;

public class FileSetCheckTask extends AbstractMFTask {

    private Job _job;
    private Map<Path, String> _fileAssets;
    private Map<String, Path> _assetFiles;
    private boolean _csumCheck = false;
    private CheckHandler _rh;
    private ExecutorService _workers;

    public FileSetCheckTask(MFSession session, Logger logger, List<Path> files, Job job, boolean csumCheck,
            CheckHandler rh, ExecutorService workers) {
        super(session, logger);
        _job = job;
        _workers = workers;
        _csumCheck = csumCheck;
        _rh = rh;
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
                _rh.checked(new CheckResult(_assetFiles.get(assetPath), assetPath, false, false, false,
                        _csumCheck ? false : null));
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

                String contentCsum = ae.value("content/csum[@base='16']");

                AssetItem ai = new AssetItem(ae, _job.namespace());

                Path file = _assetFiles.get(assetPath);

                if (contentSize == null || contentCsum == null) {
                    _rh.checked(new CheckResult(ai, file, false, false, false, _csumCheck ? false : null));
                    incCompletedOperations();
                    return;
                }

                if (_csumCheck) {
                    final boolean fileSizesMatch = contentSize == Files.size(file);
                    _workers.submit(new FileCsumCalcTask(session(), logger(), file, ChecksumType.CRC32,
                            new FileCsumCalcTask.ChecksumHandler() {

                                @Override
                                public void processChecksum(Path file, String checksum, ChecksumType checksumType) {
                                    _rh.checked(new CheckResult(ai, file, true, true, fileSizesMatch,
                                            contentCsum.equalsIgnoreCase(checksum)));
                                }
                            }));
                } else {
                    _rh.checked(new CheckResult(ai, file, true, true, contentSize == Files.size(file), null));
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
