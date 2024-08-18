package cottontex.graphdep.models;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class WorkSessionState {
    private boolean isWorking;
    private boolean isPaused;

    public enum SessionState {
        STARTED, PAUSED, ENDED
    }

    @Setter
    @Getter
    private SessionState sessionState;

    @Setter
    @Getter
    private Timestamp startTimestamp;
    @Getter
    @Setter
    private Timestamp pauseTimestamp;
    @Getter
    @Setter
    private LocalDateTime lastUpdateTime;

    // Constructor
    public WorkSessionState() {
        reset();
        this.sessionState = SessionState.ENDED;
    }

    // Getters and setters
    public boolean isWorking() {
        return isWorking;
    }

    public void setWorking(boolean working) {
        isWorking = working;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    // New reset method

    public void reset() {
        this.isWorking = false;
        this.isPaused = false;
        this.startTimestamp = null;
        this.pauseTimestamp = null;
        this.lastUpdateTime = null;
        this.sessionState = SessionState.ENDED;
    }

    public void startWorking() {
        this.isWorking = true;
        this.isPaused = false;
        this.startTimestamp = Timestamp.valueOf(LocalDateTime.now());
        this.lastUpdateTime = this.startTimestamp.toLocalDateTime();
        this.sessionState = SessionState.STARTED;
    }

    public void pauseWork() {
        this.isPaused = true;
        this.pauseTimestamp = Timestamp.valueOf(LocalDateTime.now());
        this.lastUpdateTime = this.pauseTimestamp.toLocalDateTime();
        this.sessionState = SessionState.PAUSED;
    }

    public void resumeWork() {
        this.isPaused = false;
        this.pauseTimestamp = null;
        this.lastUpdateTime = LocalDateTime.now();
        this.sessionState = SessionState.STARTED;
    }

    public void endWork() {
        reset();
        this.sessionState = SessionState.ENDED;
    }

    public LocalDateTime getWorkStartTime() {
        return startTimestamp != null ? startTimestamp.toLocalDateTime() : null;
    }


    public void setSessionState(SessionState sessionState) {
        this.sessionState = sessionState;
        switch (sessionState) {
            case STARTED:
                this.isWorking = true;
                this.isPaused = false;
                break;
            case PAUSED:
                this.isWorking = true;
                this.isPaused = true;
                break;
            case ENDED:
                this.isWorking = false;
                this.isPaused = false;
                break;
        }
    }

}