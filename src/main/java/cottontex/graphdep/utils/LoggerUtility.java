package cottontex.graphdep.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerUtility {
    private static final Logger logger = LogManager.getLogger(LoggerUtility.class);

    public static void info(String message) {
        logger.info(message);
    }

    public static void warn(String message) {
        logger.warn(message);
    }

    public static void error(String message) {
        logger.error(message);
    }

    public static void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    public static void debug(String message) {
        logger.debug(message);
    }

    public static void trace(String message) {
        logger.trace(message);
    }

    public static void logException(String message, Exception e) {
        logger.error(message, e);
    }

    public static void logAndThrow(String message, Exception e) throws RuntimeException {
        logger.error(message, e);
        throw new RuntimeException(message, e);
    }
}