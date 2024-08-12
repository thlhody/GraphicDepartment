package cottontex.graphdep.services.user;

import cottontex.graphdep.database.interfaces.user.IScheduleUserTable;
import cottontex.graphdep.models.WorkSessionState;
import cottontex.graphdep.utils.LoggerUtility;

import java.sql.Date;

public class UserBaseService {
    private final IScheduleUserTable scheduleUserTable;

    public UserBaseService(IScheduleUserTable scheduleUserTable) {
        this.scheduleUserTable = scheduleUserTable;
    }

    public WorkSessionState initializeWorkState(Integer userId) {
        Date currentDate = new Date(System.currentTimeMillis());
        WorkSessionState workSessionState = scheduleUserTable.getWorkSessionState(userId);

        if (!workSessionState.isWorking() && scheduleUserTable.hasActiveSession(userId, currentDate)) {
            LoggerUtility.info("Active session found for user " + userId + ". Resuming work state.");
            workSessionState.setWorking(true);
            workSessionState.setPaused(true);
            scheduleUserTable.saveWorkSessionState(userId, workSessionState);
        }

        return workSessionState;
    }

    public void saveWorkSessionState(Integer userId, WorkSessionState workSessionState) {
        scheduleUserTable.saveWorkSessionState(userId, workSessionState);
        LoggerUtility.info("Work session state saved for user " + userId);
    }

    public void clearWorkSessionState(Integer userId) {
        scheduleUserTable.clearWorkSessionState(userId);
        LoggerUtility.info("Work session state cleared for user " + userId);
    }
}