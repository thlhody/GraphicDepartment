package cottontex.graphdep.services.user;

import cottontex.graphdep.database.interfaces.user.IScheduleUserTable;
import cottontex.graphdep.models.UserSession;
import cottontex.graphdep.models.WorkSessionState;
import cottontex.graphdep.utils.DateTimeUtils;
import cottontex.graphdep.utils.LoggerUtility;

import java.sql.Timestamp;

public class UserService {
    private final IScheduleUserTable scheduleUserTable;

    public UserService(IScheduleUserTable scheduleUserTable) {
        this.scheduleUserTable = scheduleUserTable;
    }

    public WorkSessionState startWork(UserSession userSession, WorkSessionState workSessionState) {
        if (!workSessionState.isWorking() && userSession != null) {
            int userId = userSession.getUserId();
            Timestamp startTimestamp = new Timestamp(System.currentTimeMillis());
            scheduleUserTable.insertTimeProcessing(userId, startTimestamp);

            workSessionState.setWorking(true);
            workSessionState.setPaused(false);
            workSessionState.setStartTimestamp(startTimestamp);
            workSessionState.setPauseTimestamp(null);
            scheduleUserTable.saveWorkSessionState(userId, workSessionState);

            LoggerUtility.info("Work started for user " + userId + " at " + startTimestamp);
        }
        return workSessionState;
    }

    public WorkSessionState togglePause(UserSession userSession, WorkSessionState workSessionState) {
        if (workSessionState.isWorking() && userSession != null) {
            int userId = userSession.getUserId();
            Timestamp pauseTimestamp = new Timestamp(System.currentTimeMillis());

            if (!workSessionState.isPaused()) {
                scheduleUserTable.updateTimeProcessing(userId, pauseTimestamp);
                scheduleUserTable.insertTimeProcessing(userId, null);
                workSessionState.setPaused(true);
                workSessionState.setPauseTimestamp(pauseTimestamp);
                LoggerUtility.info("Work paused for user " + userId + " at " + pauseTimestamp);
            } else {
                scheduleUserTable.updateTimeProcessing(userId, pauseTimestamp);
                workSessionState.setPaused(false);
                workSessionState.setPauseTimestamp(null);
                LoggerUtility.info("Work resumed for user " + userId + " at " + pauseTimestamp);
            }

            scheduleUserTable.saveWorkSessionState(userId, workSessionState);
        }
        return workSessionState;
    }

    public WorkSessionState endWork(UserSession userSession, WorkSessionState workSessionState) {
        if (workSessionState.isWorking() && userSession != null) {
            int userId = userSession.getUserId();
            Timestamp endTimestamp = new Timestamp(System.currentTimeMillis());
            scheduleUserTable.updateTimeProcessing(userId, endTimestamp);
            scheduleUserTable.finalizeWorkDay(userId, endTimestamp);

            scheduleUserTable.clearWorkSessionState(userId);
            workSessionState.reset();
            LoggerUtility.info("Work ended for user " + userId + " at " + endTimestamp);
        }
        return workSessionState;
    }

    public String getDisplayTimeInfo(WorkSessionState workSessionState) {
        if (workSessionState.isWorking()) {
            if (workSessionState.isPaused()) {
                return "Work paused at " + DateTimeUtils.getCurrentDateTimeForDisplay();
            } else {
                return "Currently working at " + DateTimeUtils.getCurrentDateTimeForDisplay();
            }
        } else {
            return "Not working at " + DateTimeUtils.getCurrentDateTimeForDisplay();
        }
    }
}