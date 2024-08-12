package cottontex.graphdep.models;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

public class WorkSessionState {
    private boolean isWorking;
    private boolean isPaused;
    @Setter
    @Getter
    private Timestamp startTimestamp;
    @Getter
    @Setter
    private Timestamp pauseTimestamp;

    // Constructor
    public WorkSessionState() {
        reset();
    }

    // Getters and setters
    public boolean isWorking() { return isWorking; }
    public void setWorking(boolean working) { isWorking = working; }
    public boolean isPaused() { return isPaused; }
    public void setPaused(boolean paused) { isPaused = paused; }

    // New reset method
    public void reset() {
        this.isWorking = false;
        this.isPaused = false;
        this.startTimestamp = null;
        this.pauseTimestamp = null;
    }
}