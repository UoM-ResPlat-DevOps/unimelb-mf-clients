package unimelb.mf.client.task;

import java.util.logging.Logger;

import arc.utils.CanAbort;
import unimelb.mf.client.session.MFSession;
import unimelb.mf.client.util.LoggingUtils;

public abstract class AbstractMFTask implements MFTask {

    private MFSession _session;
    private Logger _logger;
    private CanAbort _ca;
    private long _totalOps = 0L;
    private long _completedOps = 0L;
    private String _currentOp = null;

    protected AbstractMFTask(MFSession session, Logger logger) {
        _session = session;
        _logger = logger == null ? LoggingUtils.createConsoleLogger() : logger;
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
    public void setAbortableOperation(CanAbort ca) {
        _ca = ca;
    }

    @Override
    public CanAbort abortableOperation() {
        return _ca;
    }

    @Override
    public long totalOperations() {
        return _totalOps;
    }

    protected void setTotalOperations(long totalOps) {
        _totalOps = totalOps;
    }

    @Override
    public long completedOperations() {
        return _completedOps;
    }

    protected void setCompletedOperations(long completedOps) {
        _completedOps = completedOps;
    }

    protected void incCompletedOperations(int increment) {
        _completedOps += increment;
    }

    protected void incCompletedOperations() {
        _completedOps++;
    }

    @Override
    public String currentOperation() {
        return _currentOp;
    }

    protected void setCurrentOperation(String currentOperation) {
        _currentOp = currentOperation;
    }

}
