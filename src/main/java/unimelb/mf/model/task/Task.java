package unimelb.mf.model.task;

import java.util.Date;
import java.util.List;

import arc.utils.ObjectUtil;
import arc.xml.XmlDoc;

public class Task {

    public enum State {
        PENDING, EXECUTING, COMPLETED, ABORT_PENDING, ABORTED, FAILED, SUSPENDED, STATE_FAILED_WILL_RETRY;

        @Override
        public String toString() {
            return name().replace('_', '-').toLowerCase();
        }

        public static State state(String s) {
            if (s.equalsIgnoreCase("ABORT-PENDING")) {
                return ABORT_PENDING;
            }

            if (s.equalsIgnoreCase("FAILED-WILL-RETRY")) {
                return STATE_FAILED_WILL_RETRY;
            }

            return State.valueOf(s);
        }
    }

    private long _id;
    private State _state;
    private Date _startTime;
    private Date _endTime;
    private double _execTime;
    private long _completed;
    private long _total;
    private Throwable _exception;
    private List<UnitsOfWork> _uow = null;

    protected Task(Throwable t) throws Throwable {
        _exception = t;
        _state = State.FAILED;
    }

    protected Task(XmlDoc.Element xe) throws Throwable {
        _id = xe.longValue("@id");

        _state = State.state(xe.value("state").toUpperCase());
        _startTime = xe.dateValue("start-time");
        _endTime = xe.dateValue("end-time");
        _execTime = xe.doubleValue("exec-time", 0);
        _total = xe.longValue("total", -1);
        _completed = xe.longValue("completed", 0);

        _uow = UnitsOfWork.listFromXml(xe);
    }

    /**
     * The unique (task) identifier.
     * 
     * @return
     */
    public long id() {
        return _id;
    }

    /**
     * Returns the current state of the task.
     * 
     * @return
     */
    public State state() {
        return _state;
    }

    /**
     * Returns any thrown exception
     * 
     * @return
     */
    public Throwable exception() {
        return _exception;
    }

    /**
     * Was the task aborted? This is a convenience method that checks the state
     * to see if ABORTED.
     * 
     * @return
     */
    public boolean aborted() {
        return _state.equals(State.ABORTED);
    }

    /**
     * Has the task failed?
     * 
     * @return
     */
    public boolean failed() {
        switch (_state) {
        case FAILED:
            return true;
        default:
            return false;
        }
    }

    /**
     * Has the task finished?
     * 
     * @return
     */
    public boolean finished() {
        switch (_state) {
        case COMPLETED:
        case ABORTED:
        case FAILED:
            return true;
        default:
            return false;
        }

    }

    /**
     * Returns the time started, or null.
     *
     * @return
     */
    public Date startTime() {
        return _startTime;
    }

    /**
     * Returns the time the task ended, if it has ended.
     * 
     * @return
     */
    public Date endTime() {
        return _endTime;
    }

    /**
     * Returns the total execution time (in seconds).
     * 
     * @return
     */
    public double executionTime() {
        return _execTime;
    }

    /**
     * Returns the number of sub-operations completed.
     * 
     * @return
     */
    public long subOperationsCompleted() {
        return _completed;
    }

    /**
     * Total operations to be completed
     * 
     * @return
     */
    public long totalOperations() {
        return _total;
    }

    /**
     * Additional units of work
     * 
     * @return
     */
    public List<UnitsOfWork> unitsOfWork() {
        return _uow;
    }

    /**
     * Locates a named unit of work
     * 
     * @param name
     * @return
     */
    public UnitsOfWork unitOfWork(String name) {

        List<UnitsOfWork> namedUnits = unitsOfWork();

        if (namedUnits == null || namedUnits.isEmpty()) {
            return null;
        }

        for (UnitsOfWork uow : namedUnits) {
            if (ObjectUtil.equals(uow.name(), name)) {
                return uow;
            }
        }

        return null;
    }

}
