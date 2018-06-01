package unimelb.mf.client.task;

import java.util.logging.Logger;

import unimelb.mf.client.session.MFSession;

public abstract class AbstractMFApp<T extends MFApp.Settings> implements MFApp<T> {

    private MFSession _session;
    private Logger _logger;

    protected AbstractMFApp() {

    }

    @Override
    public Logger logger() {
        return _logger;
    }

    @Override
    public MFSession session() {
        return _session;
    }

    @Override
    public void setLogger(Logger logger) {
        _logger = logger;
    }

    @Override
    public void setSession(MFSession session) {
        _session = session;
    }

}
