package cottontex.graphdep.database.handlers.user;

import com.fasterxml.jackson.core.type.TypeReference;
import cottontex.graphdep.database.*;
import cottontex.graphdep.constants.*;
import cottontex.graphdep.database.interfaces.user.IUserTimeTableHandler;
import cottontex.graphdep.models.TimeProcessingOffline;
import cottontex.graphdep.models.WorkHourEntry;
import cottontex.graphdep.models.WorkSessionStateOffline;
import cottontex.graphdep.utils.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserTimeTableHandler extends BaseDatabase implements IUserTimeTableHandler {

    public List<WorkHourEntry> getUserMonthlyWorkHours(Integer userId) {
        List<WorkHourEntry> workHours = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = getPreparedStatement(conn, SQLQueries.GET_MONTHLY_WORK_HOURS_USER);
            pstmt.setInt(1, userId);
            LoggerUtility.info("Executing query: " + SQLQueries.GET_MONTHLY_WORK_HOURS_USER + " with userId: " + userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String timeOffType = rs.getString("time_off_type");
                WorkHourEntry entry = new WorkHourEntry(
                        rs.getDate("work_date") != null ? rs.getDate("work_date").toLocalDate() : null,
                        timeOffType == null ? (rs.getTimestamp("first_start_time") != null ? rs.getTimestamp("first_start_time").toLocalDateTime() : null) : null,
                        timeOffType == null ? rs.getInt("breaks") : null,
                        timeOffType == null ? (rs.getTime("breaks_time") != null ? rs.getTime("breaks_time").toLocalTime() : null) : null,
                        timeOffType == null ? (rs.getTimestamp("end_time") != null ? rs.getTimestamp("end_time").toLocalDateTime() : null) : null,
                        timeOffType == null ? (rs.getTime("total_worked_time") != null ? rs.getTime("total_worked_time").toLocalTime() : null) : null,
                        timeOffType
                );
                workHours.add(entry);
                LoggerUtility.info("Added work hour entry: " + entry);
            }
            LoggerUtility.info("Retrieved " + workHours.size() + " work hour entries for user " + userId);
        } catch (SQLException e) {
            LoggerUtility.error("Error fetching user monthly work hours for userId: " + userId, e);
        } finally {
            closeResources(conn, pstmt, rs);
        }

        return workHours;
    }

    @Override
    public List<TimeProcessingOffline> getAllTimeProcessing() {
        List<TimeProcessingOffline> timeProcessings = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            if (conn != null) {
                pstmt = getPreparedStatement(conn, "SELECT * FROM time_processing");
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    TimeProcessingOffline timeProcessing = new TimeProcessingOffline(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getTimestamp("time_a").toLocalDateTime(),
                            rs.getTimestamp("time_b") != null ? rs.getTimestamp("time_b").toLocalDateTime() : null,
                            rs.getDouble("duration"),
                            rs.getString("session_state")
                    );
                    timeProcessings.add(timeProcessing);
                }
            } else {
                // If database is not available, read from JSON
                timeProcessings = readFromJson(JsonPaths.TIME_PROCESSING_JSON, new TypeReference<List<TimeProcessingOffline>>() {});
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error fetching all time processing entries", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return timeProcessings;
    }

    @Override
    public void saveTimeProcessing(TimeProcessingOffline timeProcessing) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            if (conn != null) {
                pstmt = getPreparedStatement(conn, "INSERT INTO time_processing (user_id, time_a, time_b, duration, session_state) VALUES (?, ?, ?, ?, ?)");
                pstmt.setInt(1, timeProcessing.getUserId());
                pstmt.setTimestamp(2, Timestamp.valueOf(timeProcessing.getTimeA()));
                pstmt.setTimestamp(3, timeProcessing.getTimeB() != null ? Timestamp.valueOf(timeProcessing.getTimeB()) : null);
                pstmt.setDouble(4, timeProcessing.getDuration());
                pstmt.setString(5, timeProcessing.getSessionState());
                pstmt.executeUpdate();
            } else {
                // If database is not available, save to JSON
                List<TimeProcessingOffline> timeProcessings = readFromJson(JsonPaths.TIME_PROCESSING_JSON, new TypeReference<List<TimeProcessingOffline>>() {});
                timeProcessings.add(timeProcessing);
                writeToJson(JsonPaths.TIME_PROCESSING_JSON, timeProcessings);
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error saving time processing entry", e);
        } finally {
            closeResources(conn, pstmt, null);
        }
    }

    @Override
    public List<WorkSessionStateOffline> getAllWorkSessionStates() {
        List<WorkSessionStateOffline> workSessionStates = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            if (conn != null) {
                pstmt = getPreparedStatement(conn, "SELECT * FROM work_session_state");
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    WorkSessionStateOffline state = new WorkSessionStateOffline(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getBoolean("is_working"),
                            rs.getBoolean("is_paused"),
                            rs.getTimestamp("start_timestamp") != null ? rs.getTimestamp("start_timestamp").toLocalDateTime() : null,
                            rs.getTimestamp("pause_timestamp") != null ? rs.getTimestamp("pause_timestamp").toLocalDateTime() : null,
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            rs.getString("session_state")
                    );
                    workSessionStates.add(state);
                }
            } else {
                // If database is not available, read from JSON
                workSessionStates = readFromJson(JsonPaths.WORK_SESSION_STATE_JSON, new TypeReference<List<WorkSessionStateOffline>>() {});
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error fetching all work session states", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return workSessionStates;
    }

    @Override
    public void saveWorkSessionState(WorkSessionStateOffline workSessionState) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            if (conn != null) {
                pstmt = getPreparedStatement(conn, "INSERT INTO work_session_state (user_id, is_working, is_paused, start_timestamp, pause_timestamp, session_state) VALUES (?, ?, ?, ?, ?, ?)");
                pstmt.setInt(1, workSessionState.getUserId());
                pstmt.setBoolean(2, workSessionState.isWorking());
                pstmt.setBoolean(3, workSessionState.isPaused());
                pstmt.setTimestamp(4, workSessionState.getStartTimestamp() != null ? Timestamp.valueOf(workSessionState.getStartTimestamp()) : null);
                pstmt.setTimestamp(5, workSessionState.getPauseTimestamp() != null ? Timestamp.valueOf(workSessionState.getPauseTimestamp()) : null);
                pstmt.setString(6, workSessionState.getSessionState());
                pstmt.executeUpdate();
            } else {
                // If database is not available, save to JSON
                List<WorkSessionStateOffline> states = readFromJson(JsonPaths.WORK_SESSION_STATE_JSON, new TypeReference<List<WorkSessionStateOffline>>() {});
                states.add(workSessionState);
                writeToJson(JsonPaths.WORK_SESSION_STATE_JSON, states);
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error saving work session state", e);
        } finally {
            closeResources(conn, pstmt, null);
        }
    }
}