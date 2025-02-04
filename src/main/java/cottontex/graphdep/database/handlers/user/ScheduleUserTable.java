package cottontex.graphdep.database.handlers.user;

import com.fasterxml.jackson.core.type.TypeReference;
import cottontex.graphdep.constants.JsonPaths;
import cottontex.graphdep.database.BaseDatabase;
import cottontex.graphdep.constants.SQLQueries;
import cottontex.graphdep.database.interfaces.user.IScheduleUserTable;
import cottontex.graphdep.models.WorkIntervalOffline;
import cottontex.graphdep.models.WorkSessionState;
import cottontex.graphdep.models.WorkSessionStateOffline;
import cottontex.graphdep.utils.LoggerUtility;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScheduleUserTable extends BaseDatabase implements IScheduleUserTable {


    public boolean saveStartHour(Integer userId, Timestamp startTimestamp) {
        if (isUsingLocalStorage()) {
            return saveStartHourToJson(userId, startTimestamp);
        } else {
            return saveStartHourToDatabase(userId, startTimestamp);
        }
    }

    private boolean saveStartHourToJson(Integer userId, Timestamp startTimestamp) {
        List<WorkSessionStateOffline> states = readFromJson(JsonPaths.WORK_SESSION_STATE_JSON, new TypeReference<List<WorkSessionStateOffline>>() {});
        WorkSessionStateOffline newState = new WorkSessionStateOffline(
                states.size() + 1,
                userId,
                true,
                false,
                startTimestamp.toLocalDateTime(),
                null,
                LocalDateTime.now(),
                "STARTED"
        );
        states.add(newState);
        writeToJson(JsonPaths.WORK_SESSION_STATE_JSON, states);
        return true;
    }

    private boolean saveStartHourToDatabase(Integer userId, Timestamp startTimestamp) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = getPreparedStatement(conn, SQLQueries.SAVE_START_HOUR)) {
            pstmt.setInt(1, userId);
            pstmt.setTimestamp(2, startTimestamp);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LoggerUtility.error("Error saving start hour to database", e);
            return false;
        }
    }


    public void savePauseTime(Integer userId, Timestamp pauseTimestamp) {
        if (isUsingLocalStorage()) {
            savePauseTimeToJson(userId, pauseTimestamp);
        } else {
            savePauseTimeToDatabase(userId, pauseTimestamp);
        }
    }

    private void savePauseTimeToJson(Integer userId, Timestamp pauseTimestamp) {
        List<WorkSessionStateOffline> states = readFromJson(JsonPaths.WORK_SESSION_STATE_JSON, new TypeReference<List<WorkSessionStateOffline>>() {});
        states.stream()
                .filter(state -> state.getUserId() == userId && "STARTED".equals(state.getSessionState()))
                .findFirst()
                .ifPresent(state -> {
                    state.setPaused(true);
                    state.setPauseTimestamp(pauseTimestamp.toLocalDateTime());
                    state.setSessionState("PAUSED");
                });
        writeToJson(JsonPaths.WORK_SESSION_STATE_JSON, states);
    }

    private void savePauseTimeToDatabase(Integer userId, Timestamp pauseTimestamp) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = getPreparedStatement(conn, SQLQueries.SAVE_PAUSE_TIME)) {
            pstmt.setTimestamp(1, pauseTimestamp);
            pstmt.setTimestamp(2, pauseTimestamp);
            pstmt.setInt(3, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LoggerUtility.error("Error saving pause time to database", e);
        }
    }

    @Override
    public List<WorkIntervalOffline> getAllWorkIntervals() {
        if (isUsingLocalStorage()) {
            return readFromJson(JsonPaths.WORK_INTERVAL_JSON, new TypeReference<List<WorkIntervalOffline>>() {});
        } else {
            return getAllWorkIntervalsFromDatabase();
        }
    }

    private List<WorkIntervalOffline> getAllWorkIntervalsFromDatabase() {
        List<WorkIntervalOffline> workIntervals = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = getPreparedStatement(conn, "SELECT * FROM work_interval");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                workIntervals.add(createWorkIntervalFromResultSet(rs));
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error fetching all work intervals from database", e);
        }
        return workIntervals;
    }

    private WorkIntervalOffline createWorkIntervalFromResultSet(ResultSet rs) throws SQLException {
        return new WorkIntervalOffline(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getTimestamp("first_start_time").toLocalDateTime(),
                rs.getInt("breaks"),
                rs.getTime("breaks_time") != null ? rs.getTime("breaks_time").toLocalTime() : null,
                rs.getTimestamp("end_time") != null ? rs.getTimestamp("end_time").toLocalDateTime() : null,
                rs.getTime("total_worked_time") != null ? rs.getTime("total_worked_time").toLocalTime() : null,
                rs.getString("time_off_type"),
                rs.getDate("work_date").toLocalDate(),
                rs.getInt("total_worked_seconds")
        );
    }

    @Override
    public void saveWorkInterval(WorkIntervalOffline workInterval) {
        if (isUsingLocalStorage()) {
            saveWorkIntervalToJson(workInterval);
        } else {
            saveWorkIntervalToDatabase(workInterval);
        }
    }

    private void saveWorkIntervalToJson(WorkIntervalOffline workInterval) {
        List<WorkIntervalOffline> intervals = readFromJson(JsonPaths.WORK_INTERVAL_JSON, new TypeReference<List<WorkIntervalOffline>>() {});
        intervals.add(workInterval);
        writeToJson(JsonPaths.WORK_INTERVAL_JSON, intervals);
    }

    private void saveWorkIntervalToDatabase(WorkIntervalOffline workInterval) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = getPreparedStatement(conn, "INSERT INTO work_interval (user_id, first_start_time, breaks, breaks_time, end_time, total_worked_time, time_off_type, work_date, total_worked_seconds) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            pstmt.setInt(1, workInterval.getUserId());
            pstmt.setTimestamp(2, Timestamp.valueOf(workInterval.getFirstStartTime()));
            pstmt.setInt(3, workInterval.getBreaks());
            pstmt.setTime(4, workInterval.getBreaksTime() != null ? Time.valueOf(workInterval.getBreaksTime()) : null);
            pstmt.setTimestamp(5, workInterval.getEndTime() != null ? Timestamp.valueOf(workInterval.getEndTime()) : null);
            pstmt.setTime(6, workInterval.getTotalWorkedTime() != null ? Time.valueOf(workInterval.getTotalWorkedTime()) : null);
            pstmt.setString(7, workInterval.getTimeOffType());
            pstmt.setDate(8, java.sql.Date.valueOf(workInterval.getWorkDate()));
            pstmt.setInt(9, workInterval.getTotalWorkedSeconds());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LoggerUtility.error("Error saving work interval to database", e);
        }
    }

    @Override
    public void finalizeWorkDay(Integer userId, Timestamp endTimestamp) {
        if (isUsingLocalStorage()) {
            finalizeWorkDayInJson(userId, endTimestamp);
        } else {
            finalizeWorkDayInDatabase(userId, endTimestamp);
        }
    }

    private void finalizeWorkDayInJson(Integer userId, Timestamp endTimestamp) {
        List<WorkSessionStateOffline> states = readFromJson(JsonPaths.WORK_SESSION_STATE_JSON, new TypeReference<List<WorkSessionStateOffline>>() {});
        states.stream()
                .filter(state -> state.getUserId() == userId && !"ENDED".equals(state.getSessionState()))
                .forEach(state -> {
                    state.setPauseTimestamp(endTimestamp.toLocalDateTime());
                    state.setSessionState("ENDED");
                });
        writeToJson(JsonPaths.WORK_SESSION_STATE_JSON, states);

        // Update WorkIntervalOffline
        List<WorkIntervalOffline> intervals = readFromJson(JsonPaths.WORK_INTERVAL_JSON, new TypeReference<List<WorkIntervalOffline>>() {});
        Optional<WorkIntervalOffline> latestInterval = intervals.stream()
                .filter(interval -> interval.getUserId() == userId && interval.getEndTime() == null)
                .findFirst();

        latestInterval.ifPresent(interval -> {
            interval.setEndTime(endTimestamp.toLocalDateTime());
            interval.setTotalWorkedTime(calculateTotalWorkedTime(interval.getFirstStartTime(), endTimestamp.toLocalDateTime()));
            interval.setTotalWorkedSeconds(calculateTotalWorkedSeconds(interval.getFirstStartTime(), endTimestamp.toLocalDateTime()));
        });

        writeToJson(JsonPaths.WORK_INTERVAL_JSON, intervals);
    }

    private void finalizeWorkDayInDatabase(Integer userId, Timestamp endTimestamp) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtUpdate = getPreparedStatement(conn, SQLQueries.FINALIZE_WORK_DAY_TIME_PROCESSING)) {
                pstmtUpdate.setTimestamp(1, endTimestamp);
                pstmtUpdate.setTimestamp(2, endTimestamp);
                pstmtUpdate.setInt(3, userId);
                int updatedRows = pstmtUpdate.executeUpdate();
                LoggerUtility.info("Updated " + updatedRows + " rows in time_processing table");
            }

            try (CallableStatement callStmt = getCallableStatement(conn, SQLQueries.FINALIZE_WORK_DAY_CALL_PROCEDURE)) {
                callStmt.setInt(1, userId);
                callStmt.setDate(2, new java.sql.Date(endTimestamp.getTime()));
                boolean hasResults = callStmt.execute();
                LoggerUtility.info("Stored procedure executed. HasResults: " + hasResults);
            }

            conn.commit();
            LoggerUtility.info("Work day finalized successfully for user ID: " + userId);
        } catch (SQLException e) {
            LoggerUtility.error("Error finalizing work day for user ID: " + userId, e);
        }
    }

    private LocalTime calculateTotalWorkedTime(LocalDateTime start, LocalDateTime end) {
        long seconds = java.time.Duration.between(start, end).getSeconds();
        return LocalTime.ofSecondOfDay(seconds);
    }

    private int calculateTotalWorkedSeconds(LocalDateTime start, LocalDateTime end) {
        return (int) java.time.Duration.between(start, end).getSeconds();
    }

    @Override
    public WorkSessionState getWorkSessionState(Integer userId) {
        if (isUsingLocalStorage()) {
            return getWorkSessionStateFromJson(userId);
        } else {
            return getWorkSessionStateFromDatabase(userId);
        }
    }

    private WorkSessionState getWorkSessionStateFromJson(Integer userId) {
        List<WorkSessionStateOffline> states = readFromJson(JsonPaths.WORK_SESSION_STATE_JSON, new TypeReference<List<WorkSessionStateOffline>>() {});
        return states.stream()
                .filter(state -> state.getUserId() == userId)
                .findFirst()
                .map(this::convertToWorkSessionState)
                .orElseGet(WorkSessionState::new);
    }

    private WorkSessionState getWorkSessionStateFromDatabase(Integer userId) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = getPreparedStatement(conn, SQLQueries.GET_WORK_SESSION_STATE)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createWorkSessionStateFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error getting work session state for user " + userId, e);
        }
        return new WorkSessionState();
    }

    private WorkSessionState createWorkSessionStateFromResultSet(ResultSet rs) throws SQLException {
        WorkSessionState state = new WorkSessionState();
        String sessionStateStr = rs.getString("session_state");
        Timestamp startTimestamp = rs.getTimestamp("time_a");

        if (sessionStateStr != null) {
            state.setSessionState(WorkSessionState.SessionState.valueOf(sessionStateStr));
        } else {
            state.setSessionState(WorkSessionState.SessionState.ENDED);
        }

        state.setStartTimestamp(startTimestamp);
        return state;
    }

    private WorkSessionState convertToWorkSessionState(WorkSessionStateOffline offlineState) {
        WorkSessionState state = new WorkSessionState();
        state.setWorking(offlineState.isWorking());
        state.setPaused(offlineState.isPaused());
        state.setStartTimestamp(Timestamp.valueOf(offlineState.getStartTimestamp()));
        state.setPauseTimestamp(offlineState.getPauseTimestamp() != null ? Timestamp.valueOf(offlineState.getPauseTimestamp()) : null);
        state.setSessionState(WorkSessionState.SessionState.valueOf(offlineState.getSessionState()));
        return state;
    }

    @Override
    public void saveWorkSessionState(Integer userId, WorkSessionState state) {
        if (isUsingLocalStorage()) {
            saveWorkSessionStateToJson(userId, state);
        } else {
            saveWorkSessionStateToDatabase(userId, state);
        }
    }

    private void saveWorkSessionStateToJson(Integer userId, WorkSessionState state) {
        List<WorkSessionStateOffline> states = readFromJson(JsonPaths.WORK_SESSION_STATE_JSON, new TypeReference<List<WorkSessionStateOffline>>() {});
        WorkSessionStateOffline offlineState = new WorkSessionStateOffline(
                states.size() + 1,
                userId,
                state.isWorking(),
                state.isPaused(),
                state.getStartTimestamp().toLocalDateTime(),
                state.getPauseTimestamp() != null ? state.getPauseTimestamp().toLocalDateTime() : null,
                LocalDateTime.now(),
                state.getSessionState().name()
        );
        states.add(offlineState);
        writeToJson(JsonPaths.WORK_SESSION_STATE_JSON, states);
    }

    private void saveWorkSessionStateToDatabase(Integer userId, WorkSessionState state) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = getPreparedStatement(conn, SQLQueries.SAVE_WORK_SESSION_STATE)) {
            pstmt.setInt(1, userId);
            pstmt.setBoolean(2, state.isWorking());
            pstmt.setBoolean(3, state.isPaused());
            pstmt.setTimestamp(4, state.getStartTimestamp());
            pstmt.setTimestamp(5, state.getPauseTimestamp());
            pstmt.setString(6, state.getSessionState().name());
            pstmt.executeUpdate();
            LoggerUtility.info("Saved work session state for user " + userId +
                    ": isWorking=" + state.isWorking() + ", isPaused=" + state.isPaused() +
                    ", sessionState=" + state.getSessionState());
        } catch (SQLException e) {
            LoggerUtility.error("Error saving work session state for user " + userId, e);
        }
    }

    @Override
    public void clearWorkSessionState(Integer userId) {
        if (isUsingLocalStorage()) {
            clearWorkSessionStateFromJson(userId);
        } else {
            clearWorkSessionStateFromDatabase(userId);
        }
    }

    private void clearWorkSessionStateFromJson(Integer userId) {
        List<WorkSessionStateOffline> states = readFromJson(JsonPaths.WORK_SESSION_STATE_JSON, new TypeReference<List<WorkSessionStateOffline>>() {});
        states.removeIf(state -> state.getUserId() == userId);
        writeToJson(JsonPaths.WORK_SESSION_STATE_JSON, states);
    }

    private void clearWorkSessionStateFromDatabase(Integer userId) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = getPreparedStatement(conn, SQLQueries.CLEAR_WORK_SESSION_STATE)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LoggerUtility.error("Error clearing work session state", e);
        }
    }

    @Override
    public void insertTimeProcessing(Integer userId, Timestamp startTime, WorkSessionState.SessionState sessionState) {
        if (isUsingLocalStorage()) {
            insertTimeProcessingToJson(userId, startTime, sessionState);
        } else {
            insertTimeProcessingToDatabase(userId, startTime, sessionState);
        }
    }

    private void insertTimeProcessingToJson(Integer userId, Timestamp startTime, WorkSessionState.SessionState sessionState) {
        List<WorkIntervalOffline> intervals = readFromJson(JsonPaths.WORK_INTERVAL_JSON, new TypeReference<List<WorkIntervalOffline>>() {});
        WorkIntervalOffline newInterval = new WorkIntervalOffline();
        newInterval.setId(intervals.size() + 1);
        newInterval.setUserId(userId);
        newInterval.setFirstStartTime(startTime.toLocalDateTime());
        newInterval.setWorkDate(startTime.toLocalDateTime().toLocalDate());
        intervals.add(newInterval);
        writeToJson(JsonPaths.WORK_INTERVAL_JSON, intervals);

        // Also update work session state
        List<WorkSessionStateOffline> states = readFromJson(JsonPaths.WORK_SESSION_STATE_JSON, new TypeReference<List<WorkSessionStateOffline>>() {});
        WorkSessionStateOffline newState = new WorkSessionStateOffline(
                states.size() + 1,
                userId,
                true,
                false,
                startTime.toLocalDateTime(),
                null,
                LocalDateTime.now(),
                sessionState.name()
        );
        states.add(newState);
        writeToJson(JsonPaths.WORK_SESSION_STATE_JSON, states);
    }

    private void insertTimeProcessingToDatabase(Integer userId, Timestamp startTime, WorkSessionState.SessionState sessionState) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = getPreparedStatement(conn, SQLQueries.INSERT_TIME_PROCESSING)) {
            pstmt.setInt(1, userId);
            pstmt.setTimestamp(2, startTime);
            pstmt.setString(3, sessionState.name());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                LoggerUtility.info("Inserted new time processing record for user " + userId);
            } else {
                LoggerUtility.warn("Failed to insert time processing record for user " + userId);
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error inserting time processing record", e);
        }
    }

    @Override
    public void updateTimeProcessing(Integer userId, Timestamp endTime, WorkSessionState.SessionState sessionState) {
        if (isUsingLocalStorage()) {
            updateTimeProcessingInJson(userId, endTime, sessionState);
        } else {
            updateTimeProcessingInDatabase(userId, endTime, sessionState);
        }
    }

    private void updateTimeProcessingInJson(Integer userId, Timestamp endTime, WorkSessionState.SessionState sessionState) {
        List<WorkIntervalOffline> intervals = readFromJson(JsonPaths.WORK_INTERVAL_JSON, new TypeReference<List<WorkIntervalOffline>>() {});
        intervals.stream()
                .filter(interval -> interval.getUserId() == userId && interval.getEndTime() == null)
                .findFirst()
                .ifPresent(interval -> {
                    interval.setEndTime(endTime.toLocalDateTime());
                    interval.setTotalWorkedTime(calculateTotalWorkedTime(interval.getFirstStartTime(), endTime.toLocalDateTime()));
                    interval.setTotalWorkedSeconds(calculateTotalWorkedSeconds(interval.getFirstStartTime(), endTime.toLocalDateTime()));
                });
        writeToJson(JsonPaths.WORK_INTERVAL_JSON, intervals);

        // Update work session state
        List<WorkSessionStateOffline> states = readFromJson(JsonPaths.WORK_SESSION_STATE_JSON, new TypeReference<List<WorkSessionStateOffline>>() {});
        states.stream()
                .filter(state -> state.getUserId() == userId && !"ENDED".equals(state.getSessionState()))
                .findFirst()
                .ifPresent(state -> {
                    state.setPauseTimestamp(endTime.toLocalDateTime());
                    state.setSessionState(sessionState.name());
                });
        writeToJson(JsonPaths.WORK_SESSION_STATE_JSON, states);
    }

    private void updateTimeProcessingInDatabase(Integer userId, Timestamp endTime, WorkSessionState.SessionState sessionState) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = getPreparedStatement(conn, SQLQueries.UPDATE_TIME_PROCESSING)) {
            pstmt.setTimestamp(1, endTime);
            pstmt.setString(2, sessionState.name());
            pstmt.setInt(3, userId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                LoggerUtility.info("Updated time processing record for user " + userId);
            } else {
                LoggerUtility.warn("No active time processing record found to update for user " + userId);
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error updating time processing record", e);
        }
    }
}