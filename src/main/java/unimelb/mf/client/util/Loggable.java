package unimelb.mf.client.util;

import java.util.logging.Level;

public interface Loggable extends HasLogger {

    default void log(Level level, String msg, Throwable thrown) {
        logger().log(level, msg, thrown);
    }

    default void logInfo(String msg) {
        logger().info(msg);
    }

    default void logWarning(String msg) {
        logger().warning(msg);
    }

    default void logError(String msg, Throwable e) {
        logger().log(Level.SEVERE, msg == null ? (e == null ? null : e.getMessage()) : msg, e);
    }

    default void logError(Throwable e) {
        logger().log(Level.SEVERE, e.getMessage(), e);
    }

    default void logError(String msg) {
        logger().severe(msg);
    }
}
