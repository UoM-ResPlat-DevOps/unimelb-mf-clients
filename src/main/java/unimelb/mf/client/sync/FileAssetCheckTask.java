package unimelb.mf.client.sync;

import java.nio.file.Path;
import java.util.Map;

import unimelb.mf.client.session.MFSession;

public class FileAssetCheckTask extends AbstractSyncTask {

    private Map<Path, String> _fileAssetMap;

    protected FileAssetCheckTask(SyncApplication app) {
        super(app);
    }

    @Override
    public void execute(MFSession session) throws Throwable {
        // TODO Auto-generated method stub

    }

}
