package unimelb.mf.client.sync.task;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import unimelb.mf.client.session.MFSession;
import unimelb.mf.client.sync.SyncTask;
import unimelb.mf.client.sync.app.SyncApp;

public class FileAssetCheckTask extends SyncTask {

    private Map<Path, String> _fileAssetMap;

    protected FileAssetCheckTask(SyncApp app, Map<Path,String> fileAssetPath) {
        super(app);
        _fileAssetMap = new LinkedHashMap<Path,String>();
        _fileAssetMap.putAll(fileAssetPath);
    }

    @Override
    public void execute(MFSession session) throws Throwable {
        // TODO Auto-generated method stub

    }

}
