package unimelb.mf.client.sync.settings;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import unimelb.mf.client.session.MFSession;
import unimelb.mf.client.util.AssetNamespaceUtils;

public abstract class Settings {

    public static final int DEFAULT_DAEMON_LISTENER_PORT = 9761;
    public static final int DEFAULT_MAX_WORKERS = 1;
    public static final int DEFAULT_QUERY_RESULT_PAGE_SIZE = 1000;

    private Action _action;
    private Direction _direction;
    private List<Job> _jobs;
    private Path _logDir = Paths.get(System.getProperty("java.io.tmpdir"));

    private int _nbWorkers = DEFAULT_MAX_WORKERS;

    private boolean _daemon = false;
    private int _daemonListenerPort = DEFAULT_DAEMON_LISTENER_PORT;

    private int _queryResultPageSize = DEFAULT_QUERY_RESULT_PAGE_SIZE;

    protected Settings(Action action, Direction direction, List<Job> jobs) {
        _action = action;
        _direction = direction;
        _jobs = new ArrayList<Job>();
        if (jobs != null) {
            _jobs.addAll(jobs);
        }
    }

    public Action action() {
        return _action;
    }

    protected void setAction(Action action) {
        if (action != null) {
            if (action != _action) {
                _action = action;
                // Make the job actions be consistent.
                if (_jobs != null && !_jobs.isEmpty()) {
                    for (ListIterator<Job> it = _jobs.listIterator(); it.hasNext();) {
                        Job job = it.next();
                        it.remove();
                        it.add(new Job(_action, _direction, job.directory(), job.namespace(),
                                job.isDestinationParent()));
                    }
                }
            }
        }
    }

    public Direction direction() {
        return _direction;
    }

    protected void setDirection(Direction direction) {
        if (direction != null) {
            if (direction != _direction) {
                _direction = direction;
                // Make the job actions be consistent.
                if (_jobs != null && !_jobs.isEmpty()) {
                    for (ListIterator<Job> it = _jobs.listIterator(); it.hasNext();) {
                        Job job = it.next();
                        it.remove();
                        it.add(new Job(_action, _direction, job.directory(), job.namespace(),
                                job.isDestinationParent()));
                    }
                }
            }
        }
    }

    public Path logDirectory() {
        return _logDir;
    }

    public void setLogDirectory(Path logDir) {
        _logDir = logDir;
    }

    public List<Job> jobs() {
        if (_jobs != null && !_jobs.isEmpty()) {
            return Collections.unmodifiableList(_jobs);
        }
        return null;
    }

    public void addJob(Path dir, String ns, boolean isDestinationParent) {
        addJob(new Job(action(), direction(), dir, ns, isDestinationParent));
    }

    public void addJob(Job... jobs) {
        if (_jobs == null) {
            _jobs = new ArrayList<Job>();
        }
        if (jobs != null && jobs.length > 0) {
            for (Job job : jobs) {
                if (job.action() != action() || job.direction() != direction()) {
                    // NOTE: the job's action must be consistent with the
                    // settings' action.
                    _jobs.add(new Job(action(), direction(), job.directory(), job.namespace(),
                            job.isDestinationParent()));
                } else {
                    _jobs.add(job);
                }
            }
        }
    }

    public void clearJobs() {
        if (_jobs != null) {
            _jobs.clear();
        }
    }

    public void setJobs(Collection<Job> jobs) {
        clearJobs();
        if (jobs != null) {
            for (Job job : jobs) {
                addJob(job);
            }
        }
    }

    public boolean daemon() {
        return _daemon;
    }

    public void setDaemon(boolean daemon) {
        _daemon = daemon;
    }

    public int daemonListenerPort() {
        return _daemonListenerPort;
    }

    public void setDaemonListenerPort(int port) {
        _daemonListenerPort = port;
    }

    public int numberOfWorkers() {
        return _nbWorkers;
    }

    public void setNumberOfWorkers(int nbWorkers) {
        if (nbWorkers <= 1) {
            _nbWorkers = 1;
        } else {
            _nbWorkers = nbWorkers;
        }
    }

    public int queryResultPageSize() {
        return _queryResultPageSize;
    }

    public void setQueryResultPageSize(int pageSize) {
        if (pageSize < 1) {
            _queryResultPageSize = 1;
        } else {
            _queryResultPageSize = pageSize;
        }
    }

    public void compile(MFSession session) throws Throwable {
        // TODO
    }

    public void validate(MFSession session) throws Throwable {
        for (Job job : _jobs) {
            if (job.namespace() == null) {
                throw new IllegalArgumentException("Asset namespace is null.", new NullPointerException());
            }
            if (!AssetNamespaceUtils.assetNamespaceExists(session, job.namespace())) {
                throw new IllegalArgumentException("Asset namespace: '" + job.namespace() + "' does not exist.");
            }
            if (job.directory() == null) {
                throw new IllegalArgumentException("Source directory is null.", new NullPointerException());
            }
            if (!Files.exists(job.directory())) {
                throw new IllegalArgumentException("Source directory: '" + job.directory() + "' does not exist",
                        new FileNotFoundException(job.directory().toString()));
            }
            if (!Files.isDirectory(job.directory())) {
                throw new IllegalArgumentException("'" + job.directory() + "' is not a directory.");
            }
        }
    }

}
