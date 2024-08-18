package cottontex.graphdep.database.interfaces.user;

import cottontex.graphdep.models.WorkSessionState;

import java.sql.Timestamp;

public interface IScheduleUserTable {
    void finalizeWorkDay(Integer userId, Timestamp endTimestamp);
    WorkSessionState getWorkSessionState(Integer userId);
    void saveWorkSessionState(Integer userId, WorkSessionState state);
    void clearWorkSessionState(Integer userId);
    void insertTimeProcessing(Integer userId, Timestamp startTime, WorkSessionState.SessionState sessionState);
    void updateTimeProcessing(Integer userId, Timestamp endTime, WorkSessionState.SessionState sessionState);}