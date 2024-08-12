package cottontex.graphdep.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtils {

    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy");

    private DateTimeUtils() {
        // Private constructor to prevent instantiation
    }

    public static Integer calculateMinutes(String timeString) {
        if (timeString == null || timeString.isEmpty() || timeString.equals("00:00")) {
            return 0;
        }
        if (timeString.equals("CM") || timeString.equals("SN") || timeString.equals("CO")) {
            return 0;  // Or you might want to return a special value for these cases
        }
        String[] parts = timeString.split(":");
        if (parts.length != 2) {
            LoggerUtility.error("Invalid time format: " + timeString);
        }
        Integer hours = Integer.parseInt(parts[0]);
        Integer minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }

    public static String formatTotalTime(Integer totalMinutes) {
        Integer hours = totalMinutes / 60;
        Integer minutes = totalMinutes % 60;
        return String.format("%d:%02d", hours, minutes);
    }

    public static String getCurrentDateTimeForDisplay() {
        LocalDateTime now = LocalDateTime.now();
        return now.format(DISPLAY_FORMATTER);
    }

    public static int roundDownHours(String timeString) {
        if (timeString == null || timeString.isEmpty() || timeString.equals("00:00")) {
            return 0;
        }

        String[] parts = timeString.split(":");
        if (parts.length != 2) {
            LoggerUtility.error("Invalid time format: " + timeString);
        }
        // Simple round down to nearest hour
        return Integer.parseInt(parts[0]);
    }
}