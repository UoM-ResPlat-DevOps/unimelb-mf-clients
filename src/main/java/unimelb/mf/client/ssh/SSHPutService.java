package unimelb.mf.client.ssh;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import arc.xml.XmlWriter;

public abstract class SSHPutService extends AbstractSSHService {

    private Map<String, Boolean> _cids;
    private Set<String> _assetIds;
    private Set<String> _namespaces;
    private String _where;
    private String _expr;
    private Boolean _unarchive;

    private String _directory;

    public SSHPutService() {
        _cids = new LinkedHashMap<String, Boolean>();
        _assetIds = new LinkedHashSet<String>();
        _namespaces = new LinkedHashSet<String>();
        _where = null;
    }

    @Override
    public void validateArgs() throws IllegalArgumentException {
        super.validateArgs();
        if (_cids.isEmpty() && _assetIds.isEmpty() && _namespaces.isEmpty() && _where == null) {
            throw new IllegalArgumentException(
                    "Missing source asset specifications, either id, cid, namespace or query must be specified.");
        }
    }

    @Override
    public void serviceArgs(XmlWriter w) throws Throwable {
        super.serviceArgs(w);
        if (!_cids.isEmpty()) {
            Set<String> cids = _cids.keySet();
            for (String cid : cids) {
                w.add("cid", new String[] { "recursive", Boolean.toString(_cids.get(cid)) }, cid);
            }
        }
        if (!_assetIds.isEmpty()) {
            for (String assetId : _assetIds) {
                w.add("id", assetId);
            }
        }
        if (!_namespaces.isEmpty()) {
            for (String namespace : _namespaces) {
                w.add("namespace", namespace);
            }
        }
        if (_where != null) {
            w.add("where", _where);
        }
        if (_expr != null) {
            w.add("expr", _expr);
        }
        if (_unarchive != null) {
            w.add("unarchive", _unarchive);
        }
        w.add("directory", _directory);
    }

    public void setCiteableIds(boolean recursive, String... cids) {
        _cids.clear();
        if (cids != null) {
            for (String cid : cids) {
                _cids.put(cid, recursive);
            }
        }
    }

    public void addCiteableId(String cid, boolean recursive) {
        if (cid != null) {
            _cids.put(cid, recursive);
        }
    }

    public void addCiteableId(String cid) {
        addCiteableId(cid, true);
    }

    public void setAssetIds(String... assetIds) {
        _assetIds.clear();
        if (assetIds != null) {
            for (String assetId : assetIds) {
                _assetIds.add(assetId);
            }
        }
    }

    public void addAssetId(String assetId) {
        if (assetId != null) {
            _assetIds.add(assetId);
        }
    }

    public void setNamespaces(String... namespaces) {
        _namespaces.clear();
        if (namespaces != null) {
            for (String namespace : namespaces) {
                _assetIds.add(namespace);
            }
        }
    }

    public void addNamespace(String namespace) {
        if (namespace != null) {
            _namespaces.add(namespace);
        }
    }

    public void setWhere(String where) {
        _where = where;
    }

    public void setOutputPathExpression(String expr) {
        _expr = expr;
    }

    public void setDstDirectory(String directory) {
        _directory = directory;
    }

    public String dstDirectory() {
        return _directory;
    }

    public void setUnarchiveContents(Boolean unarchive) {
        _unarchive = unarchive;
    }

}
