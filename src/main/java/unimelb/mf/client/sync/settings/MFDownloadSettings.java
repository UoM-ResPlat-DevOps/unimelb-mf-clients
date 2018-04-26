package unimelb.mf.client.sync.settings;

import unimelb.mf.client.sync.task.AssetDownloadTask;

public class MFDownloadSettings extends Settings {

    private boolean _overwrite = false;
    private AssetDownloadTask.Unarchive _unarchive = AssetDownloadTask.Unarchive.NONE;

    public MFDownloadSettings() {
        super(Action.TRANSFER, Direction.DOWNLOAD, null);
    }

    public boolean overwrite() {
        return _overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        _overwrite = overwrite;
    }

    public AssetDownloadTask.Unarchive unarchive() {
        return _unarchive;
    }

    public void setUnarchive(AssetDownloadTask.Unarchive unarchive) {
        _unarchive = unarchive;
    }

}
