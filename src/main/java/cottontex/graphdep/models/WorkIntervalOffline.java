package cottontex.graphdep.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Setter
@Getter
public class WorkIntervalOffline {
    // Getters and setters
    private int id;
    private int userId;
    private LocalDateTime firstStartTime;
    private int breaks;
    private LocalTime breaksTime;
    private LocalDateTime endTime;
    private LocalTime totalWorkedTime;
    private String timeOffType;
    private LocalDate workDate;
    private int totalWorkedSeconds;

    // Default constructor
    public WorkIntervalOffline() {}

    // Constructor with fields
    public WorkIntervalOffline(int id, int userId, LocalDateTime firstStartTime, int breaks, LocalTime breaksTime, LocalDateTime endTime, LocalTime totalWorkedTime, String timeOffType, LocalDate workDate, int totalWorkedSeconds) {
        this.id = id;
        this.userId = userId;
        this.firstStartTime = firstStartTime;
        this.breaks = breaks;
        this.breaksTime = breaksTime;
        this.endTime = endTime;
        this.totalWorkedTime = totalWorkedTime;
        this.timeOffType = timeOffType;
        this.workDate = workDate;
        this.totalWorkedSeconds = totalWorkedSeconds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkIntervalOffline that = (WorkIntervalOffline) o;
        return id == that.id &&
                userId == that.userId &&
                breaks == that.breaks &&
                totalWorkedSeconds == that.totalWorkedSeconds &&
                Objects.equals(firstStartTime, that.firstStartTime) &&
                Objects.equals(breaksTime, that.breaksTime) &&
                Objects.equals(endTime, that.endTime) &&
                Objects.equals(totalWorkedTime, that.totalWorkedTime) &&
                Objects.equals(timeOffType, that.timeOffType) &&
                Objects.equals(workDate, that.workDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, firstStartTime, breaks, breaksTime, endTime, totalWorkedTime, timeOffType, workDate, totalWorkedSeconds);
    }
}