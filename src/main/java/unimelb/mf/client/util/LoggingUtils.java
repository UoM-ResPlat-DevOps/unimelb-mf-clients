package unimelb.mf.client.util;

import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

public class LoggingUtils {

    public static final String LOG_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final int LOG_FILE_SIZE_LIMIT = 100000000; // 100 MB

    public static final int LOG_FILE_COUNT = 2;

    public static FileHandler createFileHandler(Path dir, String name, int logFileSizeLimit, int logFileCount,
            Level level, Formatter formatter) throws Throwable {
        String logFileNamePattern = dir.toString() + File.separatorChar + name + "." + "%g.log";
        FileHandler fileHandler = new FileHandler(logFileNamePattern, logFileSizeLimit, logFileCount, true);
        if (formatter != null) {
            fileHandler.setFormatter(formatter);
        } else {
            fileHandler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    StringBuilder sb = new StringBuilder();
                    // date & time
                    sb.append(new SimpleDateFormat(LOG_DATE_FORMAT).format(new Date(record.getMillis()))).append(" ");
                    // thread
                    sb.append("[thread ").append(record.getThreadID()).append("] ");
                    sb.append(String.format("%7s", record.getLevel().getName().toUpperCase())).append(" ");
                    if (record.getThrown() != null) {
                        sb.append(record.getThrown().getClass().getName()).append(" ");
                    }
                    if (record.getMessage() != null) {
                        sb.append(record.getMessage());
                    }
                    if (OSUtils.IS_WINDOWS) {
                        sb.append("\r");
                    }
                    sb.append("\n");
                    Throwable error = record.getThrown();
                    if (error != null) {
                        sb.append(ThrowableUtils.getStackTrace(error));
                        if (OSUtils.IS_WINDOWS) {
                            sb.append("\r");
                        }
                        sb.append("\n");
                    }
                    return sb.toString();
                }
            });
        }
        fileHandler.setLevel(level);
        return fileHandler;
    }

    public static FileHandler createFileHandler(Path dir, String name, int logFileSizeLimit, int logFileCount,
            Level level) throws Throwable {
        return createFileHandler(dir, name, logFileSizeLimit, logFileCount, level, null);
    }

    public static FileHandler createFileHandler(Path dir, String name) throws Throwable {
        return createFileHandler(dir, name, LOG_FILE_SIZE_LIMIT, LOG_FILE_COUNT, Level.ALL, null);
    }

    public static Collection<Handler> createConsoleHandlers() {

        StreamHandler outHandler = new StreamHandler(System.out, new Formatter() {

            @Override
            public String format(LogRecord record) {
                Level level = record.getLevel();
                StringBuilder sb = new StringBuilder();
                if (level.intValue() >= Level.WARNING.intValue()) {
                    sb.append("Warning: ");
                }
                sb.append(record.getMessage());
                if (OSUtils.IS_WINDOWS) {
                    sb.append('\r');
                }
                sb.append('\n');
                return sb.toString();
            }
        }) {
            @Override
            public void publish(LogRecord record) {
                Level level = record.getLevel();
                if (level.intValue() < Level.SEVERE.intValue()) {
                    super.publish(record);
                    flush();
                }
            }
        };
        outHandler.setLevel(Level.ALL);
        StreamHandler errHandler = new StreamHandler(System.err, new Formatter() {

            @Override
            public String format(LogRecord record) {
                StringBuilder sb = new StringBuilder();
                sb.append("Error: ");
                if (record.getThrown() != null) {
                    sb.append(record.getThrown().getClass().getName()).append(": ");
                }
                if (record.getMessage() != null) {
                    sb.append(record.getMessage());
                }
                if (OSUtils.IS_WINDOWS) {
                    sb.append('\r');
                }
                sb.append('\n');
                return sb.toString();
            }
        }) {
            @Override
            public void publish(LogRecord record) {
                Level level = record.getLevel();
                if (level.intValue() >= Level.SEVERE.intValue() && level.intValue() < Level.OFF.intValue()) {
                    super.publish(record);
                    flush();
                }
            }
        };
        errHandler.setLevel(Level.SEVERE);
        List<Handler> handlers = new ArrayList<Handler>(2);
        handlers.add(outHandler);
        handlers.add(errHandler);
        return handlers;
    }

    public static void addHandlers(Logger logger, Handler... handlers) {
        if (handlers != null) {
            for (Handler handler : handlers) {
                logger.addHandler(handler);
            }
        }
    }

    public static void addHandlers(Logger logger, Collection<Handler> handlers) {
        if (handlers != null) {
            for (Handler handler : handlers) {
                logger.addHandler(handler);
            }
        }
    }

    public static void addFileHandler(Logger logger, Path dir, String name) throws Throwable {
        addHandlers(logger, createFileHandler(dir, name));
    }

    public static void addConsoleHandlers(Logger logger) {
        addHandlers(logger, createConsoleHandlers());
    }

    public static Logger createConsoleLogger() {
        return createConsoleLogger(null);
    }

    public static Logger createConsoleLogger(String name) {
        Logger logger = name == null ? Logger.getAnonymousLogger() : Logger.getLogger(name);
        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);
        addConsoleHandlers(logger);
        return logger;
    }

    public static Logger createFileLogger(Path dir, String name) throws Throwable {
        Logger logger = Logger.getLogger(name);
        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);
        addFileHandler(logger, dir, name);
        return logger;
    }

    public static Logger createFileAndConsoleLogger(Path dir, String name) throws Throwable {
        Logger logger = Logger.getLogger(name);
        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);
        addFileHandler(logger, dir, name);
        addConsoleHandlers(logger);
        return logger;
    }

}
