package unimelb.mf.client.ssh;

import unimelb.mf.client.session.MFService;

public interface SSHService extends MFService {

    void setSSHServerHost(String sshServerHost);

    void setSSHServerPort(int sshServerPort);

    void setSSHUsername(String username);

    void setSSHPassword(String password);

    void setSSHBaseDirectory(String baseDir);

    void setSSHPrivateKey(String privateKey);

    void setSSHPassphrase(String passphrase);

    void setStopOnError(boolean stopOnError);
    
    boolean stopOnError();
    
    boolean continueOnError();

    void setRetryOnError(int retryOnError);

    default int retryOnError() {
        return 0;
    }

    SSHTransferProtocol sshTransferProtocol();
    
    SSHTransferDirection sshTransferDirection();

}
