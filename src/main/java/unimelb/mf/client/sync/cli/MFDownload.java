package unimelb.mf.client.sync.cli;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;

import unimelb.mf.client.session.MFConnectionSettings;
import unimelb.mf.client.session.MFSession;
import unimelb.mf.client.sync.MFSyncApp;
import unimelb.mf.client.sync.settings.Action;
import unimelb.mf.client.sync.settings.Job;
import unimelb.mf.client.sync.task.AssetDownloadTask.Unarchive;
import unimelb.mf.client.util.AssetNamespaceUtils;
import unimelb.mf.client.util.LoggingUtils;

public class MFDownload extends MFSyncApp {

    public static final String PROG = "unimelb-mf-download";

    private MFConnectionSettings _connectionSettings;
    private Set<String> _namespaces;
    private Path _rootDir;

    public MFDownload() {
        super();
        settings().setOverwrite(false);
        settings().setUnarchive(Unarchive.NONE);
        settings().setCsumCheck(false);
        settings().setVerbose(true);
        settings().setDaemon(false);
        _namespaces = new LinkedHashSet<String>();
    }

    @Override
    protected void preExecute() throws Throwable {
        if (logger() == null) {
            if (settings().logDirectory() == null) {
                setLogger(LoggingUtils.createConsoleLogger());
            } else {
                setLogger(LoggingUtils.createFileAndConsoleLogger(settings().logDirectory(), applicationName()));
            }
        }
    }

    protected void printUsage() {
        // @formatter:off
        System.out.println();
        System.out.println("USAGE:");
        System.out.println(String.format("    %s [OPTIONS] --out <dst-dir> <namespace1> [<namespace2>...]", PROG));
        System.out.println();
        System.out.println("DESCRIPTION:");
        System.out.println("    Download assets (files)  from Mediaflux to the local file system.  Pre-existing files in the local file system can be skipped or overwritten. In Daemon mode, the process will only download new assets (files)  since the process last executed.");
        System.out.println();
        System.out.println("OPTIONS:");
        System.out.println("    --mf.config <mflux.cfg>                   Path to the config file that contains Mediaflux server details and user credentials.");
        System.out.println("    --mf.host <host>                          Mediaflux server host.");
        System.out.println("    --mf.port <port>                          Mediaflux server port.");
        System.out.println("    --mf.transport <https|http|tcp/ip>        Mediaflux server transport, can be http, https or tcp/ip.");
        System.out.println("    --mf.auth <domain,user,password>          Mediaflux user credentials.");
        System.out.println("    --mf.token <token>                        Mediaflux secure identity token.");

        System.out.println("    -o, --out <dst-dir>                       The output/destination directory.");
        System.out.println("    --overwrite                               Overwrite if the dst file exists.");
        System.out.println("    --unarchive                               Extract Arcitecta .aar files.");
        System.out.println("    --csum-check                              Files are equated if the name and size are the same. In addition, with this argument, you can optionally compute the CRC32 checksumk to decide if two files are the same.");
        System.out.println("    --nb-queriers <n>                         Number of query threads. Defaults to " + unimelb.mf.client.sync.settings.Settings.DEFAULT_NUM_OF_QUERIERS);
        System.out.println("    --nb-workers <n>                          Number of concurrent worker threads to download data. Defaults to " + unimelb.mf.client.sync.settings.Settings.DEFAULT_NUM_OF_WORKERS);
        System.out.println("    --nb-retries <n>                          Retry times when error occurs. Defaults to " + unimelb.mf.client.sync.settings.Settings.DEFAULT_MAX_RETRIES);
        System.out.println("    --batch-size <size>                       Size of the query result. Defaults to " + unimelb.mf.client.sync.settings.Settings.DEFAULT_BATCH_SIZE);
        System.out.println("    --daemon                                  Run as a daemon.");
        System.out.println("    --daemon-port <port>                      Daemon listener port if running as a daemon. Defaults to " + unimelb.mf.client.sync.settings.Settings.DEFAULT_DAEMON_LISTENER_PORT);
        System.out.println("    --daemon-scan-interval <seconds>          Time interval (in seconds) between scans of source asset namespaces. Defaults to " + (unimelb.mf.client.sync.settings.Settings.DEFAULT_DAEMON_SCAN_INTERVAL/1000) + " seconds.");
        System.out.println("    --log-dir <dir>                           Path to the directory for log files. No logging if not specified.");
        System.out.println("    --notify <email-addresses>                When completes, send email notification to the recipients(comma-separated email addresses if multiple). Not applicable for daemon mode.");
        System.out.println("    --quiet                                   Do not print progress messages.");
        System.out.println("    --help                                    Prints usage.");
        System.out.println();
        System.out.println("POSITIONAL ARGUMENTS:");
        System.out.println("    <namespace>                               The asset namespace to download.");
        System.out.println();
        System.out.println("EXAMPLES:");
        System.out.println(String.format("    %s --mf.config ~/.Arcitecta/mflux.cfg --nb-workers 2  --out ~/Downloads /projects/proj-1128.1.59/foo /projects/proj-1128.1.59/bar", PROG));
        System.out.println();
        // @formatter:on
    }

    protected void parseArgs(String[] args) throws Throwable {
        if (_connectionSettings == null) {
            _connectionSettings = new MFConnectionSettings();
        }
        if (args != null) {
            try {
                for (int i = 0; i < args.length;) {
                    if ("--help".equalsIgnoreCase(args[i]) || "-h".equalsIgnoreCase(args[i])) {
                        printUsage();
                        System.exit(0);
                    }
                    int n = parseMFOptions(args, i);
                    if (n > 0) {
                        i += n;
                        continue;
                    }
                    n = parseDownloadOptions(args, i);
                    if (n > 0) {
                        i += n;
                        continue;
                    }
                    _namespaces.add(args[i]);
                    i++;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new IllegalArgumentException(e);
            }
        }

        if (_rootDir == null) {
            throw new IllegalArgumentException("Missing output directory: --out argument.");
        }
        if (_namespaces.isEmpty()) {
            throw new IllegalArgumentException("Missing remote asset namespace arguments.");
        }

        /*
         * validate MF connection settings locally
         */
        _connectionSettings.validate();

        /*
         * test MF authentication
         */
        MFSession session = new MFSession(_connectionSettings);
        session.testAuthentication();

        /*
         * set MF session
         */
        setSession(session);

        /*
         * add jobs
         */
        for (String ns : _namespaces) {
            if (!AssetNamespaceUtils.assetNamespaceExists(session, ns)) {
                throw new IllegalArgumentException("Asset namespace: '" + ns + "' does not exist.");
            }
            settings().addJob(new Job(Action.DOWNLOAD, ns, _rootDir, true));
        }
    }

    protected int parseMFOptions(String[] args, int i) throws Throwable {
        if ("--mf.config".equalsIgnoreCase(args[i])) {
            try {
                _connectionSettings.loadFromConfigFile(args[i + 1]);
            } catch (Throwable e) {
                throw new IllegalArgumentException("Invalid --mf.config: " + args[i + 1], e);
            }
            return 2;
        } else if ("--mf.host".equalsIgnoreCase(args[i])) {
            _connectionSettings.setServerHost(args[i + 1]);
            return 2;
        } else if ("--mf.port".equalsIgnoreCase(args[i])) {
            _connectionSettings.setServerPort(Integer.parseInt(args[i + 1]));
            return 2;
        } else if ("--mf.transport".equalsIgnoreCase(args[i])) {
            _connectionSettings.setServerTransport(args[i + 1]);
            return 2;
        } else if ("--mf.auth".equalsIgnoreCase(args[i])) {
            String auth = args[i + 1];
            String[] parts = auth.split(",");
            if (parts == null || parts.length != 3) {
                throw new IllegalArgumentException("Invalid mf.auth: " + auth);
            }
            _connectionSettings.setUserCredentials(parts[0], parts[1], parts[2]);
            return 2;
        } else if ("--mf.token".equalsIgnoreCase(args[i])) {
            _connectionSettings.setToken(args[i + 1]);
            return 2;
        } else {
            return 0;
        }
    }

    protected int parseDownloadOptions(String[] args, int i) throws Throwable {
        if ("--out".equalsIgnoreCase(args[i])) {
            _rootDir = Paths.get(args[i + 1]).toAbsolutePath();
            if (!Files.exists(_rootDir)) {
                throw new IllegalArgumentException("Directory: " + args[i] + " does not exist.");
            }
            if (!Files.isDirectory(_rootDir)) {
                throw new IllegalArgumentException(args[i] + " is not a directory.");
            }
            return 2;
        } else if ("--overwrite".equalsIgnoreCase(args[i])) {
            settings().setOverwrite(true);
            return 1;
        } else if ("--unarchive".equalsIgnoreCase(args[i])) {
            settings().setUnarchive(Unarchive.AAR);
            return 1;
        } else if ("--csum-check".equalsIgnoreCase(args[i])) {
            settings().setCsumCheck(true);
            return 1;
        } else if ("--batch-size".equalsIgnoreCase(args[i])) {
            try {
                int batchSize = Integer.parseInt(args[i + 1]);
                if (batchSize <= 0) {
                    throw new IllegalArgumentException("Invalid --batch-size " + args[i + 1]
                            + " Expects a positive integer, found " + args[i + 1]);
                }
                settings().setBatchSize(batchSize);
                return 2;
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException(
                        "Invalid --batch-size " + args[i + 1] + " Expects a positive integer, found " + args[i + 1],
                        nfe);
            }
        } else if ("--nb-queriers".equalsIgnoreCase(args[i])) {
            try {
                int nbQueriers = Integer.parseInt(args[i + 1]);
                if (nbQueriers <= 0) {
                    throw new IllegalArgumentException("Invalid --nb-queriers " + args[i + 1]
                            + " Expects a positive integer, found " + args[i + 1]);
                }
                settings().setNumberOfQueriers(nbQueriers);
                return 2;
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException(
                        "Invalid --nb-queriers " + args[i + 1] + " Expects a positive integer, found " + args[i + 1],
                        nfe);
            }
        } else if ("--nb-workers".equalsIgnoreCase(args[i])) {
            try {
                int nbWorkers = Integer.parseInt(args[i + 1]);
                if (nbWorkers <= 0) {
                    throw new IllegalArgumentException("Invalid --nb-workers " + args[i + 1]
                            + " Expects a positive integer, found " + args[i + 1]);
                }
                settings().setNumberOfWorkers(nbWorkers);
                return 2;
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException(
                        "Invalid --nb-workers " + args[i + 1] + " Expects a positive integer, found " + args[i + 1],
                        nfe);
            }
        } else if ("--nb-retries".equalsIgnoreCase(args[i])) {
            try {
                int nbRetries = Integer.parseInt(args[i + 1]);
                settings().setMaxRetries(nbRetries);
                return 2;
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException(
                        "Invalid --nb-retries " + args[i + 1] + " Expects a positive integer, found " + args[i + 1],
                        nfe);
            }
        } else if ("--daemon".equalsIgnoreCase(args[i])) {
            settings().setDaemon(true);
            return 1;
        } else if ("--daemon-port".equalsIgnoreCase(args[i])) {
            try {
                int port = Integer.parseInt(args[i + 1]);
                if (port < 0 || port > 65535) {
                    throw new IllegalArgumentException("Invalid --daemon-port " + args[i + 1]
                            + " Expects a positive integer, found " + args[i + 1]);
                }
                settings().setDaemonListenerPort(port);
                return 2;
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("Invalid --daemon-port " + args[i + 1]
                        + " Expects a positive integer in the range of [0, 65535], found " + args[i + 1], nfe);
            }
        } else if ("--daemon-scan-interval".equalsIgnoreCase(args[i])) {
            try {
                int intervalSeconds = Integer.parseInt(args[i + 1]);
                if (intervalSeconds <= 0) {
                    throw new IllegalArgumentException("Invalid --daemon-scan-interval " + args[i + 1]
                            + " Expects a positive integer, found " + args[i + 1]);
                }
                settings().setDaemonScanInterval(((long) intervalSeconds) * 1000L);
                return 2;
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("Invalid --daemon-scan-interval " + args[i + 1]
                        + " Expects a positive integer, found " + args[i + 1], nfe);
            }
        } else if ("--log-dir".equalsIgnoreCase(args[i])) {
            Path logDir = Paths.get(args[i + 1]).toAbsolutePath();
            if (!Files.exists(logDir)) {
                throw new IllegalArgumentException("Directory: " + args[i] + " does not exist.");
            }
            if (!Files.isDirectory(logDir)) {
                throw new IllegalArgumentException(args[i] + " is not a directory.");
            }
            settings().setLogDirectory(logDir);
            return 2;
        } else if ("--notify".equalsIgnoreCase(args[i])) {
            String[] emails = args[i + 1].indexOf(',') != -1 ? args[i + 1].split(",") : new String[] { args[i + 1] };
            settings().addRecipients(emails);
            return 2;
        } else if ("--quiet".equalsIgnoreCase(args[i])) {
            settings().setVerbose(false);
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public final String applicationName() {
        return PROG;
    }

    public static void main(String[] args) throws Throwable {
        MFDownload app = new MFDownload();
        try {
            app.parseArgs(args);
        } catch (IllegalArgumentException iae) {
            System.err.println(iae.getMessage());
            app.printUsage();
            System.exit(1);
        }
        if (app.settings().daemon()) {
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

                @Override
                public void run() {
                    app.interrupt();
                }
            }));
            new Thread(app).start();
        } else {
            app.execute();
        }
    }

}
