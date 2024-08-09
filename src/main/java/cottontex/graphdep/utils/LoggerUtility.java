package cottontex.graphdep.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtility {
    private static final Logger logger = LoggerFactory.getLogger(LoggerUtility.class);

    public static void info(String message) {
        logger.info(message);
        System.out.println(message);
    }

    public static void warn(String message) {
        logger.warn(message);
        System.out.println(message);
    }

    public static void error(String message) {
        logger.error(message);
        System.out.println(message);
    }

    public static void error(String message, Throwable throwable) {
        logger.error(message, throwable);
        System.out.println(message);
    }

    public static void debug(String message) {
        logger.debug(message);
        System.out.println(message);
    }

    public static void trace(String message) {
        logger.trace(message);
        System.out.println(message);
    }

    public static void logException(String message, Exception e) {
        logger.error(message, e);
        System.out.println(message);
    }

    public static void logAndThrow(String message, Exception e) throws RuntimeException {
        logger.error(message, e);
        System.out.println(message);
        throw new RuntimeException(message, e);
    }
}