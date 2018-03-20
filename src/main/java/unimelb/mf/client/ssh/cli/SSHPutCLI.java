package unimelb.mf.client.ssh.cli;

import java.io.PrintStream;

import unimelb.mf.client.ssh.SSHPutService;
import unimelb.mf.client.ssh.SSHTransferDirection;
import unimelb.mf.client.ssh.SSHTransferProtocol;

public abstract class SSHPutCLI<T extends SSHPutService> extends SSHCLI<T> {

    protected SSHPutCLI(T service) {
        super(service);
    }

    @Override
    protected final SSHTransferDirection sshTransferDirection() {
        return SSHTransferDirection.PUT;
    }

    protected int parseArgs(String[] args, int i) {
        if (args[i].equalsIgnoreCase("--mf.namespace")) {
            this.service.addNamespace(args[i + 1]);
            return i + 2;
        } else if (args[i].equalsIgnoreCase("--mf.unarchive")) {
            this.service.setUnarchiveContents(true);
            return i + 1;
        } else if (args[i].equalsIgnoreCase("--ssh.directory")) {
            this.service.setDstDirectory(args[i + 1]);
            return i + 2;
        } else {
            throw new IllegalArgumentException("Unexpected argument: " + args[i]);
        }
    }

    protected void printMeidafluxArgs(PrintStream s) {
        super.printMeidafluxArgs(s);
        //@formatter:off
        s.println("    --mf.namespace <src-namespace>        Source namespace on Mediaflux.");
        s.println("    --mf.unarchive                        Unpack asset contents.");
        //@formatter:on
    }

    protected void printSshArgs(PrintStream s) {
        super.printSshArgs(s);
        String protocol = sshTransferProtocol() == SSHTransferProtocol.SFTP ? "SFTP" : "SSH";
        //@formatter:off
        s.println(String.format("    --ssh.directory <dst-directory>       Destination directory on remote %s server.", protocol));
        //@formatter:on
    }

}
