package unimelb.mf.client.session;

import java.io.Console;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import arc.mf.client.AuthenticationDetails;
import arc.mf.client.RemoteServer;
import arc.mf.client.RequestOptions;
import arc.mf.client.ServerClient;
import arc.mf.client.ServerClient.Input;
import arc.utils.AbortableOperationHandler;
import arc.utils.CanAbort;
import arc.xml.XmlDoc;
import unimelb.mf.client.util.HasAbortableOperation;

public class MFSession {

    public static final int DEFAULT_CONNECT_RETRY_TIMES = 5;
    public static final int DEFAULT_CONNECT_RETRY_INTERVAL = 100;
    public static final int DEFAULT_EXECUTE_RETRY_TIMES = 1;
    public static final int DEFAULT_EXECUTE_RETRY_INTERVAL = 0;

    protected MFConnectionSettings settings;

    private RemoteServer _rs;
    private AuthenticationDetails _auth;
    private String _sessionId;
    private Timer _timer;

    public MFSession(MFConnectionSettings settings) {
        this.settings = settings;
    }

    public synchronized String sessionId() {
        return _sessionId;
    }

    private synchronized void setSessionId(String sessionId) {
        _sessionId = sessionId;
    }

    public ServerClient.Connection connect() throws Throwable {
        return connect(this.settings.connectRetryTimes());
    }

    private synchronized ServerClient.Connection connect(int retryTimes) throws Throwable {
        if (_rs == null) {
            _rs = new RemoteServer(this.settings.serverHost(), this.settings.serverPort(), this.settings.useHttp(),
                    this.settings.encrypt());
            _rs.setConnectionPooling(true);
        }
        ServerClient.Connection cxn = null;
        try {
            cxn = _rs.open();
            String sessionId = sessionId();
            if (sessionId != null) {
                cxn.reconnect(sessionId);
            } else {
                setSessionId(cxn.connect(_auth == null ? this.settings.authenticationDetails() : _auth));
            }
            if (_auth == null) {
                _auth = cxn.authenticationDetails();
            }
            return cxn;
        } catch (java.net.ConnectException ce) {
            if (retryTimes > 0) {
                System.out.println("Failed to connect. Retrying ...");
                if (this.settings.connectRetryInterval() > 0) {
                    Thread.sleep(this.settings.connectRetryInterval());
                }
                return connect(--retryTimes);
            } else {
                throw ce;
            }
        }
    }

    public XmlDoc.Element execute(String service, String args, ServerClient.Input input, ServerClient.Output output,
            HasAbortableOperation abortable) throws Throwable {
        return execute(service, args, input == null ? null : Arrays.asList(input), output, abortable);
    }

    public XmlDoc.Element execute(String service, String args, List<ServerClient.Input> inputs,
            ServerClient.Output output, HasAbortableOperation abortable) throws Throwable {
        ServerClient.Connection cxn = connect();
        try {
            return execute(cxn, service, args, inputs, output, abortable, this.settings.executeRetryTimes());
        } finally {
            cxn.close();
        }
    }

    public void testAuthentication() throws Throwable {
        ServerClient.Connection cxn = null;
        try {
            cxn = connect();
        } finally {
            if (cxn != null) {
                cxn.close();
            }
        }
    }

    public void doConsoleLogon() throws Throwable {
        Console console = System.console();
        if (console == null) {
            throw new UnsupportedOperationException("Failed to open system console");
        }
        if (this.settings.serverHost() == null) {
            this.settings.readServerHostFromConsole(console);
        } else {
            console.printf("Host: %s%n", this.settings.serverHost());
        }
        if (this.settings.serverPort() <= 0) {
            this.settings.readServerPortFromConsole(console);
        } else {
            console.printf("Port: %d%n", this.settings.serverPort());
        }
        if (this.settings.serverTransport() == null) {
            this.settings.readServerTransportFromConsole(console);
        } else {
            console.printf("Transport: %s%n", this.settings.serverTransport());
        }
        if (this.settings.domain() == null) {
            this.settings.readDomainFromConsole(console);
        } else {
            console.printf("Domain: %s%n", this.settings.domain());
        }
        if (this.settings.user() == null) {
            this.settings.readUserFromConsole(console);
        } else {
            console.printf("User: %s%n", this.settings.user());
        }
        this.settings.readPasswordFromConsole(console);

        try {
            testAuthentication();
        } catch (Throwable e) {
            console.printf("%n");
            console.printf("Error: %s%n", e.getMessage());
            console.printf("%n");
            doConsoleLogon();
        }
    }

    protected XmlDoc.Element execute(ServerClient.Connection cxn, String service, String args,
            List<ServerClient.Input> inputs, ServerClient.Output output, HasAbortableOperation abortable,
            int retryTimes) throws Throwable {
        try {
            RequestOptions ops = new RequestOptions();
            ops.setAbortHandler(new AbortableOperationHandler() {

                @Override
                public void finished(CanAbort ca) {
                    if (abortable != null) {
                        abortable.setAbortableOperation(null);
                    }
                }

                @Override
                public void started(CanAbort ca) {
                    if (abortable != null) {
                        abortable.setAbortableOperation(ca);
                    }
                }
            });
            return cxn.executeMultiInput(null, service, args, inputs, output, ops);
        } catch (ServerClient.ExSessionInvalid si) {
            if (_auth != null && retryTimes > 0) {
                System.out.println("Session invalid. Try re-authenticating...");
                if (this.settings.executeRetryInterval() > 0) {
                    Thread.sleep(this.settings.executeRetryInterval());
                }
                setSessionId(cxn.connect(_auth));
                return execute(cxn, service, args, inputs, output, abortable, --retryTimes);
            }
            throw si;
        }
    }

    public XmlDoc.Element execute(String service, String args, List<ServerClient.Input> inputs,
            ServerClient.Output output) throws Throwable {
        return execute(service, args, inputs, output, null);
    }

    public XmlDoc.Element execute(String service, String args, List<ServerClient.Input> inputs) throws Throwable {
        return execute(service, args, inputs, null, null);
    }

    public XmlDoc.Element execute(String service, String args, ServerClient.Input input, ServerClient.Output output)
            throws Throwable {
        return execute(service, args, input, output, null);
    }

    public XmlDoc.Element execute(String service, String args, ServerClient.Input input) throws Throwable {
        return execute(service, args, input, null, null);
    }

    public XmlDoc.Element execute(String service, String args, ServerClient.Output output) throws Throwable {
        return execute(service, args, (List<Input>) null, output, null);
    }

    public XmlDoc.Element execute(String service, String args) throws Throwable {
        return execute(service, args, (List<Input>) null, null, null);
    }

    public XmlDoc.Element execute(String service) throws Throwable {
        return execute(service, null, (List<Input>) null, null, null);
    }

    public void discard() {
        if (_rs != null) {
            _rs.discard();
        }
        stopPingServerPeriodically();
    }

    /**
     * 
     * @param period
     *            time in milliseconds.
     */
    public void startPingServerPeriodically(int period) {
        stopPingServerPeriodically();
        _timer = new Timer();
        _timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                try {
                    execute("server.ping");
                } catch (Throwable e) {
                    e.printStackTrace(System.err);
                }
            }
        }, 0, period);
    }

    public void stopPingServerPeriodically() {
        if (_timer != null) {
            _timer.cancel();
            _timer = null;
        }
    }

    public String serverHost() {
        return this.settings.serverHost();
    }

}
