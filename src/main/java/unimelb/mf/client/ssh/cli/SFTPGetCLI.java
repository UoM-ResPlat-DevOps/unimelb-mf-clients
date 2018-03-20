package unimelb.mf.client.ssh.cli;

import unimelb.mf.client.ssh.SFTPGetService;
import unimelb.mf.client.ssh.SSHTransferProtocol;

public class SFTPGetCLI extends SSHGetCLI<SFTPGetService> {

    protected SFTPGetCLI() {
        super(new SFTPGetService());
    }

    @Override
    protected final String appName() {
        return "sftp-get";
    }

    @Override
    protected final SSHTransferProtocol sshTransferProtocol() {
        return SSHTransferProtocol.SFTP;
    }

    public static void main(String[] args) throws Throwable {
        execute(new SFTPGetCLI(), args);
    }

    @Override
    protected String description() {
        return "Import files from remote SFTP server to Mediaflux using sftp.";
    }
}
