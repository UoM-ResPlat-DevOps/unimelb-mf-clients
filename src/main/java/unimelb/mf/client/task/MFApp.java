package unimelb.mf.client.task;

import java.util.logging.Logger;

import unimelb.mf.client.session.HasMFSession;
import unimelb.mf.client.session.MFSession;
import unimelb.mf.client.util.HasLogger;

public interface MFApp<T extends MFApp.Settings> extends HasLogger, HasMFSession {

    public static interface Settings {

    }

    T settings();

    String applicationName();

    String description();

    void setLogger(Logger logger);

    void setSession(MFSession session);

    void execute() throws Throwable;

}
