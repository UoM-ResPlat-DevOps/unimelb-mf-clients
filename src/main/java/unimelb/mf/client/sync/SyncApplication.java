package unimelb.mf.client.sync;

import unimelb.mf.client.session.HasMFSession;
import unimelb.mf.client.util.HasLogger;

public interface SyncApplication extends HasLogger, HasMFSession {

    String applicationName();
    
}
