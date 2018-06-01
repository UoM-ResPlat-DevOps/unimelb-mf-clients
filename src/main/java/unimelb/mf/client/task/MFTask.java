package unimelb.mf.client.task;

import java.util.concurrent.Callable;

import unimelb.mf.client.session.HasMFSession;
import unimelb.mf.client.util.HasAbortableOperation;
import unimelb.mf.client.util.HasProgress;
import unimelb.mf.client.util.Loggable;

public interface MFTask extends Callable<Void>, Loggable, HasMFSession, HasAbortableOperation, HasProgress {

    void execute() throws Throwable;
}
