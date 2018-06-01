package unimelb.mf.client.sync.settings;

import java.nio.file.Path;
import java.nio.file.Paths;

import unimelb.mf.client.session.MFSession;
import unimelb.mf.client.util.AssetNamespaceUtils;
import unimelb.mf.client.util.PathUtils;

public class Job {

    private Action _action;
    private String _ns;
    private Path _dir;

    public Job(Action action, Path dir, String ns) {
        this(action, dir, ns, false);
    }

    public Job(Action action, Path dir, String ns, boolean isParentNS) {
        _action = action;
        _dir = dir;
        _ns = isParentNS ? PathUtils.joinSystemIndependent(ns, dir.getFileName().toString()) : ns;
    }

    public Job(Action action, String ns, Path dir) {
        this(action, ns, dir, false);
    }

    public Job(Action action, String ns, Path dir, boolean isParentDir) {
        _action = action;
        _dir = isParentDir ? Paths.get(dir.toString(), PathUtils.getLastComponent(ns)) : dir;
        _ns = ns;
    }

    public final Action action() {
        return _action;
    }

    public final String namespace() {
        return _ns;
    }

    public final Path directory() {
        return _dir;
    }

    public boolean matchPath(Path path) {
        // TODO
        return true;
    }

    public String computeAssetPath(Path file) {
        return computeAssetPath(file, _dir, _ns);
    }

    public Path computeFilePath(String assetPath) {
        return computeFilePath(assetPath, _ns, _dir);
    }

    public static String computeAssetPath(Path file, Path dir, String assetNamespace) {
        String relativePath = PathUtils.getRelativePathSI(dir, file);
        String assetPath = PathUtils.joinSystemIndependent(assetNamespace, relativePath);
        return assetPath;
    }

    public static Path computeFilePath(String assetPath, String assetNamespace, Path dir) {
        String relativePath = PathUtils.getRelativePathSI(assetNamespace, assetPath);
        Path file = Paths
                .get(PathUtils.joinSystemIndependent(PathUtils.toSystemIndependent(dir.toString()), relativePath));
        return file;
    }

    public boolean namespaceExists(MFSession session) throws Throwable {
        return AssetNamespaceUtils.assetNamespaceExists(session, namespace());
    }

}
