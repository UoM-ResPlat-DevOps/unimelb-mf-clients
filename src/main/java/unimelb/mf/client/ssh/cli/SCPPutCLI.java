package unimelb.mf.client.ssh.cli;

import unimelb.mf.client.ssh.SCPPutService;
import unimelb.mf.client.ssh.SSHTransferProtocol;

public class SCPPutCLI extends SSHPutCLI<SCPPutService> {

    protected SCPPutCLI() {
        super(new SCPPutService());
    }

    @Override
    protected final String appName() {
        return "scp-put";
    }

    @Override
    protected final SSHTransferProtocol sshTransferProtocol() {
        return SSHTransferProtocol.SCP;
    }

    public static void main(String[] args) throws Throwable {
        execute(new SCPPutCLI(), args);
    }

    @Override
    protected String description() {
        return "Export Mediaflux assets to remote SSH server using scp.";
    }

}
