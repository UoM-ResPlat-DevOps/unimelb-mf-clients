package unimelb.mf.client.sync.check;

import java.nio.file.Path;
import java.util.logging.Logger;

public class CheckResult {

    private String _srcType;
    private String _srcPath;
    private String _dstType;
    private String _dstPath;
    private boolean _dstExists;
    private boolean _namesMatch;
    private boolean _sizesMatch;
    private Boolean _checksumsMatch;

    public CheckResult(Path file, String assetPath, boolean dstExists, boolean namesMatch, boolean sizesMatch,
            Boolean checksumsMatch) {
        this(FileItem.TYPE_NAME, file.toAbsolutePath().toString(), AssetItem.TYPE_NAME, assetPath, dstExists,
                namesMatch, sizesMatch, checksumsMatch);
    }

    public CheckResult(String assetPath, Path file, boolean dstExists, boolean namesMatch, boolean sizesMatch,
            Boolean checksumsMatch) {
        this(AssetItem.TYPE_NAME, assetPath, FileItem.TYPE_NAME, file.toAbsolutePath().toString(), dstExists,
                namesMatch, sizesMatch, checksumsMatch);
    }

    public CheckResult(AssetItem asset, Path file, boolean dstExists, boolean namesMatch, boolean sizesMatch,
            Boolean checksumsMatch) {
        this(asset.typeName(), asset.fullPath(), FileItem.TYPE_NAME, file.toAbsolutePath().toString(), dstExists,
                namesMatch, sizesMatch, checksumsMatch);
    }

    protected CheckResult(String srcType, String srcPath, String dstType, String dstPath, boolean dstExists,
            boolean namesMatch, boolean sizesMatch, Boolean checksumsMatch) {
        _srcType = srcType;
        _srcPath = srcPath;
        _dstType = dstType;
        _dstPath = dstPath;
        _dstExists = dstExists;
        _namesMatch = namesMatch;
        _sizesMatch = sizesMatch;
        _checksumsMatch = checksumsMatch;
    }

    public final String srcType() {
        return _srcType;
    }

    public final String srcPath() {
        return _srcPath;
    }

    public final String dstType() {
        return _dstType;
    }

    public final String dstPath() {
        return _dstPath;
    }

    public final boolean exists() {
        return _dstExists;
    }

    public final boolean sizesMatch() {
        return _sizesMatch;
    }

    public final boolean namesMatch() {
        return _namesMatch;
    }

    public final Boolean checksumsMatch() {
        return _checksumsMatch;
    }

    public final boolean contentsMatch() {
        return _sizesMatch && (_checksumsMatch == null || _checksumsMatch);
    }

    public final boolean match() {
        return _dstExists && _sizesMatch && _namesMatch && (_checksumsMatch == null || _checksumsMatch);
    }

    public final String toCSVRecord() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s:%s,%s:%s,", srcType(), srcPath(), dstType(), dstPath()));
        sb.append(exists()).append(",");
        sb.append(contentsMatch()).append(",");
        return sb.toString();
    }

    @Override
    public final String toString() {
        return toCSVRecord();
    }

    public final void log(Logger logger) {
        logger.info(toCSVRecord());
    }
}
