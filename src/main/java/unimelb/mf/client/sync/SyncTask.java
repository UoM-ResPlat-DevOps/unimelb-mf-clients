package unimelb.mf.client.sync;

import unimelb.mf.client.sync.app.SyncApp;
import unimelb.mf.client.task.AbstractMFTask;

public abstract class SyncTask extends AbstractMFTask {

    protected SyncTask(SyncApp<?> app) {
        super(app.session(), app.logger());
    }

}
