package cottontex.graphdep.services.user;

import cottontex.graphdep.database.interfaces.user.IScheduleUserTable;
import cottontex.graphdep.models.UserSession;
import cottontex.graphdep.models.WorkSessionState;
import cottontex.graphdep.utils.DateTimeUtils;
import cottontex.graphdep.utils.LoggerUtility;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;

public class UserService {
    private final IScheduleUserTable scheduleUserTable;
    private LocalDateTime workStartTime;

    public UserService(IScheduleUserTable scheduleUserTable) {
        this.scheduleUserTable = scheduleUserTable;
    }

    public String getDisplayTimeInfo(WorkSessionState state) {
        LocalDateTime now = LocalDateTime.now();
        String currentTime = DateTimeUtils.formatTimeNoSeconds(now);

        if (state.isWorking()) {
            if (workStartTime == null) {
                workStartTime = state.getWorkStartTime();
                if (workStartTime == null) {
                    return "Error: Work start time not set";
                }
            }
            Duration workDuration = Duration.between(workStartTime, now);
            String formattedDuration = formatDuration(workDuration);

            if (state.isPaused()) {
                return String.format("Work paused at %s (Total work: %s)",
                        currentTime, formattedDuration);
            } else {
                return String.format("Currently working at %s (Total work: %s)",
                        currentTime, formattedDuration);
            }
        } else {
            workStartTime = null;
            return "Not working at " + currentTime;
        }
    }

    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return String.format("%02d:%02d", hours, minutes);
    }

    public WorkSessionState startWork(UserSession userSession) {
        WorkSessionState state = getCurrentWorkSessionState(userSession.getUserId());
        state.setSessionState(WorkSessionState.SessionState.STARTED);
        workStartTime = LocalDateTime.now();
        state.setStartTimestamp(Timestamp.valueOf(workStartTime));
        scheduleUserTable.saveWorkSessionState(userSession.getUserId(), state);
        scheduleUserTable.insertTimeProcessing(userSession.getUserId(), state.getStartTimestamp(), state.getSessionState());
        LoggerUtility.info("Work started for user " + userSession.getUserId());
        return state;
    }

    public WorkSessionState togglePause(UserSession userSession) {
        WorkSessionState state = getCurrentWorkSessionState(userSession.getUserId());
        if (state.isWorking()) {
            Timestamp currentTime = Timestamp.valueOf(LocalDateTime.now());
            if (state.isPaused()) {
                state.setSessionState(WorkSessionState.SessionState.STARTED);
                scheduleUserTable.insertTimeProcessing(userSession.getUserId(), currentTime, WorkSessionState.SessionState.STARTED);
                LoggerUtility.info("Work resumed for user " + userSession.getUserId());
            } else {
                state.setSessionState(WorkSessionState.SessionState.PAUSED);
                scheduleUserTable.updateTimeProcessing(userSession.getUserId(), currentTime, WorkSessionState.SessionState.PAUSED);
                LoggerUtility.info("Work paused for user " + userSession.getUserId());
            }
            scheduleUserTable.saveWorkSessionState(userSession.getUserId(), state);
        }
        return state;
    }

    public WorkSessionState endWork(UserSession userSession) {
        WorkSessionState state = getCurrentWorkSessionState(userSession.getUserId());
        if (state.isWorking()) {
            Timestamp endTime = Timestamp.valueOf(LocalDateTime.now());
            state.setSessionState(WorkSessionState.SessionState.ENDED);
            scheduleUserTable.updateTimeProcessing(userSession.getUserId(), endTime, WorkSessionState.SessionState.ENDED);
            scheduleUserTable.finalizeWorkDay(userSession.getUserId(), endTime);
            scheduleUserTable.clearWorkSessionState(userSession.getUserId());
            workStartTime = null;
            LoggerUtility.info("Work ended for user " + userSession.getUserId());
        }
        return state;
    }

    public WorkSessionState getCurrentWorkSessionState(int userId) {
        WorkSessionState state = scheduleUserTable.getWorkSessionState(userId);
        LoggerUtility.info("Retrieved work session state for user " + userId +
                ": sessionState=" + state.getSessionState() +
                ", isWorking=" + state.isWorking() +
                ", isPaused=" + state.isPaused());
        return state;
    }
}