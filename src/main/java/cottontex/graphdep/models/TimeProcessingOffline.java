package cottontex.graphdep.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Setter
@Getter
public class TimeProcessingOffline {
    // Getters and setters
    private int id;
    private int userId;
    private LocalDateTime timeA;
    private LocalDateTime timeB;
    private double duration;
    private String sessionState;

    // Default constructor
    public TimeProcessingOffline() {}

    // Constructor with fields
    public TimeProcessingOffline(int id, int userId, LocalDateTime timeA, LocalDateTime timeB, double duration, String sessionState) {
        this.id = id;
        this.userId = userId;
        this.timeA = timeA;
        this.timeB = timeB;
        this.duration = duration;
        this.sessionState = sessionState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeProcessingOffline that = (TimeProcessingOffline) o;
        return id == that.id &&
                userId == that.userId &&
                Double.compare(that.duration, duration) == 0 &&
                Objects.equals(timeA, that.timeA) &&
                Objects.equals(timeB, that.timeB) &&
                Objects.equals(sessionState, that.sessionState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, timeA, timeB, duration, sessionState);
    }
}