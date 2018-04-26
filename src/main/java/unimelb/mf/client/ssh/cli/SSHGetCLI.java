package unimelb.mf.client.ssh.cli;

import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import unimelb.mf.client.ssh.SSHGetService;
import unimelb.mf.client.ssh.SSHTransferDirection;
import unimelb.mf.client.ssh.SSHTransferProtocol;
import unimelb.mf.model.asset.worm.Worm;

public abstract class SSHGetCLI<T extends SSHGetService> extends SSHCLI<T> {

    protected SSHGetCLI(T service) {
        super(service);
    }

    @Override
    protected final SSHTransferDirection sshTransferDirection() {
        return SSHTransferDirection.GET;
    }

    protected int parseArgs(String[] args, int i) {
        if (args[i].equalsIgnoreCase("--mf.namespace")) {
            this.service.setDstNamespace(args[i + 1]);
            return i + 2;
        } else if (args[i].equalsIgnoreCase("--mf.readonly")) {
            this.service.setReadOnly(true);
            return i + 1;
        } else if (args[i].equalsIgnoreCase("--mf.worm")) {
            if (this.service.worm() == null) {
                this.service.setWorm(new Worm());
            }
            return i + 1;
        } else if (args[i].equalsIgnoreCase("--mf.worm.expiry")) {
            if (this.service.worm() == null) {
                this.service.setWorm(new Worm());
            }
            try {
                this.service.worm().setExpiry(new SimpleDateFormat("d-MMM-yyyy").parse(args[i + 1]));
            } catch (ParseException pe) {
                throw new IllegalArgumentException("Invalid date value for --mf.worm.expiry: " + args[i + 1], pe);
            }
            return i + 2;
        } else if (args[i].equalsIgnoreCase("--ssh.path")) {
            this.service.addSrcPath(args[i + 1]);
            return i + 2;
        } else {
            throw new IllegalArgumentException("Unexpected argument: " + args[i]);
        }
    }

    protected void printMediafluxArgs(PrintStream s) {
        super.printMediafluxArgs(s);
        //@formatter:off
        s.println("    --mf.namespace <dst-namespace>        Destination namespace on Mediaflux.");
        s.println("    --mf.readonly                         Set the assets to be read-only.");
        s.println("    --mf.worm                             Set the assets to WORM state.");
        s.println("    --mf.worm.expiry <d-MMM-yyyy>         Set the assets WORM expiry date.");
        //@formatter:on
    }

    protected void printSshArgs(PrintStream s) {
        super.printSshArgs(s);
        String protocol = sshTransferProtocol() == SSHTransferProtocol.SFTP ? "SFTP" : "SSH";
        //@formatter:off
        s.println(String.format("    --ssh.path <src-path>                 Source path on remote %s server.", protocol));
        //@formatter:on
    }

    @Override
    protected void printExamples(PrintStream s) {
        //@formatter:off
        s.println(String.format("    The command below imports files from %s server into the specified Mediaflux asset namespace:", sshTransferProtocol()));
        s.println(String.format("         %s --mf.host mediaflux.your-domain.org --mf.port 443 --mf.transport 443 --mf.auth mf_domain,mf_user,MF_PASSWD --mf.namespace /path/to/dst-namespace --ssh.host ssh-server.your-domain.org --ssh.port 22 --ssh.user ssh_username --ssh.password SSH_PASSWD --ssh.path path/to/src-directory", appName()));
        
        s.println();
        //@formatter:on
    }
}
