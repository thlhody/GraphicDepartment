package cottontex.graphdep.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;


public class WorkSessionStateOffline {
    // Getters and setters
    @Setter
    @Getter
    private int id;
    @Getter
    @Setter
    private int userId;
    private boolean isWorking;
    private boolean isPaused;
    @Getter
    private LocalDateTime startTimestamp;
    @Setter
    private LocalDateTime pauseTimestamp;
    @Setter
    private LocalDateTime createdAt;
    @Getter
    private String sessionState;

    // Default constructor
    public WorkSessionStateOffline() {}

    // Constructor with fields
    public WorkSessionStateOffline(int id, int userId, boolean isWorking, boolean isPaused, LocalDateTime startTimestamp, LocalDateTime pauseTimestamp, LocalDateTime createdAt, String sessionState) {
        this.id = id;
        this.userId = userId;
        this.isWorking = isWorking;
        this.isPaused = isPaused;
        this.startTimestamp = startTimestamp;
        this.pauseTimestamp = pauseTimestamp;
        this.createdAt = createdAt;
        this.sessionState = sessionState;
    }

    public boolean isWorking() { return isWorking; }
    public void setWorking(boolean working) { isWorking = working; }

    public boolean isPaused() { return isPaused; }
    public void setPaused(boolean paused) { isPaused = paused; }

    public void setStartTimestamp(LocalDateTime startTimestamp) { this.startTimestamp = startTimestamp; }

    public LocalDateTime getPauseTimestamp() { return pauseTimestamp; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setSessionState(String sessionState) { this.sessionState = sessionState; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkSessionStateOffline that = (WorkSessionStateOffline) o;
        return id == that.id &&
                userId == that.userId &&
                isWorking == that.isWorking &&
                isPaused == that.isPaused &&
                Objects.equals(startTimestamp, that.startTimestamp) &&
                Objects.equals(pauseTimestamp, that.pauseTimestamp) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(sessionState, that.sessionState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, isWorking, isPaused, startTimestamp, pauseTimestamp, createdAt, sessionState);
    }
}