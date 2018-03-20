package unimelb.mf.client.ssh;

import arc.xml.XmlWriter;

public abstract class AbstractSSHService implements SSHService {

    protected SSHConnectionSettings sshSettings;
    protected boolean stopOnError = true;
    protected int retryOnError = 0;

    protected AbstractSSHService() {
        this.sshSettings = new SSHConnectionSettings();
    }

    @Override
    public void serviceArgs(XmlWriter w) throws Throwable {
        w.add("host", this.sshSettings.host());
        w.add("port", this.sshSettings.port());
        w.add("user", this.sshSettings.username());
        if (this.sshSettings.password() != null) {
            w.add("password", this.sshSettings.password());
        }
        if (this.sshSettings.privateKey() != null) {
            w.add("private-key", this.sshSettings.privateKey());
        }
        if (this.sshSettings.passphrase() != null) {
            w.add("passphrase", this.sshSettings.passphrase());
        }
        if (!this.stopOnError || this.retryOnError > 0) {
            w.add("on-error", new String[] { "retry", Integer.toString(this.retryOnError) },
                    this.stopOnError ? "stop" : "continue");
        }

    }

    @Override
    public void setStopOnError(boolean stopOnError) {
        this.stopOnError = stopOnError;
    }

    @Override
    public void setSSHServerHost(String sshServerHost) {
        this.sshSettings.setServerHost(sshServerHost);
    }

    @Override
    public void setSSHServerPort(int sshServerPort) {
        this.sshSettings.setServerPort(sshServerPort);
    }

    @Override
    public void setSSHUsername(String username) {
        this.sshSettings.setUsername(username);
    }

    @Override
    public void setSSHPassword(String password) {
        this.sshSettings.setPassword(password);
    }

    @Override
    public void setSSHBaseDirectory(String baseDir) {
        this.sshSettings.setBaseDirectory(baseDir);
    }

    @Override
    public void setSSHPrivateKey(String privateKey) {
        this.sshSettings.setPrivateKey(privateKey);
    }

    @Override
    public void setSSHPassphrase(String passphrase) {
        this.sshSettings.setPassphrase(passphrase);
    }

    @Override
    public final int retryOnError() {
        return this.retryOnError;
    }

    @Override
    public final void setRetryOnError(int retryOnError) {
        this.retryOnError = retryOnError;
    }

    @Override
    public void validateArgs() throws IllegalArgumentException {
        this.sshSettings.validate();
    }

    @Override
    public boolean stopOnError() {
        return this.stopOnError;
    }

    @Override
    public boolean continueOnError() {
        return !this.stopOnError;
    }

}
