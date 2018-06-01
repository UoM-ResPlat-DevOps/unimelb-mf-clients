package unimelb.mf.client.sync.task;

import java.nio.file.Path;
import java.util.logging.Logger;

import unimelb.mf.client.session.MFSession;
import unimelb.mf.client.task.AbstractMFTask;
import unimelb.mf.client.util.ChecksumUtils;
import unimelb.mf.client.util.ChecksumUtils.ChecksumType;

public class FileCsumCalcTask extends AbstractMFTask {

    public static interface ChecksumHandler {
        void processChecksum(Path file, String checksum, ChecksumType checksumType);
    }

    private Path _file;
    private ChecksumType _csumType;
    private ChecksumHandler _csumHandler;

    protected FileCsumCalcTask(MFSession session, Logger logger, Path file, ChecksumType csumType,
            ChecksumHandler csumHandler) {
        super(session, logger);
        _file = file;
        _csumType = csumType;
    }

    @Override
    public void execute() throws Throwable {
        String csum = ChecksumUtils.get(_file.toFile(), _csumType);
        _csumHandler.processChecksum(_file, csum, _csumType);
    }

}
