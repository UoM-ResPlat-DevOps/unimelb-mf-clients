package unimelb.mf.client.ssh.cli;

import unimelb.mf.client.ssh.SCPGetService;
import unimelb.mf.client.ssh.SSHTransferProtocol;

public class SCPGetCLI extends SSHGetCLI<SCPGetService> {

    protected SCPGetCLI() {
        super(new SCPGetService());
    }

    @Override
    protected final String appName() {
        return "scp-get";
    }

    @Override
    protected final SSHTransferProtocol sshTransferProtocol() {
        return SSHTransferProtocol.SCP;
    }

    public static void main(String[] args) throws Throwable {
        execute(new SCPGetCLI(), args);
    }

    @Override
    protected String description() {
        return "Import files from remote SSH server to Mediaflux using scp.";
    }

}
