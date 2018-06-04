package unimelb.mf.client.sync.task;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

import unimelb.mf.client.session.MFSession;
import unimelb.mf.client.sync.check.AssetItem;
import unimelb.mf.client.sync.check.CheckHandler;
import unimelb.mf.client.sync.check.CheckResult;
import unimelb.mf.client.sync.check.ChecksumType;
import unimelb.mf.client.sync.settings.Job;
import unimelb.mf.client.task.AbstractMFTask;
import unimelb.mf.client.util.ChecksumUtils;

public class AssetSetCheckTask extends AbstractMFTask {

    private List<AssetItem> _assets;
    private Job _job;
    private boolean _csumCheck;
    private CheckHandler _rh;
    private ExecutorService _workers;

    public AssetSetCheckTask(MFSession session, Logger logger, List<AssetItem> assets, Job job, boolean csumCheck,
            CheckHandler rh, ExecutorService workers) {
        super(session, logger);
        _assets = assets;
        _job = job;
        _csumCheck = csumCheck;
        _rh = rh;
        _workers = workers;
    }

    @Override
    public void execute() throws Throwable {
        for (AssetItem ai : _assets) {
            Path file = _job.computeFilePath(ai.assetPath());
            if (Files.exists(file)) {
                if (_csumCheck) {
                    long fileSize = Files.size(file);
                    if (fileSize == ai.length()) {
                        String assetCsum = ai.checksum(ChecksumType.CRC32);
                        _workers.submit(new FileCsumCalcTask(session(), logger(), file,
                                ChecksumUtils.ChecksumType.CRC32, new FileCsumCalcTask.ChecksumHandler() {

                                    @Override
                                    public void processChecksum(Path file, String checksum,
                                            ChecksumUtils.ChecksumType checksumType) {
                                        _rh.checked(new CheckResult(ai, file, true, true, true,
                                                assetCsum != null && assetCsum.equalsIgnoreCase(checksum)));
                                    }
                                }));
                    } else {
                        _rh.checked(new CheckResult(ai, file, true, true, false, false));
                    }
                } else {
                    _rh.checked(new CheckResult(ai, file, true, true,
                            ai.length() >= 0 && ai.length() == Files.size(file), null));
                }
            } else {
                _rh.checked(new CheckResult(ai, file, false, false, false, _csumCheck ? false : null));
            }
        }
    }

}
