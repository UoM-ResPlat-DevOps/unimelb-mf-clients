package unimelb.mf.client.ssh;

public class SCPPutService extends SSHPutService {

    @Override
    public final String name() {
        return "unimelb.scp.put";
    }

    @Override
    public final SSHTransferProtocol sshTransferProtocol() {
        return SSHTransferProtocol.SCP;
    }

    @Override
    public final SSHTransferDirection sshTransferDirection() {
        return SSHTransferDirection.PUT;
    }
}
