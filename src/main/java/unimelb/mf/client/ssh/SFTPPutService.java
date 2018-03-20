package unimelb.mf.client.ssh;

public class SFTPPutService extends SSHPutService {

    @Override
    public String name() {
        return "daris.sftp.put";
    }

    @Override
    public final SSHTransferProtocol sshTransferProtocol() {
        return SSHTransferProtocol.SFTP;
    }

    @Override
    public final SSHTransferDirection sshTransferDirection() {
        return SSHTransferDirection.PUT;
    }
}
