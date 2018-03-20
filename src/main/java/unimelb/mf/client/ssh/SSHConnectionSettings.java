package unimelb.mf.client.ssh;

public class SSHConnectionSettings {

    private String _host;
    private int _port = 22;
    private String _username;
    private String _password;
    private String _privateKey;
    private String _passphrase;
    private String _baseDir;

    public SSHConnectionSettings() {

    }

    public SSHConnectionSettings setServerHost(String host) {
        _host = host;
        return this;
    }

    public SSHConnectionSettings setServerPort(int port) {
        _port = port;
        return this;
    }

    public SSHConnectionSettings setUsername(String username) {
        _username = username;
        return this;
    }

    public SSHConnectionSettings setPassword(String password) {
        _password = password;
        return this;
    }

    public SSHConnectionSettings setPrivateKey(String privateKey, String passphrase) {
        _privateKey = privateKey;
        _passphrase = passphrase;
        return this;
    }

    public SSHConnectionSettings setPrivateKey(String privateKey) {
        _privateKey = privateKey;
        return this;
    }

    public SSHConnectionSettings setPassphrase(String passphrase) {
        _passphrase = passphrase;
        return this;
    }

    public SSHConnectionSettings setBaseDirectory(String baseDirectory) {
        _baseDir = baseDirectory;
        return this;
    }

    public String host() {
        return _host;
    }

    public int port() {
        return _port;
    }

    public String username() {
        return _username;
    }

    public String password() {
        return _password;
    }

    public String privateKey() {
        return _privateKey;
    }

    public String passphrase() {
        return _passphrase;
    }

    public String baseDirectory() {
        return _baseDir;
    }

    public void validate() throws IllegalArgumentException {
        if (_host == null) {
            throw new IllegalArgumentException("Missing SSH server host.");
        }
        if (_port <= 0 || _port > 65535) {
            throw new IllegalArgumentException("Invalid SSH server port: " + _port);
        }
        if (_username == null) {
            throw new IllegalArgumentException("Missing SSH username.");
        }
        if (_password == null && _privateKey == null) {
            throw new IllegalArgumentException("Missing SSH password or private key. Expects at least one.");
        }
    }

}
