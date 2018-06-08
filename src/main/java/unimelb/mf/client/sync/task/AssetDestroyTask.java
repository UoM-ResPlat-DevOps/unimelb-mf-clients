package unimelb.mf.client.sync.task;

import java.util.Set;
import java.util.logging.Logger;

import unimelb.mf.client.session.MFSession;
import unimelb.mf.client.task.AbstractMFTask;

public class AssetDestroyTask extends AbstractMFTask {

    private Set<String> _assetPaths;
    private boolean _softDestroy = true;

    protected AssetDestroyTask(MFSession session, Logger logger) {
        super(session, logger);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void execute() throws Throwable {
        // TODO Auto-generated method stub

    }

}
