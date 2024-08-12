package cottontex.graphdep.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtility {
    private static final Logger logger = LoggerFactory.getLogger(LoggerUtility.class);

    public static void initialize(Class<?> clazz, String additionalInfo) {
        String message = "Initializing " + clazz.getSimpleName();
        if (additionalInfo != null && !additionalInfo.isEmpty()) {
            message += ": " + additionalInfo;
        }
        logger.info(message);
    }

    public static void buttonInfo(String buttonName, String username) {
        String message = "Button clicked: " + buttonName + " by user: " + username;
        logger.info(message);
    }

    public static void switchController(Class<?> fromController, Class<?> toController, String username) {
        String message = "Switching from " + fromController.getSimpleName() + " to " + toController.getSimpleName() + " for user: " + username;
        logger.info(message);
    }

    public static void actionInfo(String action, String details, String username) {
        String message = "Action performed: " + action + " - " + details + " by user: " + username;
        logger.info(message);
    }

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