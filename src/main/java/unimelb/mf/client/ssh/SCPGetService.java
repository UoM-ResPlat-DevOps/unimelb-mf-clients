package unimelb.mf.client.ssh;

public class SCPGetService extends SSHGetService {

    @Override
    public final String name() {
        return "daris.scp.get";
    }

    @Override
    public final SSHTransferProtocol sshTransferProtocol() {
        return SSHTransferProtocol.SCP;
    }

    @Override
    public final SSHTransferDirection sshTransferDirection() {
        return SSHTransferDirection.GET;
    }

}
