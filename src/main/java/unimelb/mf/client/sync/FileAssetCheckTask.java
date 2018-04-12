package unimelb.mf.client.sync;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import unimelb.mf.client.session.MFSession;

public class FileAssetCheckTask extends AbstractSyncTask {

    private Map<Path, String> _fileAssetMap;

    protected FileAssetCheckTask(SyncApplication app, Map<Path,String> fileAssetPath) {
        super(app);
        _fileAssetMap = new LinkedHashMap<Path,String>();
        _fileAssetMap.putAll(fileAssetPath);
    }

    @Override
    public void execute(MFSession session) throws Throwable {
        // TODO Auto-generated method stub

    }

}
