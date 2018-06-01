package unimelb.mf.client.sync.task;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import arc.mf.client.ServerClient;
import arc.streams.StreamCopy.AbortCheck;
import arc.xml.XmlDoc;
import arc.xml.XmlStringWriter;
import unimelb.mf.client.file.PosixAttributes;
import unimelb.mf.client.session.MFSession;
import unimelb.mf.client.task.AbstractMFTask;
import unimelb.mf.client.util.PathUtils;

public class FileUploadTask extends AbstractMFTask {

    private Path _file;
    private String _assetPath;
    private boolean _csumCheck;
    private int _retry;
    long _bytesUploaded = 0;
    private DataTransferListener<Path, String> _ul;

    private String _assetId;
    private long _crc32;

    public FileUploadTask(MFSession session, Logger logger, Path file, String assetPath, boolean csumCheck, int retry,
            DataTransferListener<Path, String> ul) {
        super(session, logger);
        _file = file;
        _assetPath = assetPath;
        _csumCheck = csumCheck;
        _retry = retry;
        _bytesUploaded = 0;
        _ul = ul;
    }

    @Override
    public void execute() throws Throwable {
        try {
            if (_ul != null) {
                _ul.transferStarted(_file, _assetPath);
            }
            PosixAttributes fileAttrs = null;
            long fileSize = Files.size(_file);

            XmlStringWriter w2 = new XmlStringWriter();
            w2.push("service", new String[] { "name", "asset.set" });
            w2.add("id", "path=" + _assetPath);
            w2.add("create", true);
            w2.push("meta", new String[] { "action", "replace" });
            if (fileAttrs == null) {
                fileAttrs = PosixAttributes.read(_file);
            }
            fileAttrs.save(w2);
            w2.pop();
            w2.pop();

            if (_csumCheck) {
                w2.push("service", new String[] { "name", "asset.get" });
                w2.add("id", "path=" + _assetPath);
                w2.pop();
            }

            String fileExt = PathUtils.getFileExtension(_file.toString());
            ServerClient.Input input = new ServerClient.GeneratedInput(null, fileExt, _file.toString(), fileSize) {
                @Override
                protected void copyTo(OutputStream out, AbortCheck ac) throws Throwable {
                    try {
                        InputStream in = new BufferedInputStream(new FileInputStream(_file.toFile()));
                        if (_csumCheck) {
                            in = new CheckedInputStream(in, new CRC32());
                        }
                        byte[] buffer = new byte[8192];
                        int len;
                        try {
                            while ((len = in.read(buffer)) != -1) {
                                out.write(buffer, 0, len);
                                _bytesUploaded += len;
                                if (_ul != null) {
                                    _ul.transferProgressed(_file, _assetPath, len);
                                }
                                if ((ac != null && ac.hasBeenAborted()) || Thread.interrupted()) {
                                    throw new InterruptedException("Upload aborted.");
                                }
                            }
                        } finally {
                            in.close();
                        }
                        if (_csumCheck) {
                            _crc32 = ((CheckedInputStream) in).getChecksum().getValue();
                        }
                    } finally {
                        out.close();
                    }
                }
            };
            setCurrentOperation("Uploading file: '" + _file + "' to asset: '" + _assetPath + "'");
            logInfo("Uploading file: '" + _file + "' to asset: '" + _assetPath + "'");
            XmlDoc.Element re = session().execute("service.execute", w2.document(), input, null, this);
            if (_csumCheck) {
                XmlDoc.Element ae = re.element("reply[@service='asset.get']/response/asset");
                _assetId = re.value("id");
                long assetCRC32 = ae.longValue("content/csum[@base='16']", 0L, 16);
                if (_crc32 != assetCRC32) {
                    logWarning("CRC32 checksums do not match for file: '" + _file + "'(" + _crc32 + ") and asset: '"
                            + _assetPath + "'(" + assetCRC32 + ")");
                    _crc32 = 0;
                    if (_retry > 0) {
                        _retry--;
                        rewindProgress();
                        execute();
                        return;
                    } else {
                        throw new Exception("CRC32 checksums do not match for file: '" + _file + "'(" + _crc32
                                + ") and asset: '" + _assetPath + "'(" + assetCRC32 + ")");
                    }
                }
            } else {
                if (re.elementExists("reply[@service='asset.set']/response/id")) {
                    _assetId = re.value("reply[@service='asset.set']/response/id");
                } else if (re.elementExists("reply[@service='asset.set']/response/version")) {
                    _assetId = re.value("reply[@service='asset.set']/response/version/@id");
                }
            }
            if (_ul != null) {
                _ul.transferCompleted(_file, _assetPath);
            }
            logInfo("Uploaded file: '" + _file + "' to asset(id=" + _assetId + "): '" + _assetPath + "'");
        } catch (Throwable e) {
            if (_ul != null) {
                _ul.transferFailed(_file, _assetPath);
            }
            rewindProgress();
            throw e;
        }
    }

    private void rewindProgress() {
        setCompletedOperations(0);
        if (_bytesUploaded > 0) {
            if (_ul != null) {
                _ul.transferProgressed(_file, _assetPath, -1 * _bytesUploaded);
            }
            _bytesUploaded = 0;
        }
    }

}
