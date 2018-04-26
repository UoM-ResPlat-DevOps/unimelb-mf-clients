package unimelb.mf.client.session;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Path;
import java.util.Properties;

import arc.mf.client.AuthenticationDetails;
import arc.xml.XmlDoc;

/**
 * See src/main/config/mf-sync-properties.sample.xml
 * 
 * @author wliu5
 *
 */
public class MFConnectionSettings {

    private String _serverHost = null;
    private String _serverTransport = null;
    private int _serverPort;

    private int _connectRetryTimes = MFSession.DEFAULT_CONNECT_RETRY_TIMES;
    private int _connectRetryInterval = MFSession.DEFAULT_CONNECT_RETRY_INTERVAL;
    private int _executeRetryTimes = MFSession.DEFAULT_EXECUTE_RETRY_TIMES;
    private int _executeRetryInterval = MFSession.DEFAULT_EXECUTE_RETRY_INTERVAL;

    private String _app = null;
    private String _domain = null;
    private String _user = null;
    private String _password = null;
    private String _token = null;
    private String _sessionKey = null;

    public MFConnectionSettings() throws Throwable {

    }

    public MFConnectionSettings(Path xmlFile) throws Throwable {
        this(xmlFile.toFile());
    }

    public MFConnectionSettings(File xmlFile) throws Throwable {
        if (xmlFile != null && xmlFile.exists()) {
            loadFromXmlFile(xmlFile);
        }
    }

    public MFConnectionSettings(XmlDoc.Element pe) throws Throwable {
        if (pe != null) {
            loadFromXml(pe);
        }
    }

    private static String parseServerTransport(String proto) throws Exception {
        if (proto != null) {
            if ("http".equalsIgnoreCase(proto)) {
                return "http";
            }
            if ("https".equalsIgnoreCase(proto)) {
                return "https";
            }
            if (proto.toLowerCase().startsWith("tcp")) {
                return "tcp/ip";
            }
            throw new Exception("Invalid transport protocol: " + proto);
        }
        return null;
    }

    public AuthenticationDetails authenticationDetails() {
        if (_token == null && (_domain == null || _user == null || _password == null)) {
            return null;
        } else {
            if (_token == null) {
                return new AuthenticationDetails(_app, _domain, _user, _password);
            } else {
                return new AuthenticationDetails(_app, _token);
            }
        }
    }

    public String domain() {
        return _domain;
    }

    public boolean encrypt() {
        return "https".equalsIgnoreCase(_serverTransport);
    }

    public boolean hasAuthenticationDetails() {
        return hasToken() || hasUserCredentials();
    }

    public boolean hasSessionKey() {
        return _sessionKey != null;
    }

    public boolean hasToken() {
        return _token != null;
    }

    public boolean hasUserCredentials() {
        return _domain != null && _user != null && _password != null;
    }

    private void loadFromXml(XmlDoc.Element pe) throws Throwable {

        // @formatter:off
        _serverHost = pe.value("server/host");
        _serverTransport = parseServerTransport(pe.value("server/protocol"));
        _serverPort = pe.intValue("server/port", 0);
        if (_serverPort == 0) {
            if ("http".equalsIgnoreCase(_serverTransport)) {
                _serverPort = 80;
            }
            if ("https".equalsIgnoreCase(_serverTransport)) {
                _serverPort = 443;
            }
            if ("tcp/ip".equalsIgnoreCase(_serverTransport)) {
                _serverPort = 1967;
            }
        }
        _connectRetryTimes = pe.intValue("server/session/connectRetryTimes", MFSession.DEFAULT_CONNECT_RETRY_TIMES);
        _connectRetryInterval = pe.intValue("server/session/connectRetryInterval", MFSession.DEFAULT_CONNECT_RETRY_INTERVAL);
        _executeRetryTimes = pe.intValue("server/session/executeRetryTimes", MFSession.DEFAULT_EXECUTE_RETRY_TIMES);
        _executeRetryInterval = pe.intValue("server/session/executeRetryInterval", MFSession.DEFAULT_EXECUTE_RETRY_INTERVAL);
        
        _app = pe.value("credential/app");
        _domain = pe.value("credential/domain");
        _user = pe.value("credential/user");
        _password = pe.value("credential/password");
        _token = pe.value("credential/token");
        _sessionKey = pe.value("credential/sessionKey");
        // @formatter:on

    }

    public void loadFromXmlFile(File xmlFile) throws Throwable {
        Reader reader = new BufferedReader(new FileReader(xmlFile));
        try {
            XmlDoc.Element pe = new XmlDoc().parse(reader);
            loadFromXml(pe);
        } finally {
            reader.close();
        }
    }

    public String password() {
        return _password;
    }

    public String serverHost() {
        return _serverHost;
    }

    public int serverPort() {
        return _serverPort;
    }

    public String serverTransport() {
        return _serverTransport;
    }

    public String sessionKey() {
        return _sessionKey;
    }

    public MFConnectionSettings setApp(String app) {
        _app = app;
        return this;
    }

    public MFConnectionSettings setDomain(String domain) {
        _domain = domain;
        return this;
    }

    public MFConnectionSettings setPassword(String password) {
        _password = password;
        return this;
    }

    public MFConnectionSettings setServer(String host, int port, boolean useHttp, boolean encrypt) {
        _serverHost = host;
        _serverPort = port;
        if (useHttp) {
            _serverTransport = encrypt ? "https" : "http";
        } else {
            _serverTransport = "tcp/ip";
        }
        return this;
    }

    public MFConnectionSettings setServer(String host, int port, String transport) throws Exception {
        _serverHost = host;
        _serverPort = port;
        setServerTransport(transport);
        return this;
    }

    public MFConnectionSettings setServerHost(String host) {
        _serverHost = host;
        return this;
    }

    public MFConnectionSettings setServerPort(int port) {
        _serverPort = port;
        return this;
    }

    public MFConnectionSettings setServerTransport(String transport) throws Exception {
        _serverTransport = parseServerTransport(transport);
        return this;
    }

    public MFConnectionSettings setSessionKey(String sessionKey) {
        _sessionKey = sessionKey;
        return this;
    }

    public MFConnectionSettings setToken(String token) {
        _token = token;
        return this;
    }

    public MFConnectionSettings setUser(String user) {
        _user = user;
        return this;
    }

    public MFConnectionSettings setUserCredentials(String domain, String user, String password) {
        _domain = domain;
        _user = user;
        _password = password;
        return this;
    }

    public String token() {
        return _token;
    }

    public boolean useHttp() {
        return "https".equalsIgnoreCase(_serverTransport) || "http".equalsIgnoreCase(_serverTransport);
    }

    public String user() {
        return _user;
    }

    public MFConnectionSettings setConnectRetryTimes(int retryTimes) {
        _connectRetryTimes = retryTimes;
        return this;
    }

    public int connectRetryTimes() {
        return _connectRetryTimes;
    }

    public MFConnectionSettings setConnectRetryInterval(int millisecs) {
        _connectRetryInterval = millisecs;
        return this;
    }

    public int connectRetryInterval() {
        return _connectRetryInterval;
    }

    public MFConnectionSettings setExecuteRetryTimes(int retryTimes) {
        _executeRetryTimes = retryTimes;
        return this;
    }

    public int executeRetryTimes() {
        return _executeRetryTimes;
    }

    public MFConnectionSettings setExecuteRetryInterval(int millisecs) {
        _executeRetryInterval = millisecs;
        return this;
    }

    public int executeRetryInterval() {
        return _executeRetryInterval;
    }

    public void validate() throws Throwable {
        if (_serverHost == null) {
            throw new IllegalArgumentException("Missing mf.host");
        }
        if (_serverPort <= 0) {
            throw new IllegalArgumentException("Missing mf.port");
        }
        if (serverTransport() == null) {
            throw new IllegalArgumentException("Missing mf.transport");
        }
        if (_token == null && (_domain == null || _user == null || _password == null) && _sessionKey == null) {
            throw new IllegalArgumentException("Missing/Incomplete mf.token or mf.auth");
        }
    }

    public void loadFromConfigFile(String configFile) throws Exception {
        File cf = new File(configFile);
        Properties props = new Properties();
        InputStream in = new BufferedInputStream(new FileInputStream(cf));
        try {
            props.load(in);
            if (props.containsKey("host")) {
                setServerHost(props.getProperty("host"));
            }
            if (props.containsKey("port")) {
                int port = Integer.parseInt(props.getProperty("port"));
                setServerPort(port);
            }
            if (props.containsKey("transport")) {
                setServerTransport(props.getProperty("transport"));
            }
            if (props.containsKey("domain")) {
                setDomain(props.getProperty("domain"));
            }
            if (props.containsKey("user")) {
                setUser(props.getProperty("user"));
            }
            if (props.containsKey("password")) {
                setPassword(props.getProperty("password"));
            }
            if (props.containsKey("token")) {
                setToken(props.getProperty("token"));
            }
        } finally {
            in.close();
        }
    }

}
