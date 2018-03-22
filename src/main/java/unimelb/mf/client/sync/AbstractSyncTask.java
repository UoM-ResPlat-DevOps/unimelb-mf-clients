package unimelb.mf.client.sync;

import unimelb.mf.client.task.AbstractMFTask;

public abstract class AbstractSyncTask extends AbstractMFTask {

    protected AbstractSyncTask(SyncApplication app) {
        super(app.session(), app.logger());
    }

}
