package unimelb.mf.client.sync.settings;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import arc.xml.XmlDoc;
import unimelb.mf.client.session.MFSession;
import unimelb.mf.client.sync.check.CheckHandler;
import unimelb.mf.client.sync.task.AssetDownloadTask;
import unimelb.mf.client.sync.task.AssetDownloadTask.Unarchive;
import unimelb.mf.client.task.MFApp;
import unimelb.mf.client.util.AssetNamespaceUtils;

public class Settings implements MFApp.Settings {

    public static final int DEFAULT_DAEMON_LISTENER_PORT = 9761;
    public static final long DEFAULT_DAEMON_SCAN_INTERVAL = 60000L;
    public static final int DEFAULT_NUM_OF_QUERIERS = 1;
    public static final int DEFAULT_NUM_OF_WORKERS = 1;
    public static final int DEFAULT_BATCH_SIZE = 1000;
    public static final int DEFAULT_MAX_RETRIES = 0;

    private List<Job> _jobs;
    private Path _logDir = Paths.get(System.getProperty("java.io.tmpdir"));

    private int _nbQueriers = DEFAULT_NUM_OF_QUERIERS;
    private int _nbWorkers = DEFAULT_NUM_OF_WORKERS;

    private boolean _daemon = false;
    private int _daemonListenerPort = DEFAULT_DAEMON_LISTENER_PORT;
    private long _daemonScanInterval = DEFAULT_DAEMON_SCAN_INTERVAL;

    private int _batchSize = DEFAULT_BATCH_SIZE;

    private boolean _csumCheck;

    private int _maxRetries = DEFAULT_MAX_RETRIES; // Number of retries...

    /*
     * download settings
     */
    private boolean _overwrite = false;
    private AssetDownloadTask.Unarchive _unarchive = AssetDownloadTask.Unarchive.NONE;

    /*
     * upload settings;
     */
    private boolean _excludeEmptyFolder = true;

    /*
     * check settings
     */
    private CheckHandler _checkHandler = null;

    private boolean _verbose = false;

    private Set<String> _recipients;

    private boolean _deleteFiles = false;

    private boolean _deleteAssets = false;

    private boolean _hardDestroy = false;

    public Settings() {
        _jobs = new ArrayList<Job>();
    }

    public int batchSize() {
        return _batchSize;
    }

    public void setBatchSize(int batchSize) {
        if (batchSize < 1) {
            batchSize = DEFAULT_BATCH_SIZE;
        }
        _batchSize = batchSize;
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

    public void addJob(Job... jobs) {
        if (_jobs == null) {
            _jobs = new ArrayList<Job>();
        }
        if (jobs != null && jobs.length > 0) {
            for (Job job : jobs) {
                _jobs.add(job);
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

    public int numberOfQueriers() {
        return _nbQueriers;
    }

    public void setNumberOfQueriers(int nbQueriers) {
        if (nbQueriers <= 1) {
            _nbQueriers = 1;
        } else {
            _nbQueriers = nbQueriers;
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

    public boolean csumCheck() {
        return _csumCheck;
    }

    public void setCsumCheck(boolean csumCheck) {
        _csumCheck = csumCheck;
    }

    /**
     * Currently for download only.
     * 
     * @return
     */
    public boolean overwrite() {
        return _overwrite;
    }

    /**
     * Currently for download only.
     * 
     * @param overwrite
     */
    public void setOverwrite(boolean overwrite) {
        _overwrite = overwrite;
    }

    /**
     * Currently for download only.
     * 
     * @return
     */
    public AssetDownloadTask.Unarchive unarchive() {
        return _unarchive;
    }

    /**
     * Currently for download only.
     * 
     * @param unarchive
     */
    public void setUnarchive(AssetDownloadTask.Unarchive unarchive) {
        _unarchive = unarchive;
    }

    /**
     * For check only.
     * 
     * @return
     */
    public CheckHandler checkHandler() {
        return _checkHandler;
    }

    /**
     * For check only.
     * 
     * @param ch
     */
    public void setCheckHandler(CheckHandler ch) {
        _checkHandler = ch;
    }

    public int retry() {
        return _maxRetries;
    }

    public void setMaxRetries(int retry) {
        if (retry < 0) {
            _maxRetries = 0;
        }
        _maxRetries = retry;
    }

    public boolean excludeEmptyFolder() {
        return _excludeEmptyFolder;
    }

    public void setExcludeEmptyFolder(boolean excludeEmptyFolder) {
        _excludeEmptyFolder = excludeEmptyFolder;
    }

    public boolean verbose() {
        return _verbose;
    }

    public void setVerbose(boolean verbose) {
        _verbose = verbose;
    }

    public long daemonScanInterval() {
        return _daemonScanInterval;
    }

    public void setDaemonScanInterval(long interval) {
        _daemonScanInterval = interval;
    }

    public boolean hasRecipients() {
        return _recipients != null && !_recipients.isEmpty();
    }

    public Collection<String> recipients() {
        return _recipients == null ? null : Collections.unmodifiableCollection(_recipients);
    }

    public void addRecipients(String... emails) {
        if (emails != null && emails.length > 0) {
            if (_recipients == null) {
                _recipients = new LinkedHashSet<String>();
            }
            for (String email : emails) {
                _recipients.add(email.toLowerCase());
            }
        }
    }

    public boolean hasJobs() {
        return _jobs != null && !_jobs.isEmpty();
    }

    public boolean hasOnlyCheckJobs() {
        if (hasJobs()) {
            for (Job job : _jobs) {
                if (job.action().type() != Action.Type.CHECK) {
                    return false;
                }
            }
        }
        return true;
    }

    public void loadFromXmlFile(Path xmlFile) throws Throwable {
        if (xmlFile != null) {
            Reader r = new BufferedReader(new FileReader(xmlFile.toFile()));
            try {
                XmlDoc.Element se = new XmlDoc().parse(r).element("properties/sync/settings");
                if (se == null) {
                    throw new Exception("element properties/sync/settings is not found.");
                }

                if (se.elementExists("numberOfQueriers")) {
                    setNumberOfQueriers(se.intValue("numberOfQueriers"));
                }
                if (se.elementExists("numberOfWorkers")) {
                    setNumberOfQueriers(se.intValue("numberOfWorkers"));
                }
                if (se.elementExists("batchSize")) {
                    setBatchSize(se.intValue("batchSize"));
                }
                if (se.elementExists("maxRetries")) {
                    setMaxRetries(se.intValue("maxRetries"));
                }
                if (se.elementExists("csumCheck")) {
                    setCsumCheck(se.booleanValue("csumCheck", false));
                }
                if (se.elementExists("overwrite")) {
                    setOverwrite(se.booleanValue("overwrite", false));
                }
                if (se.elementExists("unarchive")) {
                    setUnarchive(Unarchive.fromString(se.value("unarchive")));
                }
                if (se.elementExists("daemon")) {
                    setDaemon(se.booleanValue("daemon/@enabled", false));
                    if (se.elementExists("daemon/listenerPort")) {
                        setDaemonListenerPort(se.intValue("daemon/listenerPort"));
                    }
                    if (se.elementExists("daemon/scanInterval")) {
                        setDaemonScanInterval(se.intValue("daemon/scanInterval"));
                    }
                }
                if (se.elementExists("logDirectory")) {
                    Path logDir = Paths.get(se.value("logDirectory"));
                    if (!Files.exists(logDir)) {
                        throw new FileNotFoundException(logDir.toString());
                    }
                    if (!Files.isDirectory(logDir)) {
                        throw new Exception(logDir.toString() + " is not a directory!");
                    }
                    setLogDirectory(logDir);
                }
                if (se.elementExists("excludeEmptyFolder")) {
                    setExcludeEmptyFolder(se.booleanValue("excludeEmptyFolder"));
                }
                if (se.elementExists("verbose")) {
                    setVerbose(se.booleanValue("verbose"));
                }
                if (se.elementExists("notification/email")) {
                    Collection<String> emails = se.values("notification/email");
                    if (emails != null) {
                        for (String email : emails) {
                            addRecipients(email);
                        }
                    }
                }
            } finally {
                r.close();
            }
        }
    }

    public boolean hasSyncJobs() {
        if (_jobs != null && !_jobs.isEmpty()) {
            for (Job job : _jobs) {
                if (job.action() == Action.SYNC) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasDownloadJobs() {
        if (_jobs != null && !_jobs.isEmpty()) {
            for (Job job : _jobs) {
                if (job.action() == Action.DOWNLOAD) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasUploadJobs() {
        if (_jobs != null && !_jobs.isEmpty()) {
            for (Job job : _jobs) {
                if (job.action() == Action.UPLOAD) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Delete local files.
     * 
     * @return
     */
    public boolean deleteFiles() {
        return _deleteFiles;
    }

    public void setDeleteFiles(boolean deleteFiles) {
        _deleteFiles = deleteFiles;
    }

    /**
     * Destroy remote assets.
     * 
     * @return
     */
    public boolean deleteAssets() {
        return _deleteAssets;
    }

    public void setDeleteAssets(boolean deleteAssets) {
        _deleteAssets = deleteAssets;
    }

    public boolean hardDestroy() {
        return _hardDestroy;
    }

    public void setHardDestroy(boolean hardDestroy) {
        _hardDestroy = hardDestroy;
    }

    public boolean needToDeleteFiles() {
        return (hasDownloadJobs() || hasSyncJobs()) && deleteFiles();
    }

    public boolean needToDeleteAssets() {
        return (hasUploadJobs() || hasSyncJobs()) && deleteAssets();
    }

}
