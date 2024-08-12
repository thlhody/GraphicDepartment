package cottontex.graphdep.database.interfaces.user;

import cottontex.graphdep.models.WorkSessionState;

import java.sql.Date;
import java.sql.Timestamp;

public interface IScheduleUserTable {
    boolean saveStartHour(Integer userId, Timestamp startTimestamp);
    void savePauseTime(Integer userId, Timestamp pauseTimestamp);
    void finalizeWorkDay(Integer userId, Timestamp endTimestamp);
    boolean hasActiveSession(Integer userId, Date date);
    WorkSessionState getWorkSessionState(Integer userId);
    void saveWorkSessionState(Integer userId, WorkSessionState state);
    void clearWorkSessionState(Integer userId);
    void insertTimeProcessing(Integer userId, Timestamp startTime);
    void updateTimeProcessing(Integer userId, Timestamp endTime);
}