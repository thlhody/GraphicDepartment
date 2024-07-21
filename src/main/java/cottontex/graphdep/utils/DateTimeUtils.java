package cottontex.graphdep.utils;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

public final class DateTimeUtils {

    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy");

    private DateTimeUtils() {
        // Private constructor to prevent instantiation
    }

    public static String formatTimeForDisplay(String timestampString) {
        if (timestampString == null || timestampString.isEmpty()) {
            return "N/A";
        }
        try {
            LocalDateTime dateTime = LocalDateTime.parse(timestampString, INPUT_FORMATTER);
            return dateTime.format(OUTPUT_FORMATTER);
        } catch (DateTimeParseException e) {
            LoggerUtility.error("Failed to parse time: " + timestampString);
            return "N/A";
        }
    }

    public static int calculateMinutes(String time) {
        if (time == null || time.equals("N/A") || time.isEmpty()) {
            return 0;
        }
        try {
            String[] parts = time.split(":");
            return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            LoggerUtility.error("Failed to calculate minutes from time: " + time);
            return 0;
        }
    }

    public static String formatTotalTime(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        return String.format("%d:%02d", hours, minutes);
    }

    public static int calculateTotalMinutes(Map<Integer, String> dailyData) {
        return dailyData.values().stream()
                .mapToInt(DateTimeUtils::calculateMinutes)
                .sum();
    }

    public static String getCurrentDateTimeForDisplay() {
        LocalDateTime now = LocalDateTime.now();
        return now.format(DISPLAY_FORMATTER);
    }

    public static Date getSelectedDate(boolean useSystemTime, LocalDate pickerDate) {
        if (useSystemTime) {
            return new Date(System.currentTimeMillis());
        } else {
            return java.sql.Date.valueOf(pickerDate);
        }
    }

    public static Timestamp getSelectedTimestamp(boolean useSystemTime, LocalDate pickerDate, LocalTime pickerTime) {
        if (useSystemTime) {
            return new Timestamp(System.currentTimeMillis());
        } else {
            return Timestamp.valueOf(pickerDate.atTime(pickerTime));
        }
    }
}