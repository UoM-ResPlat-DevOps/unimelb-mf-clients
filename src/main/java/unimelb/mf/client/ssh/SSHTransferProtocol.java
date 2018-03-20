package unimelb.mf.client.ssh;

public enum SSHTransferProtocol {

    SCP, SFTP;

    public final String toString() {
        return name().toLowerCase();
    }

}
