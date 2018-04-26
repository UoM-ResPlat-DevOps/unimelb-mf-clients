package unimelb.mf.client.sync.app;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import unimelb.mf.client.session.MFSession;
import unimelb.mf.client.sync.settings.Direction;
import unimelb.mf.client.sync.settings.Job;
import unimelb.mf.client.sync.settings.MFCheckSettings;

public class MFCheckApp extends AbstractSyncApp<MFCheckSettings> {

    public static final String APP_NAME = "unimelb-mf-check";

    protected MFCheckApp() {
        super(new MFCheckSettings(null));
    }

    public final String applicationName() {
        return APP_NAME;
    }

    public final String description() {
        return "A command line tool to compare files in local file system against the ones on remote Mediaflux server, or, vice versa.";
    }

    @Override
    public void execute(MFSession session, MFCheckSettings settings) throws Throwable {
        Logger uploadCheckOutputCSVLogger = null;
        Logger downloadCheckOutputCSVLogger = null;

        if (settings.direction() == Direction.DOWNLOAD || settings.direction() == Direction.BOTH) {
            String downloadCsvFileName = String.format("%s-download_check-%s.csv", settings.outputFileNamePrefix(),
                    new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
            Path downloadCheckOutputCSV = Paths.get(settings.outputDirectory().toString(), downloadCsvFileName);
            downloadCheckOutputCSVLogger = createOutputLogger(downloadCheckOutputCSV);
        }
        if (settings.direction() == Direction.UPLOAD || settings.direction() == Direction.BOTH) {
            String uploadCsvFileName = String.format("%s-upload-%s.csv", settings.outputFileNamePrefix(),
                    new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
            Path uploadCheckOutputCSV = Paths.get(settings.outputDirectory().toString(), uploadCsvFileName);
            uploadCheckOutputCSVLogger = createOutputLogger(uploadCheckOutputCSV);
        }

        List<Job> jobs = settings.jobs();
        if (jobs != null && !jobs.isEmpty()) {
            for (Job job : jobs) {
                if (settings.direction() == Direction.DOWNLOAD || settings.direction() == Direction.BOTH) {

                }
                if (settings.direction() == Direction.UPLOAD || settings.direction() == Direction.BOTH) {

                }
            }
        }
    }

    private void checkUpload(Job job, MFCheckSettings settings) {

    }

    private void checkDownload(Job job, MFCheckSettings settings) {

    }

    private static Logger createOutputLogger(Path outputFile) throws Throwable {
        Logger logger = Logger.getLogger(MFCheckApp.class.getName());
        FileHandler fh = new FileHandler(outputFile.toString(), 1000000000, 1, true);
        fh.setFormatter(new Formatter() {

            @Override
            public String format(LogRecord record) {
                return record.getMessage();
            }
        });
        logger.setLevel(Level.ALL);
        logger.addHandler(fh);
        return logger;
    }
}
