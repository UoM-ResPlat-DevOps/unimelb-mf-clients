package unimelb.mf.client.sync.task;

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
import unimelb.mf.client.sync.SyncTask;
import unimelb.mf.client.sync.app.SyncApp;
import unimelb.mf.client.util.FileUtils;
import unimelb.mf.client.util.PathUtils;

public class AssetDownloadTask extends SyncTask {

    public static enum Unarchive {
        AAR, ALL, NONE;

        public static Unarchive fromString(String sv) {
            Unarchive[] vs = values();
            for (Unarchive v : vs) {
                if (v.name().equalsIgnoreCase(sv)) {
                    return v;
                }
            }
            return null;
        }
    }

    private String _assetId;
    private String _assetPath;

    private Path _dstPath;

    private Unarchive _unarchive = Unarchive.NONE;

    public AssetDownloadTask(SyncApp<?> app, String assetPath, Path dstPath) {
        super(app);
        _assetPath = assetPath;
        _dstPath = dstPath.toAbsolutePath();
    }

    protected AssetDownloadTask(SyncApp<?> app, Path dstPath, String assetId) {
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
                        Files.createDirectories(dir);
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
                                            logInfo("Extracting file: '" + f.getAbsolutePath() + "'");
                                            StreamCopy.copy(e.stream(), f);
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
                        FileUtils.createParentDirectories(_dstPath);
                        OutputStream os = new BufferedOutputStream(new FileOutputStream(_dstPath.toFile()));
                        try {
                            logInfo("Downloading file: '" + _dstPath + "'");
                            StreamCopy.copy(is, os);
                        } finally {
                            os.close();
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
