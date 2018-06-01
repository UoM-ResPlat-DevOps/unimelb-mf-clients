package unimelb.mf.client.sync.check;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import arc.xml.XmlDoc;
import unimelb.mf.client.util.PathUtils;

public class AssetItem implements Item {

    public static final String TYPE_NAME = "asset";

    private Map<ChecksumType, String> _checksums;
    private long _length;

    private String _assetId;
    private String _assetPath;
    private String _assetNamespace;

    private String _baseANS;

    public AssetItem(String assetPath, String baseAssetNamespace, long length, String checksum,
            ChecksumType checksumType) {
        if (checksum != null && checksumType != null) {
            _checksums = new LinkedHashMap<ChecksumType, String>();
            _checksums.put(checksumType, checksum);
        }
        _length = length;
        _assetId = null;
        _assetPath = assetPath;
        _assetNamespace = PathUtils.getParentPath(_assetPath);
        _baseANS = baseAssetNamespace;
    }

    public AssetItem(XmlDoc.Element ae, String baseAssetNamespace) throws Throwable {
        _checksums = new LinkedHashMap<ChecksumType, String>();
        _assetId = ae.value("@id");
        _assetPath = ae.value("path");
        _assetNamespace = ae.value("namespace");
        if (!ae.elementExists("content")) {
            throw new Exception("No content is found on asset " + _assetId);
        }
        _length = ae.longValue("content/size");
        String crc32 = ae.value("content/csum[@base='16']");
        if (crc32 != null) {
            _checksums.put(ChecksumType.CRC32, crc32);
        }
        _baseANS = baseAssetNamespace;
    }

    protected void setBaseNamespace(String baseAssetNamespace) {
        _baseANS = baseAssetNamespace;
    }

    @Override
    public final long length() {
        return _length;
    }

    @Override
    public final Map<ChecksumType, String> checksums() {
        if (_checksums != null) {
            return Collections.unmodifiableMap(_checksums);
        }
        return null;
    }

    public final String assetNamespace() {
        return _assetNamespace;
    }

    public final String assetId() {
        return _assetId;
    }

    public final String assetPath() {
        return _assetPath;
    }

    @Override
    public final String fullPath() {
        return assetPath();
    }

    @Override
    public final String basePath() {
        return _baseANS;
    }

    public final String baseNamespace() {
        return _baseANS;
    }

    @Override
    public final String typeName() {
        return TYPE_NAME;
    }

}
