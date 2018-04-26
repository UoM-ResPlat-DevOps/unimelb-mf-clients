package unimelb.mf.client.sync.settings;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import unimelb.mf.client.sync.app.MFCheckApp;
import unimelb.mf.client.util.PathUtils;

public class MFCheckSettings extends Settings {

    private Path _outputDir;
    private String _outputFileNamePrefix;

    public MFCheckSettings(Direction direction) {
        super(Action.CHECK, direction, null);
        _outputDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        _outputFileNamePrefix = MFCheckApp.APP_NAME;
    }

    @Override
    public final void setAction(Action action) {
        assert action == Action.CHECK;
        super.setAction(action);
    }

    @Override
    public final void setDirection(Direction direction) {
        super.setDirection(direction);
    }

    public Path outputDirectory() {
        return _outputDir;
    }

    public String outputFileNamePrefix() {
        return _outputFileNamePrefix;
    }

    public void setOutputPath(String path) {
        path = PathUtils.toSystemIndependent(path);
        Path dir = Paths.get(PathUtils.getParentPath(path));
        if (Files.exists(dir)) {
            throw new IllegalArgumentException("Directory: '" + dir.toString() + "' does not exist.",
                    new FileNotFoundException(dir.toString()));
        }
        if (Files.isDirectory(dir)) {
            throw new IllegalArgumentException("'" + dir.toString() + "' is not a directory.");
        }
        _outputDir = dir.toAbsolutePath();
        _outputFileNamePrefix = PathUtils.getFileName(path);
        if (_outputFileNamePrefix.matches("[\\w\\-\\.\\ ]+")) {
            throw new IllegalArgumentException("Invalid file name: '" + _outputFileNamePrefix + "'");
        }
    }

}
