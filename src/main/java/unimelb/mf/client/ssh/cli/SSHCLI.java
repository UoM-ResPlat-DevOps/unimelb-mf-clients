package unimelb.mf.client.ssh.cli;

import java.io.PrintStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import arc.mf.client.ServerClient;
import arc.utils.ObjectUtil;
import arc.xml.XmlDoc;
import unimelb.mf.client.session.MFConnectionSettings;
import unimelb.mf.client.session.MFSession;
import unimelb.mf.client.ssh.SSHService;
import unimelb.mf.client.ssh.SSHTransferDirection;
import unimelb.mf.client.ssh.SSHTransferProtocol;
import unimelb.mf.client.task.Task;
import unimelb.mf.client.util.XmlUtils;
import unimelb.mf.model.service.BackgroundService;

public abstract class SSHCLI<T extends SSHService> implements BackgroundService.StateListener {

    protected abstract String appName();

    protected abstract String description();

    protected abstract SSHTransferProtocol sshTransferProtocol();

    protected abstract SSHTransferDirection sshTransferDirection();

    protected T service;

    protected MFSession session;

    protected boolean async = false;

    private Timer _timer;

    private String _currentActivity = null;

    protected SSHCLI(T service) {
        this.service = service;
    }

    protected void execute(String[] args) throws Throwable {

        MFConnectionSettings mfcs = new MFConnectionSettings(Applications.PROPERTIES_FILE);
        mfcs.setApp(Applications.APP_NAME);
        for (int i = 0; i < args.length;) {
            if (args[i].equals("--mf.host")) {
                mfcs.setServerHost(args[i + 1]);
                i += 2;
            } else if (args[i].equals("--mf.port")) {
                mfcs.setServerPort(Integer.parseInt(args[i + 1]));
                i += 2;
            } else if (args[i].equals("--mf.transport")) {
                mfcs.setServerTransport(args[i + 1]);
                i += 2;
            } else if (args[i].equals("--mf.auth")) {
                String auth = args[i + 1];
                String[] parts = auth.split(",");
                if (parts == null || parts.length != 3) {
                    throw new IllegalArgumentException("Invalid mf.auth: " + auth);
                }
                mfcs.setUserCredentials(parts[0], parts[1], parts[2]);
                i += 2;
            } else if (args[i].equals("--mf.token")) {
                mfcs.setToken(args[i + 1]);
                i += 2;
            } else if (args[i].equals("--mf.async")) {
                async = true;
                i++;
            } else if (args[i].equals("--ssh.host")) {
                service.setSSHServerHost(args[i + 1]);
                i += 2;
            } else if (args[i].equals("--ssh.port")) {
                service.setSSHServerPort(Integer.parseInt(args[i + 1]));
                i += 2;
            } else if (args[i].equals("--ssh.user")) {
                service.setSSHUsername(args[i + 1]);
                i += 2;
            } else if (args[i].equals("--ssh.password")) {
                service.setSSHPassword(args[i + 1]);
                i += 2;
            } else if (args[i].equals("--ssh.private-key")) {
                service.setSSHPrivateKey(args[i + 1]);
                i += 2;
            } else if (args[i].equals("--ssh.passphrase")) {
                service.setSSHPassphrase(args[i + 1]);
                i += 2;
            } else {
                i = parseArgs(args, i);
            }
        }
        mfcs.validate();
        this.session = new MFSession(mfcs);
        this.session.testAuthentication();
        long id = this.service.executeBackground(this.session);
        if (async) {
            System.out.println("Background service ID: " + id);
            System.out.println();
            System.out.println("Run \n    service.background.describe :id " + id + "\n to check its status.");
            session.discard();
        } else {
            _timer = new Timer();
            _timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    try {
                        BackgroundService.describe(session, id, SSHCLI.this);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 1000);
        }
    }

    public void updated(BackgroundService bs) throws Throwable {
        if (bs.finished()) {
            _timer.cancel();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(formatTime(bs.executionTime()));
        sb.append(" [service=").append(bs.name()).append(", id=").append(bs.id()).append(", state=").append(bs.state())
                .append(", completed=").append(bs.numberSubOperationsCompleted()).append("] ");
        String prefix = sb.toString();
        System.out.print(prefix);

        if (bs.currentActivity() != null && !ObjectUtil.equals(_currentActivity, bs.currentActivity())) {
            /*
             * current activity
             */
            _currentActivity = bs.currentActivity();
            System.out.println(_currentActivity);
        } else {
            /*
             * state
             */
            System.out.println(bs.state());
        }

        if (bs.state() == Task.State.COMPLETED && this.service.continueOnError()) {
            /*
             * completed
             */
            XmlDoc.Element re = bs.getResult(this.session);
            List<XmlDoc.Element> fes = re.elements();
            if (fes != null) {
                for (XmlDoc.Element fe : fes) {
                    XmlUtils.print(System.err, fe);
                }
            }
        }

        if (bs.finished()) {
            session.discard();
        }
        if (bs.failed()) {
            /*
             * error
             */
            if (bs.exception() != null) {
                throw bs.exception();
            } else {
                throw new Exception(bs.error());
            }
        } else if (bs.aborted()) {
            /*
             * aborted
             */
            throw new ServerClient.ExAborted();
        }
    }

    protected abstract int parseArgs(String[] args, int idx);

    public void printUsage(PrintStream s) {
        s.println();
        s.println("USAGE:");
        s.println(String.format("    %s <mediaflux-arguments> <%s-arguments>", appName(),
                sshTransferProtocol().toString()));
        s.println();
        s.println("DESCRIPTION:");
        s.println("    " + description());
        s.println();
        s.println("MEDIAFLUX ARGUMENTS:");
        printMeidafluxArgs(s);
        s.println();
        s.println(String.format("%s ARGUMENTS:", sshTransferProtocol().toString().toUpperCase()));
        printSshArgs(s);
        s.println();
        s.println("EXAMPLES:");
        printExamples(s);
    }

    protected void printMeidafluxArgs(PrintStream s) {
        //@formatter:off
        s.println("    --mf.host <host>                      Mediaflux server host.");
        s.println("    --mf.port <port>                      Mediaflux server port.");
        s.println("    --mf.transport <https|http|tcp/ip>    Mediaflux server transport, can be http, https or tcp/ip.");
        s.println("    --mf.auth <domain,user,password>      Mediaflux user credentials.");
        s.println("    --mf.token <token>                    Mediaflux secure identity token.");
        s.println("    --mf.async                            Executes the job in the background. The background service can be checked by executing service.background.describe service in Mediaflux Aterm.");
        //@formatter:on
    }

    protected void printSshArgs(PrintStream s) {
        String protocol = sshTransferProtocol() == SSHTransferProtocol.SFTP ? "SFTP" : "SSH";
        //@formatter:off
        s.println(String.format("    --ssh.host <host>                     %s server host.", protocol));
        s.println(String.format("    --ssh.port <port>                     %s server port. Optional. Defaults to 22.", protocol));
        s.println(String.format("    --ssh.user <username>                 %s user name.", protocol));
        s.println(String.format("    --ssh.password <password>             %s user's password.", protocol));
        s.println(String.format("    --ssh.private-key <private-key>       %s user's private key.", protocol));
        s.println(String.format("    --ssh.passphrase <passphrase>         Passphrase for the %s user's private key.", protocol));
        //@formatter:on
    }

    protected abstract void printExamples(PrintStream s);

    private static String formatTime(double durationSeconds) {
        long seconds = (long) durationSeconds;
        long minutes = seconds / 60L;
        seconds = seconds % 60L;
        long hours = minutes / 60L;
        minutes = minutes % 60L;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static <T extends SSHService> void execute(SSHCLI<T> cli, String[] args) throws Throwable {
        try {
            cli.execute(args);
        } catch (Throwable e) {
            e.printStackTrace();
            if (e instanceof IllegalArgumentException) {
                System.err.println("Error: " + e.getMessage());
                cli.printUsage(System.out);
            }
            System.exit(1);
        }
    }

}
