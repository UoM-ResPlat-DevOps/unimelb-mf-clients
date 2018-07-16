package unimelb.mf.client.sync.settings;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import unimelb.mf.client.session.MFSession;
import unimelb.mf.client.sync.settings.Action.Direction;
import unimelb.mf.client.util.AssetNamespaceUtils;
import unimelb.mf.client.util.PathPattern;
import unimelb.mf.client.util.PathUtils;

public class Job {

    private Action _action;
    private String _ns;
    private Path _dir;
    private List<String> _includes;
    private List<String> _excludes;

    public Job(Action action, Path dir, String ns) {
        this(action, dir, ns, false, null, null);
    }

    public Job(Action action, Path dir, String ns, boolean isParentNS) {
        this(action, dir, ns, isParentNS, null, null);
    }

    public Job(Action action, Path dir, String ns, boolean isParentNS, Collection<String> includes,
            Collection<String> excludes) {
        _action = action;
        _dir = dir;
        _ns = isParentNS ? PathUtils.joinSystemIndependent(ns, dir.getFileName().toString()) : ns;
        if (includes != null && !includes.isEmpty()) {
            _includes = new ArrayList<String>();
            _includes.addAll(includes);
        }

        if (excludes != null && !excludes.isEmpty()) {
            _excludes = new ArrayList<String>();
            _excludes.addAll(excludes);
        }
    }

    public Job(Action action, String ns, Path dir) {
        this(action, ns, dir, false, null, null);
    }

    public Job(Action action, String ns, Path dir, boolean isParentDir) {
        this(action, ns, dir, isParentDir, null, null);
    }

    public Job(Action action, String ns, Path dir, boolean isParentDir, Collection<String> includes,
            Collection<String> excludes) {
        _action = action;
        _dir = isParentDir ? Paths.get(dir.toString(), PathUtils.getLastComponent(ns)) : dir;
        _ns = ns;
        if (includes != null && !includes.isEmpty()) {
            _includes = new ArrayList<String>();
            _includes.addAll(includes);
        }

        if (excludes != null && !excludes.isEmpty()) {
            _excludes = new ArrayList<String>();
            _excludes.addAll(excludes);
        }
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

    public final List<String> sourcePathExcludes() {
        if (_excludes != null) {
            Collections.unmodifiableList(_excludes);
        }
        return null;
    }

    public final List<String> sourcePathIncludes() {
        if (_includes != null) {
            Collections.unmodifiableList(_includes);
        }
        return null;
    }
    
    public boolean sourcePathMatches(Path path) {
        return sourcePathMatches(PathUtils.toSystemIndependent(path.toAbsolutePath().toString()));
    }

    public boolean sourcePathMatches(String path) {

        String src = _action.direction() == Direction.UP
                ? PathUtils.toSystemIndependent(_dir.toAbsolutePath().toString())
                : _ns;
        if (!PathUtils.isOrIsDescendant(path, src)) {
            return false;
        }
        boolean haveIncludePatterns = _includes != null && !_includes.isEmpty();
        boolean haveExcludePatterns = _excludes != null && !_excludes.isEmpty();
        if (!haveIncludePatterns && !haveExcludePatterns) {
            return true;
        }
        String relativePath = PathUtils.getRelativePathSI(src, path);
        if (haveIncludePatterns) {
            if (haveExcludePatterns) {
                return matchesAny(relativePath, _includes) && !matchesAny(relativePath, _excludes);
            } else {
                return matchesAny(relativePath, _includes);
            }
        } else {
            if (haveExcludePatterns) {
                return !matchesAny(relativePath, _excludes);
            } else {
                return true;
            }
        }
    }

    static boolean matchesAny(String relativePath, Collection<String> patterns) {
        for (String pattern : patterns) {
            String regex = PathPattern.toRegEx(pattern);
            if (relativePath.matches(regex)) {
                return true;
            }
        }
        return false;
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
