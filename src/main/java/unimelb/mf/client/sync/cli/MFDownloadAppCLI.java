package unimelb.mf.client.sync.cli;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import unimelb.mf.client.sync.app.MFDownloadApp;
import unimelb.mf.client.sync.settings.MFDownloadSettings;
import unimelb.mf.client.sync.task.AssetDownloadTask.Unarchive;
import unimelb.mf.client.util.PathUtils;

public class MFDownloadAppCLI extends MFDownloadApp implements SyncCLI<MFDownloadSettings> {

    private Path _dstDir = null;

    protected MFDownloadAppCLI() {
        super();
    }

    @Override
    public final String synopsis(String appName) {
        return String.format("    %s [mediaflux-arguments] [--overwrite] [--directory <dst-dir>] <src-namespace>",
                appName);
    }

    @Override
    public int parseArg(MFDownloadSettings settings, String[] args, int i) {
        if ("--overwrite".equalsIgnoreCase(args[i])) {
            settings.setOverwrite(true);
            return i + 1;
        } else if ("--unarchive".equals(args[i])) {
            Unarchive unarchive = Unarchive.fromString(args[i + 1]);
            if (unarchive == null) {
                throw new IllegalArgumentException("Invalid argument --unarchive: " + args[i + 1]);
            }
            settings.setUnarchive(unarchive);
            return i + 2;
        } else if ("--directory".equals(args[i]) || "-d".equals(args[i])) {
            _dstDir = Paths.get(args[i + 1]);
            if (!Files.exists(_dstDir)) {
                throw new IllegalArgumentException("Invalid argument --directory '" + args[i] + "'",
                        new FileNotFoundException(_dstDir.toString()));
            }
            if (!Files.isDirectory(_dstDir)) {
                throw new IllegalArgumentException("Invalid argument: '" + args[i] + "' Not a directory.");
            }
            _dstDir = _dstDir.toAbsolutePath();
            return i + 2;
        } else {
            if (_dstDir == null) {
                _dstDir = Paths.get(System.getProperty("user.dir"));
            }
            String ns = args[i];
            String nsName = PathUtils.getLastComponent(ns);
            String dirName = PathUtils.getLastComponent(_dstDir.toString());
            boolean isDestinationParent = true;
            if (dirName != null && dirName.equals(nsName)) {
                isDestinationParent = false;
            }
            settings.addJob(_dstDir, ns, isDestinationParent);
            return i + 1;
        }
    }

    @Override
    public void printArgs(PrintStream ps) {
        //@formatter:off
        ps.println("    --overwrite                               Overwrite existing files.");
        ps.println("    --unarchive <aar|all>                     Unpack archives.");
        ps.println("    --directory | -d <dst-directory>          Destination directory. If not present, defaults to current working directory.");
        //@formatter:on
    }

    @Override
    public void printExamples(PrintStream ps) {
        //@formatter:off
        ps.println(String.format("    %s --mf.config ~/.Arcitecta/mflux.cfg --overwrite --directory ~/Downloads /mf-projects/my-project/data1", applicationName()));
        //@formatter:on
    }

    public static void main(String[] args) throws Throwable {

        MFDownloadAppCLI app = new MFDownloadAppCLI();
        System.out.println(app.settings().logDirectory());
        app.execute(app, args);
    }
}
