package unimelb.mf.client.sync.check;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import unimelb.mf.client.util.OSUtils;
import unimelb.mf.client.util.PathUtils;

public class DefaultCheckHandler implements CheckHandler {

    private Logger _csvLogger;

    private AtomicInteger _nbFileChecked = new AtomicInteger(0);
    private AtomicInteger _nbFilePassed = new AtomicInteger(0);
    private AtomicInteger _nbFileFailed = new AtomicInteger(0);
    private AtomicInteger _nbFileMissing = new AtomicInteger(0);
    private AtomicInteger _nbFileContentMismatch = new AtomicInteger(0);

    private AtomicInteger _nbAssetChecked = new AtomicInteger(0);
    private AtomicInteger _nbAssetPassed = new AtomicInteger(0);
    private AtomicInteger _nbAssetFailed = new AtomicInteger(0);
    private AtomicInteger _nbAssetMissing = new AtomicInteger(0);
    private AtomicInteger _nbAssetContentMismatch = new AtomicInteger(0);

    public DefaultCheckHandler(Path outputFile) throws Throwable {
        this(outputFile.getParent(), outputFile.getFileName().toString());
    }

    public DefaultCheckHandler(Path outputDir, String fileName) throws Throwable {
        if (!Files.exists(outputDir)) {
            throw new IllegalArgumentException("Directory: '" + outputDir.toString() + "' does not exist.",
                    new FileNotFoundException(outputDir.toString()));
        }
        if (!Files.isDirectory(outputDir)) {
            throw new IllegalArgumentException("'" + outputDir.toString() + "' is not a directory.");
        }
        if (fileName == null) {
            throw new IllegalArgumentException("Output CSV file name is null.",
                    new NullPointerException("Output CSV file name is null."));
        }
        if (!fileName.toLowerCase().endsWith(".csv")) {
            fileName = fileName + ".csv";
        }
        try {
            Paths.get(PathUtils.toSystemIndependent(outputDir.toString()), fileName).toFile().getCanonicalPath();
        } catch (IOException ioe) {
            throw new IllegalArgumentException("Invalid file name: " + fileName, ioe);
        }
        _csvLogger = createCsvLogger(outputDir, fileName);
        // Header
        _csvLogger.info("Source,Destination,Exist?,Content Match?,");
    }

    public DefaultCheckHandler(String outputCsvPath) throws Throwable {
        this(Paths.get(PathUtils.getParentPath(PathUtils.toSystemIndependent(outputCsvPath))),
                PathUtils.getFileName(PathUtils.toSystemIndependent(outputCsvPath)));
    }

    public void writeSummary() {
        _csvLogger.info(",,,,");
        _csvLogger.info(",,,,");
        int nbCheckedFiles = numberOfCheckedFiles();
        if (nbCheckedFiles > 0) {
            _csvLogger.info(String.format(",,Number of checked files (total):,%d,", nbCheckedFiles));
            _csvLogger.info(String.format(",,Number of files (passed):,%d,", numberOfPassedFiles()));
            _csvLogger.info(String.format(",,Number of files (missing):,%d,", numberOfMissingFiles()));
            _csvLogger.info(String.format(",,Number of files (content mismatch):,%d,", numberOfMissingFiles()));
            _csvLogger.info(",,,,");
        }
        int nbCheckedAssets = numberOfCheckedAssets();
        if (nbCheckedAssets > 0) {
            _csvLogger.info(String.format(",,Number of checked assets (total):,%d,", nbCheckedAssets));
            _csvLogger.info(String.format(",,Number of assets (passed):,%d,", numberOfPassedAssets()));
            _csvLogger.info(String.format(",,Number of assets (missing):,%d,", numberOfMissingAssets()));
            _csvLogger.info(String.format(",,Number of assets (content mismatch):,%d,", numberOfMissingAssets()));
            _csvLogger.info(",,,,");
        }
    }

    public void printSummary() {
        System.out.println();
        int nbCheckedFiles = numberOfCheckedFiles();
        if (nbCheckedFiles > 0) {
            System.out.println(String.format("    %,13d files [checked/total]", nbCheckedFiles));
            System.out.println(String.format("    %,13d files [passed]", numberOfPassedFiles()));
            System.out.println(String.format("    %,13d files [missing]", numberOfMissingFiles()));
            System.out.println(String.format("    %,13d files [content mismatch]", numberOfContentMismatchFiles()));
            System.out.println();
        }
        int nbCheckedAssets = numberOfCheckedAssets();
        if (nbCheckedAssets > 0) {
            System.out.println(String.format("    %,13d assets [checked/total]", nbCheckedAssets));
            System.out.println(String.format("    %,13d assets [passed]", numberOfPassedAssets()));
            System.out.println(String.format("    %,13d assets [missing]", numberOfMissingAssets()));
            System.out.println(String.format("    %,13d assets [content mismatch]", numberOfContentMismatchAssets()));
            System.out.println();
        }
    }

    @Override
    public void checked(CheckResult result) {
        if (FileItem.TYPE_NAME.equals(result.srcType())) {
            _nbFileChecked.getAndIncrement();
            if (result.match()) {
                _nbFilePassed.getAndIncrement();
            } else {
                _nbFileFailed.getAndIncrement();
                if (!result.exists()) {
                    _nbFileMissing.getAndIncrement();
                } else {
                    _nbFileContentMismatch.getAndIncrement();
                }
                result.log(_csvLogger);
            }
        } else {
            _nbAssetChecked.getAndIncrement();
            if (result.match()) {
                _nbAssetPassed.getAndIncrement();
            } else {
                _nbAssetFailed.getAndIncrement();
                if (!result.exists()) {
                    _nbAssetMissing.getAndIncrement();
                } else {
                    _nbAssetContentMismatch.getAndIncrement();
                }
                result.log(_csvLogger);
            }
        }
    }

    public int numberOfCheckedFiles() {
        return _nbFileChecked.get();
    }

    public int numberOfPassedFiles() {
        return _nbFilePassed.get();
    }

    public int numberOfFailedFiles() {
        return _nbFileFailed.get();
    }

    public int numberOfMissingFiles() {
        return _nbFileMissing.get();
    }

    public int numberOfContentMismatchFiles() {
        return _nbFileContentMismatch.get();
    }

    public int numberOfCheckedAssets() {
        return _nbAssetChecked.get();
    }

    public int numberOfPassedAssets() {
        return _nbAssetPassed.get();
    }

    public int numberOfFailedAssets() {
        return _nbAssetFailed.get();
    }

    public int numberOfMissingAssets() {
        return _nbAssetMissing.get();
    }

    public int numberOfContentMismatchAssets() {
        return _nbAssetContentMismatch.get();
    }

    public void close() throws IOException {
        if (_csvLogger != null) {
            Handler[] handlers = _csvLogger.getHandlers();
            if (handlers != null) {
                for (Handler handler : handlers) {
                    handler.close();
                }
            }
        }
    }

    public static Logger createCsvLogger(Path dir, String fileName) throws Throwable {
        Logger logger = Logger.getAnonymousLogger();
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.INFO);
        logger.addHandler(createCsvFileHandler(dir, fileName));
        return logger;
    }

    public static FileHandler createCsvFileHandler(Path dir, String fileName) throws Throwable {
        FileHandler fileHandler = new FileHandler(Paths.get(dir.toString(), fileName).toString(), Integer.MAX_VALUE, 1,
                true);
        fileHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                StringBuilder sb = new StringBuilder();
                if (record.getMessage() != null) {
                    sb.append(record.getMessage());
                    if (OSUtils.IS_WINDOWS) {
                        sb.append("\r");
                    }
                    sb.append("\n");
                }
                return sb.toString();
            }
        });

        fileHandler.setLevel(Level.INFO);
        return fileHandler;
    }

}
