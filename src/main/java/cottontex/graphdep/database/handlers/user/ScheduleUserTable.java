package cottontex.graphdep.database.handlers.user;

import cottontex.graphdep.database.BaseDatabase;
import cottontex.graphdep.constants.SQLQueries;
import cottontex.graphdep.database.interfaces.user.IScheduleUserTable;
import cottontex.graphdep.models.WorkSessionState;
import cottontex.graphdep.utils.LoggerUtility;

import java.sql.*;

public class ScheduleUserTable extends BaseDatabase implements IScheduleUserTable {

    public boolean saveStartHour(Integer userId, Timestamp startTimestamp) {
        try (Connection conn = getConnection(); PreparedStatement pstmt = getPreparedStatement(conn, SQLQueries.SAVE_START_HOUR)) {
            pstmt.setInt(1, userId);
            pstmt.setTimestamp(2, startTimestamp);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LoggerUtility.error("Error saving start hour", e);
            return false;
        }
    }

    public void savePauseTime(Integer userId, Timestamp pauseTimestamp) {
        try (Connection conn = getConnection(); PreparedStatement pstmt = getPreparedStatement(conn, SQLQueries.SAVE_PAUSE_TIME)) {
            pstmt.setTimestamp(1, pauseTimestamp);
            pstmt.setTimestamp(2, pauseTimestamp);
            pstmt.setInt(3, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LoggerUtility.error("Error saving pause time", e);
        }
    }

    public void finalizeWorkDay(Integer userId, Timestamp endTimestamp) {
        Connection conn = null;
        PreparedStatement pstmtUpdate = null;
        CallableStatement callStmt = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            pstmtUpdate = getPreparedStatement(conn, SQLQueries.FINALIZE_WORK_DAY_TIME_PROCESSING);
            pstmtUpdate.setTimestamp(1, endTimestamp);
            pstmtUpdate.setTimestamp(2, endTimestamp);
            pstmtUpdate.setInt(3, userId);
            int updatedRows = pstmtUpdate.executeUpdate();
            LoggerUtility.info("Updated " + updatedRows + " rows in time_processing table");

            callStmt = getCallableStatement(conn, SQLQueries.FINALIZE_WORK_DAY_CALL_PROCEDURE);
            callStmt.setInt(1, userId);
            callStmt.setDate(2, new java.sql.Date(endTimestamp.getTime()));
            boolean hasResults = callStmt.execute();
            LoggerUtility.info("Stored procedure executed. HasResults: " + hasResults);

            conn.commit();
            LoggerUtility.info("Work day finalized successfully for user ID: " + userId);
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    LoggerUtility.info("Transaction rolled back due to error");
                } catch (SQLException ex) {
                    LoggerUtility.error("Error rolling back transaction", ex);
                }
            }
            LoggerUtility.error("Error finalizing work day for user ID: " + userId, e);
        } finally {
            closeResources(conn, pstmtUpdate, null);
            if (callStmt != null) {
                try {
                    callStmt.close();
                } catch (SQLException e) {
                    LoggerUtility.error("Error closing CallableStatement", e);
                }
            }
        }
    }

    public boolean hasActiveSession(Integer userId, Date date) {
        try (Connection conn = getConnection(); PreparedStatement pstmt = getPreparedStatement(conn, SQLQueries.HAS_ACTIVE_SESSION)) {
            pstmt.setInt(1, userId);
            pstmt.setDate(2, date); // This should now be correct as we're passing java.sql.Date
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error checking for active session", e);
        }
        return false;
    }

    @Override
    public WorkSessionState getWorkSessionState(Integer userId) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = getPreparedStatement(conn, SQLQueries.GET_WORK_SESSION_STATE)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                WorkSessionState state = new WorkSessionState();
                state.setWorking(rs.getBoolean("is_working"));
                state.setPaused(rs.getBoolean("is_paused"));
                state.setStartTimestamp(rs.getTimestamp("start_timestamp"));
                state.setPauseTimestamp(rs.getTimestamp("pause_timestamp"));
                return state;
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error getting work session state", e);
        }
        return new WorkSessionState(); // Return default state if no record found
    }

    @Override
    public void saveWorkSessionState(Integer userId, WorkSessionState state) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = getPreparedStatement(conn, SQLQueries.SAVE_WORK_SESSION_STATE)) {
            pstmt.setInt(1, userId);
            pstmt.setBoolean(2, state.isWorking());
            pstmt.setBoolean(3, state.isPaused());
            pstmt.setTimestamp(4, state.getStartTimestamp());
            pstmt.setTimestamp(5, state.getPauseTimestamp());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LoggerUtility.error("Error saving work session state", e);
        }
    }

    @Override
    public void clearWorkSessionState(Integer userId) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = getPreparedStatement(conn, SQLQueries.CLEAR_WORK_SESSION_STATE)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LoggerUtility.error("Error clearing work session state", e);
        }
    }

    @Override
    public void insertTimeProcessing(Integer userId, Timestamp startTime) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = getPreparedStatement(conn, SQLQueries.INSERT_TIME_PROCESSING)) {
            pstmt.setInt(1, userId);
            pstmt.setTimestamp(2, startTime);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LoggerUtility.error("Error inserting time processing", e);
        }
    }

    @Override
    public void updateTimeProcessing(Integer userId, Timestamp endTime) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = getPreparedStatement(conn, SQLQueries.UPDATE_TIME_PROCESSING)) {
            pstmt.setTimestamp(1, endTime);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LoggerUtility.error("Error updating time processing", e);
        }
    }
}