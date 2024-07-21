package cottontex.graphdep.utils;

import javafx.scene.control.DatePicker;
import jfxtras.scene.control.LocalTimePicker;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

public class DateTimeUtils {

    public static Timestamp getTimestampFromPickers(DatePicker datePicker, LocalTimePicker timePicker) {
        LocalDateTime dateTime = LocalDateTime.of(datePicker.getValue(), timePicker.getLocalTime());
        return Timestamp.valueOf(dateTime);
    }

    public static int calculateMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    public static String formatTotalTime(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        return String.format("%d:%02d", hours, minutes);
    }

    public static int calculateTotalMinutes(Map<Integer, String> dailyData) {
        return dailyData.values().stream()
                .mapToInt(s -> {
                    String[] parts = s.split(":");
                    return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
                }).sum();
    }
}