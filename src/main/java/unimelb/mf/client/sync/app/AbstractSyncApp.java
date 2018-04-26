package unimelb.mf.client.sync.app;

import java.util.logging.Logger;

import unimelb.mf.client.session.MFSession;
import unimelb.mf.client.sync.settings.Settings;

public abstract class AbstractSyncApp<T extends Settings> implements SyncApp<T> {

    private MFSession _session;
    private T _settings;
    private Logger _logger;

    protected AbstractSyncApp(T settings) {
        _settings = settings;
        assert _settings != null;
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
    public T settings() {
        return _settings;
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
