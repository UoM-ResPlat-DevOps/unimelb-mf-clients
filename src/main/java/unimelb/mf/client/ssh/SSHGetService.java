package unimelb.mf.client.ssh;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import arc.xml.XmlWriter;
import unimelb.mf.model.asset.worm.Worm;

public abstract class SSHGetService extends AbstractSSHService {

    protected Boolean readOnly = null;
    protected Worm worm = null;
    private Set<String> _srcPaths;
    private String _dstNamespace = null;

    public SSHGetService() {
        _srcPaths = new LinkedHashSet<String>();
    }

    @Override
    public void validateArgs() throws IllegalArgumentException {
        super.validateArgs();
        if (_srcPaths.isEmpty()) {
            throw new IllegalArgumentException("Missing path on the remote SSH server.");
        }
        if (_dstNamespace == null) {
            throw new IllegalArgumentException("Missing namespace path on the Mediaflux server.");
        }
    }

    @Override
    public void serviceArgs(XmlWriter w) throws Throwable {
        super.serviceArgs(w);
        if (this.readOnly != null) {
            w.add("read-only", this.readOnly);
        }
        if (this.worm != null) {
            w.push("worm");
            this.worm.save(w);
            w.pop();
        }
        if (_dstNamespace != null) {
            w.add("namespace", _dstNamespace);
        }
        if (_srcPaths != null) {
            for (String srcPath : _srcPaths) {
                w.add("path", srcPath);
            }
        }
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public void setWorm(Worm worm) {
        this.worm = worm;
    }

    public Set<String> srcPaths() {
        return Collections.unmodifiableSet(_srcPaths);
    }

    public void setSrcPath(String path) {
        _srcPaths.clear();
        if (path != null) {
            _srcPaths.add(path);
        }
    }

    public void setSrcPaths(Collection<String> paths) {
        _srcPaths.clear();
        if (paths != null) {
            _srcPaths.addAll(paths);
        }
    }

    public void addSrcPath(String path) {
        if (path != null) {
            _srcPaths.add(path);
        }
    }

    public String dstNamespace() {
        return _dstNamespace;
    }

    public void setDstNamespace(String namespace) {
        _dstNamespace = namespace;
    }

    public Worm worm() {
        return this.worm;
    }

}
