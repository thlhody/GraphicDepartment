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

    public WorkSessionState initializeWorkState(int userId) {
        WorkSessionState state = scheduleUserTable.getWorkSessionState(userId);
        if (state == null) {
            LoggerUtility.info("No existing work session state found for user " + userId + ". Creating new state.");
            state = new WorkSessionState();
        } else {
            LoggerUtility.info("Retrieved existing work session state for user " + userId +
                    ": isWorking=" + state.isWorking() + ", isPaused=" + state.isPaused());
        }
        return state;
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