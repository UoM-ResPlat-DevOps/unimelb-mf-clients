package unimelb.mf.client.ssh;

public class SFTPGetService extends SSHGetService {

    @Override
    public final String name() {
        return "unimelb.sftp.get";
    }

    @Override
    public final SSHTransferProtocol sshTransferProtocol() {
        return SSHTransferProtocol.SFTP;
    }

    @Override
    public final SSHTransferDirection sshTransferDirection() {
        return SSHTransferDirection.GET;
    }
}
