package unimelb.mf.model.service;

import java.util.Collection;

import arc.xml.XmlDoc;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlStringWriter;
import unimelb.mf.client.session.MFSession;
import unimelb.mf.client.task.UserOwnedTask;

public class BackgroundService extends UserOwnedTask {

    public static interface StateListener {
        void updated(BackgroundService bs) throws Throwable;
    }

    public static final String TYPE_NAME = "Background Service";

    public static class ExCannotAbort extends Throwable {
        /**
         * 
         */
        private static final long serialVersionUID = -1103667269608909544L;

        public ExCannotAbort(BackgroundService bs) {
            super("Aborting is not supported for background service (id): " + bs.id());
        }
    }

    public static class ExCannotSuspend extends Throwable {
        /**
         * 
         */
        private static final long serialVersionUID = -1947086492494616540L;

        public ExCannotSuspend(BackgroundService bs) {
            super("Suspension is not supported for background service (id): " + bs.id());
        }
    }

    private String _name;
    private String _key;
    private String _description;
    private int _nbOut;
    private XmlDoc.Element _args;
    private long _completed;
    private long _failures;
    private String _activity;
    private String _error;
    private boolean _canAbort;
    private boolean _canSuspend;
    private Collection<String> _initialFailures;

    protected BackgroundService(XmlDoc.Element xe) throws Throwable {
        super(xe);

        _args = xe.element("service");
        _name = _args.value("@name");
        _key = xe.value("key");
        _description = xe.value("description");
        _nbOut = _args.intValue("@outputs", 0);
        _activity = xe.value("activity");
        _completed = xe.longValue("completed", 0);
        _failures = xe.longValue("failed", 0);
        _error = xe.value("error");
        _canAbort = xe.booleanValue("can-abort");
        _canSuspend = xe.booleanValue("can-suspend");
        _initialFailures = xe.values("failure");
    }

    /**
     * Returns the service name.
     * 
     * @return
     */
    public String name() {
        return _name;
    }

    /**
     * Application specific key, if any. This will be the key supplied when the
     * request was executed.
     * 
     * @return
     */
    public String key() {
        return _key;
    }

    /**
     * Returns the description, if any, supplied when the service was executed.
     * 
     * @return
     */
    public String description() {
        return _description;
    }

    /**
     * Returns the arguments supplied to the service.
     * 
     * @return
     */
    public XmlDoc.Element arguments() {
        return _args;
    }

    /**
     * The number of outputs to be generated.
     * 
     * @return
     */
    public int numberOfOutputs() {
        return _nbOut;
    }

    /**
     * The number of sub-operations completed by this service, if any. Note that
     * the service may not report progress.
     * 
     * @return
     */
    public long numberSubOperationsCompleted() {
        return _completed;
    }

    /**
     * Total number of failures.
     * 
     * @return
     */
    public long numberOfFailures() {
        return _failures;
    }

    /**
     * The stack traces of some of the first failures.
     * 
     * @return
     */
    public Collection<String> failureStackTraces() {
        return _initialFailures;
    }

    /**
     * Returns the current activity, if any.
     * 
     * @return
     */
    public String currentActivity() {
        return _activity;
    }

    /**
     * If failed, then the error information.
     * 
     * @return
     */
    public String error() {
        return _error;
    }

    /**
     * Can this task be suspended?
     * 
     * @return
     */
    public boolean canSuspend() {
        return _canSuspend;
    }

    /**
     * Can this task be aborted?
     * 
     * @return
     */
    public boolean canAbort() {
        return _canAbort;
    }

    /**
     * Requests this service is aborted. This request is sent to the server. The
     * task will attempt to abort. This can only be called if the service
     * indicates it can be aborted.
     * 
     * @throws Throwable
     * 
     */
    public void abort(MFSession session) throws Throwable {
        abort(session, id());
    }

    /**
     * Abort the request identified by the given 'id'. The caller must ensure
     * the service referred to by 'id' can in fact be aborted.
     * 
     * @param id
     * @throws Throwable
     */
    public static void abort(MFSession session, long id) throws Throwable {
        XmlStringWriter w = new XmlStringWriter();
        w.add("id", id);
        session.execute("service.background.abort", w.document());
    }

    /**
     * Requests this service is destroyed. This request is sent to the server
     * and the task will be removed/destroyed if completed, failed or aborted.
     * If not in one of those states, the request will be ignored.
     * 
     * @throws Throwable
     * 
     */
    public void destroy(MFSession session) throws Throwable {
        destroy(session, id());
    }

    public static void destroy(MFSession session, long id) throws Throwable {
        XmlStringWriter w = new XmlStringWriter();
        w.add("id", id);
        session.execute("service.background.destroy", w.document());
    }

    public static BackgroundService describe(MFSession session, long id) throws Throwable {
        XmlStringWriter w = new XmlStringWriter();
        w.add("id", id);
        return new BackgroundService(session.execute("service.background.describe", w.document()).element("task"));
    }

    public static void describe(MFSession session, long id, StateListener l) throws Throwable {
        l.updated(describe(session, id));
    }

    public Element getResult(MFSession session) throws Throwable {
        XmlStringWriter w = new XmlStringWriter();
        w.add("id", id());
        return session.execute("service.background.results.get", w.document()).element("reply");
    }

}
