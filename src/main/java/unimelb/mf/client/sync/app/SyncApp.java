package unimelb.mf.client.sync.app;

import java.util.logging.Logger;

import unimelb.mf.client.session.HasMFSession;
import unimelb.mf.client.session.MFSession;
import unimelb.mf.client.sync.settings.Settings;
import unimelb.mf.client.util.HasLogger;

public interface SyncApp<T extends Settings> extends HasLogger, HasMFSession {

    String applicationName();

    String description();

    T settings();

    void setLogger(Logger logger);

    void setSession(MFSession session);

    void execute(MFSession session, T settings) throws Throwable;

}
