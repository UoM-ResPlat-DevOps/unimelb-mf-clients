package unimelb.mf.client.sync;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import arc.archive.ArchiveInput;
import arc.archive.ArchiveRegistry;
import arc.mf.client.ServerClient;
import arc.mf.client.archive.Archive;
import arc.mime.NamedMimeType;
import arc.streams.LongInputStream;
import arc.streams.StreamCopy;
import arc.xml.XmlDoc;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlStringWriter;
import unimelb.mf.client.session.MFSession;
import unimelb.mf.client.util.FileUtils;
import unimelb.mf.client.util.PathUtils;

public class AssetDownloadTask extends AbstractSyncTask {

    public static enum Unarchive {
        AAR, ALL, NONE
    }

    private String _assetId;
    private String _assetPath;

    private Path _dstPath;

    private Unarchive _unarchive = Unarchive.NONE;
    // TODO remove _overwrite, let check step decide
    private boolean _overwrite = false;

    protected AssetDownloadTask(SyncApplication app, String assetPath, Path dstPath) {
        super(app);
        _assetPath = assetPath;
        _dstPath = dstPath.toAbsolutePath();
    }

    protected AssetDownloadTask(SyncApplication app, Path dstPath, String assetId) {
        super(app);
        _dstPath = dstPath.toAbsolutePath();
        _assetId = assetId;
    }

    public String assetPath() {
        return _assetPath;
    }

    public String assetId() {
        return _assetId;
    }

    @Override
    public void execute(MFSession session) throws Throwable {

        ServerClient.Output output = new ServerClient.OutputConsumer() {

            @Override
            protected void consume(Element re, LongInputStream is) throws Throwable {
                XmlDoc.Element ae = re.element("asset");
                if (_assetPath == null) {
                    _assetPath = ae.value("path");
                } else {
                    _assetId = ae.value("@id");
                }
                if (!ae.elementExists("content")) {
                    logWarning("Asset " + _assetId + ": '" + _assetPath + "' does not have content.");
                    return;
                }
                if (is == null) {
                    logWarning("Asset " + _assetId + ": '" + _assetPath + "' content stream is null.");
                    return;
                }
                try {
                    if (needToUnarchive(ae)) {
                        String ctype = ae.value("content/type");
                        Path dir = Paths.get(PathUtils.removeFileExtension(_dstPath.toString()));
                        FileUtils.createParentDirectories(dir);
                        Archive.declareSupportForAllTypes();
                        ArchiveInput ai = ArchiveRegistry.createInput(is, new NamedMimeType(ctype));
                        try {
                            ArchiveInput.Entry e = null;
                            while ((e = ai.next()) != null) {
                                try {
                                    if (e.isDirectory()) {
                                        Files.createDirectories(PathUtils.getPath(dir.toString(), e.name()));
                                    } else {
                                        try {
                                            File f = PathUtils.getFile(dir.toString(), e.name());
                                            if (_overwrite || !f.exists()) {
                                                logInfo("Extracting file: '" + f.getAbsolutePath() + "'");
                                                StreamCopy.copy(e.stream(), f);
                                            } else {
                                                logWarning(
                                                        "File: '" + f.getAbsolutePath() + "' already exists. Skipped.");
                                            }
                                        } finally {
                                            e.stream().close();
                                        }
                                    }
                                } finally {
                                    ai.closeEntry();
                                }
                            }
                        } finally {
                            ai.close();
                        }
                    } else {
                        if (_overwrite || Files.exists(_dstPath)) {
                            OutputStream os = new BufferedOutputStream(new FileOutputStream(_dstPath.toFile()));
                            try {
                                logInfo("Downloading file: '" + _dstPath + "'");
                                StreamCopy.copy(is, os);
                            } finally {
                                os.close();
                            }
                        } else {
                            logWarning("File: '" + _dstPath + "' already exists. Skipped.");
                        }
                    }
                } finally {
                    is.close();
                }
            }
        };

        XmlStringWriter w = new XmlStringWriter();
        w.add("id", _assetId != null ? _assetId : ("path=" + _assetPath));
        session.execute("asset.get", w.document(), (List<ServerClient.Input>) null, output, this);
    }

    private boolean needToUnarchive(XmlDoc.Element ae) throws Throwable {
        if (_unarchive != Unarchive.NONE) {
            String ctype = ae.value("content/type");
            if (ctype != null && ArchiveRegistry.isAnArchive(ctype)) {
                return _unarchive == Unarchive.ALL || "application/arc-archive".equals(ctype);
            }
        }
        return false;
    }

}
