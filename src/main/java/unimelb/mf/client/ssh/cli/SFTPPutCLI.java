package unimelb.mf.client.ssh.cli;

import unimelb.mf.client.ssh.SFTPPutService;
import unimelb.mf.client.ssh.SSHTransferProtocol;

public class SFTPPutCLI extends SSHPutCLI<SFTPPutService> {

    protected SFTPPutCLI() {
        super(new SFTPPutService());
    }

    @Override
    protected final String appName() {
        return "sftp-put";
    }

    @Override
    protected final SSHTransferProtocol sshTransferProtocol() {
        return SSHTransferProtocol.SFTP;
    }

    public static void main(String[] args) throws Throwable {
        execute(new SFTPPutCLI(), args);
    }

    @Override
    protected String description() {
        return "Export Mediaflux assets to remote SFTP server using sftp.";
    }

}
