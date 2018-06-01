package unimelb.mf.client.sync.task;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

import arc.archive.ArchiveInput;
import arc.archive.ArchiveRegistry;
import arc.mf.client.ServerClient;
import arc.mf.client.archive.Archive;
import arc.mime.NamedMimeType;
import arc.streams.LongInputStream;
import arc.streams.SizedInputStream;
import arc.streams.StreamCopy;
import arc.xml.XmlDoc;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlStringWriter;
import unimelb.io.ProgressMonitor;
import unimelb.io.ProgressMonitorInputStream;
import unimelb.mf.client.session.MFSession;
import unimelb.mf.client.task.AbstractMFTask;
import unimelb.mf.client.util.FileUtils;
import unimelb.mf.client.util.PathUtils;

public class AssetDownloadTask extends AbstractMFTask {

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

    private DataTransferListener<String, Path> _dl;

    public AssetDownloadTask(MFSession session, Logger logger, String assetPath, Path dstPath,
            DataTransferListener<String, Path> dl) {
        super(session, logger);
        _assetPath = assetPath;
        _dstPath = dstPath.toAbsolutePath();
        _dl = dl;
    }

    protected AssetDownloadTask(MFSession session, Logger logger, Path dstPath, String assetId,
            DataTransferListener<String, Path> dl) {
        super(session, logger);
        _dstPath = dstPath.toAbsolutePath();
        _assetId = assetId;
        _dl = dl;
    }

    public String assetPath() {
        return _assetPath;
    }

    public String assetId() {
        return _assetId;
    }

    @Override
    public void execute() throws Throwable {

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
                boolean unarchive = needToUnarchive(ae);
                Path dstDir = unarchive ? Paths.get(PathUtils.removeFileExtension(_dstPath.toString())) : null;
                ProgressMonitorInputStream pis = new ProgressMonitorInputStream(is, new ProgressMonitor() {
                    @Override
                    public void progressed(long bytesRead) {
                        if (_dl != null) {
                            _dl.transferProgressed(_assetPath, unarchive ? dstDir : _dstPath, bytesRead);
                        }
                    }
                });
                SizedInputStream sis = new SizedInputStream(pis, is.length());
                try {
                    if (unarchive) {
                        String ctype = ae.value("content/type");

                        if (_dl != null) {
                            _dl.transferStarted(_assetPath, dstDir);
                        }
                        Files.createDirectories(dstDir);
                        Archive.declareSupportForAllTypes();
                        ArchiveInput ai = ArchiveRegistry.createInput(sis, new NamedMimeType(ctype));
                        try {
                            ArchiveInput.Entry e = null;
                            while ((e = ai.next()) != null) {
                                try {
                                    if (e.isDirectory()) {
                                        Files.createDirectories(PathUtils.getPath(dstDir.toString(), e.name()));
                                    } else {
                                        try {
                                            File f = PathUtils.getFile(dstDir.toString(), e.name());
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
                        if (_dl != null) {
                            _dl.transferStarted(_assetPath, _dstPath);
                        }
                        OutputStream os = new BufferedOutputStream(new FileOutputStream(_dstPath.toFile()));
                        try {
                            logInfo("Downloading file: '" + _dstPath + "'");
                            StreamCopy.copy(sis, os);
                        } finally {
                            os.close();
                        }
                    }
                    if (_dl != null) {
                        _dl.transferCompleted(_assetPath, unarchive ? dstDir : _dstPath);
                    }
                } catch (Throwable t) {
                    if (_dl != null) {
                        _dl.transferFailed(_assetPath, unarchive ? dstDir : _dstPath);
                    }
                    throw t;
                } finally {
                    try {
                        sis.close();
                    } finally {
                        pis.close();
                    }
                }
            }
        };

        XmlStringWriter w = new XmlStringWriter();
        w.add("id", _assetId != null ? _assetId : ("path=" + _assetPath));

        session().execute("asset.get", w.document(), (List<ServerClient.Input>) null, output, this);
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
