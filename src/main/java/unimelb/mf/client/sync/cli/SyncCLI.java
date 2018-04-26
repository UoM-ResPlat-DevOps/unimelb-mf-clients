package unimelb.mf.client.sync.cli;

import java.io.PrintStream;
import java.util.logging.Logger;

import unimelb.mf.client.session.MFConnectionSettings;
import unimelb.mf.client.session.MFSession;
import unimelb.mf.client.sync.app.SyncApp;
import unimelb.mf.client.sync.settings.Settings;
import unimelb.mf.client.util.LoggingUtils;

public interface SyncCLI<T extends Settings> {

    default void execute(SyncApp<T> app, String[] args) throws Throwable {

        MFConnectionSettings cns = new MFConnectionSettings();
        cns.setApp(app.applicationName());
        try {
            for (int i = 0; i < args.length;) {
                if (args[i].equals("--mf.config")) {
                    try {
                        cns.loadFromConfigFile(args[i + 1]);
                    } catch (Throwable e) {
                        throw new IllegalArgumentException("Invalid --mf.conf: " + args[i + 1], e);
                    }
                    i += 2;
                } else if (args[i].equals("--mf.host")) {
                    cns.setServerHost(args[i + 1]);
                    i += 2;
                } else if (args[i].equals("--mf.port")) {
                    cns.setServerPort(Integer.parseInt(args[i + 1]));
                    i += 2;
                } else if (args[i].equals("--mf.transport")) {
                    cns.setServerTransport(args[i + 1]);
                    i += 2;
                } else if (args[i].equals("--mf.auth")) {
                    String auth = args[i + 1];
                    String[] parts = auth.split(",");
                    if (parts == null || parts.length != 3) {
                        throw new IllegalArgumentException("Invalid mf.auth: " + auth);
                    }
                    cns.setUserCredentials(parts[0], parts[1], parts[2]);
                    i += 2;
                } else if (args[i].equals("--mf.token")) {
                    cns.setToken(args[i + 1]);
                    i += 2;
                } else if (args[i].equals("--daemon")) {
                    app.settings().setDaemon(true);
                    i++;
                } else if (args[i].equals("--daemon-port")) {
                    try {
                        int port = Integer.parseInt(args[i + 1]);
                        if (port <= 0 || port > 65535) {
                            throw new IllegalArgumentException("Invalid --daemon-port: " + args[i + 1]);
                        }
                        app.settings().setDaemonListenerPort(port);
                    } catch (NumberFormatException nfe) {
                        throw new IllegalArgumentException("Invalid --daemon-port: " + args[i + 1], nfe);
                    }
                    i += 2;
                } else if (args[i].equals("--nb-workers")) {
                    try {
                        int nbWorkers = Integer.parseInt(args[i + 1]);
                        if (nbWorkers < 1) {
                            throw new IllegalArgumentException("Invalid --nb-workers: " + args[i + 1]);
                        }
                        app.settings().setNumberOfWorkers(nbWorkers);
                    } catch (NumberFormatException nfe) {
                        throw new IllegalArgumentException("Invalid --nb-workers: " + args[i + 1], nfe);
                    }
                    i += 2;
                } else {
                    i = parseArg(app.settings(), args, i);
                }
            }
            cns.validate();

            MFSession session = new MFSession(cns);

            // test authentication
            session.testAuthentication();

            // set associated session
            app.setSession(session);

            // compile settings: may communicate to MF server for values.
            app.settings().compile(session);

            // validate settings
            app.settings().validate(session);

            // initialize logging
            Logger logger = LoggingUtils.createFileAndConsoleLogger(app.settings().logDirectory(),
                    app.applicationName());

            // set associated logger
            app.setLogger(logger);

        } catch (Throwable e) {
            e.printStackTrace();
            if (e instanceof IllegalArgumentException) {
                printUsage(app, System.out);
            }
            System.exit(1);
        }

        // execute the application.
        app.execute(app.session(), app.settings());
        
    }

    /**
     * Parse command line arguments and set settings.
     * 
     * @param settings
     *            The application settings.
     * @param args
     *            The array of command line arguments.
     * @param i
     *            The index of the next argument to be processed.
     * @return
     */
    int parseArg(T settings, String[] args, int i);

    default void printUsage(SyncApp<T> app, PrintStream ps) {
        ps.println();
        ps.println("USAGE:");
        ps.println(synopsis(app.applicationName()));
        ps.println();
        ps.println();
        ps.println("DESCRIPTION:");
        ps.println(String.format("    %s", app.description()));
        ps.println();
        ps.println("MEDIAFLUX ARGUMENTS:");
        printMediafluxArgs(ps);
        ps.println();
        ps.println("ARGUMENTS:");
        ps.println("    --nb-workers <number-of-worker-threads>   Number of worker threads. Defaults to 1.");
        ps.println("    --log-dir <log directory>                 Log directory. Defaults to system temp directory.");
        printArgs(ps);
        ps.println();
        ps.println("EXAMPLES:");
        printExamples(ps);
    }

    default String synopsis(String appName) {
        return String.format("    %s [mediaflux-arguments] <arguments>", appName);
    }

    default void printMediafluxArgs(PrintStream ps) {
        //@formatter:off
        ps.println("    --mf.config <mflux.cfg>                   Path to the config file that contains Mediaflux server details and user credentials.");
        ps.println("    --mf.host <host>                          Mediaflux server host.");
        ps.println("    --mf.port <port>                          Mediaflux server port.");
        ps.println("    --mf.transport <https|http|tcp/ip>        Mediaflux server transport, can be http, https or tcp/ip.");
        ps.println("    --mf.auth <domain,user,password>          Mediaflux user credentials.");
        ps.println("    --mf.token <token>                        Mediaflux secure identity token.");
        //@formatter:on
    }

    void printArgs(PrintStream ps);

    void printExamples(PrintStream ps);

}
