package unimelb.mf.client.task;

import java.util.concurrent.Callable;

import arc.utils.CanAbort;
import unimelb.mf.client.session.HasMFSession;
import unimelb.mf.client.session.MFSession;
import unimelb.mf.client.util.HasAbortableOperation;
import unimelb.mf.client.util.HasProgress;
import unimelb.mf.client.util.Loggable;

public interface MFTask extends Callable<Void>, Loggable, HasMFSession, HasAbortableOperation, HasProgress {

    default Void call() throws Exception {
        try {
            execute(session());
            return null;
        } catch (Throwable e) {
            logError(e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
                CanAbort ca = abortableOperation();
                if (ca != null) {
                    try {
                        ca.abort();
                    } catch (Throwable e1) {
                        logError("Fail to abort service call.", e1);
                    }
                }
            }
            if (e instanceof Exception) {
                throw (Exception) e;
            } else {
                throw new Exception(e.getMessage(), e);
            }
        }
    }

    void execute(MFSession session) throws Throwable;
}
