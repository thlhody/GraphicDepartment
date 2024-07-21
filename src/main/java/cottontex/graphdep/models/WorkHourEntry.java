package cottontex.graphdep.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class WorkHourEntry {
    private LocalDate date;
    private LocalDateTime startTime;
    private Integer breaks;
    private LocalTime breaksTime;
    private LocalDateTime endTime;
    private LocalTime totalWorkedTime;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Constructor
    public WorkHourEntry(LocalDate date, LocalDateTime startTime, Integer breaks, LocalTime breaksTime, LocalDateTime endTime, LocalTime totalWorkedTime) {
        this.date = date;
        this.startTime = startTime;
        this.breaks = breaks;
        this.breaksTime = breaksTime;
        this.endTime = endTime;
        this.totalWorkedTime = totalWorkedTime;
    }

    public String getDate() {
        return date != null ? date.format(DATE_FORMATTER) : "N/A";
    }

    public String getStartTime() {
        return startTime != null ? startTime.toLocalTime().format(TIME_FORMATTER) : "N/A";
    }

    public int getBreaks() { return breaks; }

    public String getBreaksTime() {
        return breaksTime != null ? breaksTime.format(TIME_FORMATTER) : "N/A";
    }

    public String getEndTime() {
        return endTime != null ? endTime.toLocalTime().format(TIME_FORMATTER) : "N/A";
    }

    public String getTotalWorkedTime() {
        return totalWorkedTime != null ? totalWorkedTime.format(TIME_FORMATTER) : "N/A";
    }
}