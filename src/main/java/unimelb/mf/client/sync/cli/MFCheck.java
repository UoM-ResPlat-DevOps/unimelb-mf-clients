package unimelb.mf.client.sync.cli;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import unimelb.mf.client.session.MFConnectionSettings;
import unimelb.mf.client.session.MFSession;
import unimelb.mf.client.sync.MFSyncApp;
import unimelb.mf.client.sync.check.DefaultCheckHandler;
import unimelb.mf.client.sync.settings.Action;
import unimelb.mf.client.sync.settings.Job;
import unimelb.mf.client.util.AssetNamespaceUtils;
import unimelb.mf.client.util.LoggingUtils;

public class MFCheck extends MFSyncApp {

    public static final String PROG = "unimelb-mf-check";

    private MFConnectionSettings _connectionSettings;
    private Map<Path, String> _dirNamespaces;
    private Action.Direction _direction;
    private Path _outputFile;
    private DefaultCheckHandler _checkHandler;

    public MFCheck() {
        super();
        settings().setCsumCheck(true);
        settings().setVerbose(true);

        _dirNamespaces = new LinkedHashMap<Path, String>();

    }

    @Override
    protected void preExecute() {
        if (logger() == null) {
            setLogger(LoggingUtils.createConsoleLogger());
        }
    }

    protected void printUsage() {
        // @formatter:off
        System.out.println();
        System.out.println("USAGE:");
        System.out.println(String.format("    %s [OPTIONS] --direction <up|down|both> --output <output.csv> <dir1> <namespace1> [<dir2> <namespace2>...]", PROG));
        System.out.println();
        System.out.println("DESCRIPTION:");
        System.out.println("    Compare files in local diretory with assets in remote asset namespace.");
        System.out.println();
        System.out.println("OPTIONS:");
        System.out.println("    --mf.config <mflux.cfg>                   Path to the config file that contains Mediaflux server details and user credentials.");
        System.out.println("    --mf.host <host>                          Mediaflux server host.");
        System.out.println("    --mf.port <port>                          Mediaflux server port.");
        System.out.println("    --mf.transport <https|http|tcp/ip>        Mediaflux server transport, can be http, https or tcp/ip.");
        System.out.println("    --mf.auth <domain,user,password>          Mediaflux user credentials.");
        System.out.println("    --mf.token <token>                        Mediaflux secure identity token.");
        System.out.println("    --direction <up|down|both>                Direction(up/down/both).");
        System.out.println("    -o, --output <output.csv>                 Output CSV file.");
        System.out.println("    --detailed-output                         Include all files checked. Otherwise, only the missing or invalid files are included in the output.");
        System.out.println("    --no-csum-check                           Do not generate and compare CRC32 checksum.");
        System.out.println("    --nb-queriers <n>                         Number of query threads. Defaults to " + unimelb.mf.client.sync.settings.Settings.DEFAULT_NUM_OF_QUERIERS);
        System.out.println("    --nb-workers <n>                          Number of concurrent worker threads to read local file (to generate checksum) if needed. Defaults to " + unimelb.mf.client.sync.settings.Settings.DEFAULT_NUM_OF_WORKERS);
        System.out.println("    --nb-retries <n>                          Retry times when error occurs. Defaults to " + unimelb.mf.client.sync.settings.Settings.DEFAULT_MAX_RETRIES);
        System.out.println("    --batch-size <size>                       Size of the query result. Defaults to " + unimelb.mf.client.sync.settings.Settings.DEFAULT_BATCH_SIZE);
        System.out.println("    --quiet                                   Do not print progress messages.");
        System.out.println();
        System.out.println("POSITIONAL ARGUMENTS:");
        System.out.println("    <dir>                                     Local directory path.");
        System.out.println("    <namespace>                               Remote Mediaflux namespace path.");
        System.out.println();
        System.out.println("EXAMPLES:");
        System.out.println(String.format("    %s --mf.config ~/.Arcitecta/mflux.cfg --direction down --output ~/Documents/foo-download-check.csv ~/Documents/foo /projects/proj-1.2.3/foo", PROG));
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
                    int n = parseMFOptions(args, i);
                    if (n > 0) {
                        i += n;
                        continue;
                    }
                    n = parseCheckOptions(args, i);
                    if (n > 0) {
                        i += n;
                        continue;
                    }
                    Path dir = Paths.get(args[i]);
                    if (!Files.exists(dir)) {
                        throw new IllegalArgumentException("Directory: '" + dir.toString() + "' does not exist.");
                    }
                    if (!Files.isDirectory(dir)) {
                        throw new IllegalArgumentException("'" + dir.toString() + "' is not a directory.");
                    }
                    if (i + 1 >= args.length) {
                        throw new IllegalArgumentException("Missing asset namespace path.");
                    }
                    String ns = args[i + 1];
                    _dirNamespaces.put(dir, ns);
                    i += 2;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new IllegalArgumentException(e);
            }
        }

        if (_direction == null) {
            throw new IllegalArgumentException("Missing --direction argument.");
        }
        if (_outputFile == null) {
            throw new IllegalArgumentException("Missing --output-file argument.");
        }
        if (_dirNamespaces.isEmpty()) {
            throw new IllegalArgumentException("Missing local directory and remote asset namespace arguments.");
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
        Set<Path> dirs = _dirNamespaces.keySet();
        for (Path dir : dirs) {
            String ns = _dirNamespaces.get(dir);
            if (!AssetNamespaceUtils.assetNamespaceExists(session, ns)) {
                throw new IllegalArgumentException("Asset namespace: '" + ns + "' does not exist.");
            }
            Job job = new Job(Action.get(Action.Type.CHECK, _direction), dir, ns);
            settings().addJob(job);
        }

        _checkHandler = new DefaultCheckHandler(_outputFile);
        settings().setCheckHandler(_checkHandler);
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

    protected int parseCheckOptions(String[] args, int i) throws Throwable {
        if ("--direction".equalsIgnoreCase(args[i])) {
            _direction = Action.Direction.fromString(args[i + 1]);
            if (_direction == null) {
                throw new IllegalArgumentException("Invalid --direction " + args[i + 1]);
            }
            return 2;
        } else if ("--output".equalsIgnoreCase(args[i]) || "-o".equalsIgnoreCase(args[i])) {
            _outputFile = Paths.get(args[i + 1]);
            Path dir = _outputFile.toAbsolutePath().getParent();
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            _outputFile = renameOutputCsvFile(_outputFile);
            return 2;
        } else if ("--no-csum-check".equalsIgnoreCase(args[i])) {
            settings().setCsumCheck(false);
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
        } else if ("--quiet".equalsIgnoreCase(args[i])) {
            settings().setVerbose(false);
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    protected void postExecute() {
        super.postExecute();
        try {
            _checkHandler.writeSummary();
            _checkHandler.printSummary();
        } finally {
            try {
                _checkHandler.close();
            } catch (Throwable e) {
                logger().log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    private static Path renameOutputCsvFile(Path file) {
        Path f = file;
        String dir = f.toAbsolutePath().getParent().toString();
        String name = f.getFileName().toString();
        if (!name.toLowerCase().endsWith(".csv")) {
            return renameOutputCsvFile(Paths.get(dir, name + ".csv"));
        }
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        StringBuilder sb = new StringBuilder();
        if (Files.exists(f)) {
            sb.append(name.substring(0, name.length() - 4));
            sb.append("-").append(timestamp);
            sb.append(".csv");
            return Paths.get(dir, sb.toString());
        } else {
            return file;
        }
    }

    public static void main(String[] args) throws Throwable {
        MFCheck app = new MFCheck();
        try {
            app.parseArgs(args);
        } catch (IllegalArgumentException iae) {
            System.err.println(iae.getMessage());
            app.printUsage();
            System.exit(1);
        }
        app.execute();
    }

    @Override
    public final String applicationName() {
        return PROG;
    }

}
