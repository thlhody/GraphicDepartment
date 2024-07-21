package cottontex.graphdep.database.queries;

import cottontex.graphdep.database.BaseDatabase;
import cottontex.graphdep.utils.LoggerUtility;

import java.sql.*;

public class ScheduleUserTable extends BaseDatabase {

    public boolean saveStartHour(Integer userId, Timestamp startTimestamp) {
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(SQLQueries.SAVE_START_HOUR)) {
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
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(SQLQueries.SAVE_PAUSE_TIME)) {
            pstmt.setTimestamp(1, pauseTimestamp);
            pstmt.setTimestamp(2, pauseTimestamp);
            pstmt.setInt(3, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LoggerUtility.error("Error saving pause time", e);
        }
    }

    public void finalizeWorkDay(Integer userId, Timestamp endTimestamp) {

        try (Connection conn = getConnection(); PreparedStatement pstmtUpdate = conn.prepareStatement(SQLQueries.FINALIZE_WORK_DAY_TIME_PROCESSING);
                                                CallableStatement callStmt = conn.prepareCall(SQLQueries.FINALIZE_WORK_DAY_CALL_PROCEDURE)) {

            // Update time_processing
            pstmtUpdate.setTimestamp(1, endTimestamp);
            pstmtUpdate.setTimestamp(2, endTimestamp);
            pstmtUpdate.setInt(3, userId);
            pstmtUpdate.executeUpdate();

            // Call the stored procedure
            callStmt.setInt(1, userId);
            callStmt.setDate(2, new java.sql.Date(endTimestamp.getTime()));
            callStmt.execute();

        } catch (SQLException e) {
            LoggerUtility.error("Error finalizing work day", e);
        }
    }

    public boolean hasActiveSession(Integer userId, Date date) {

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(SQLQueries.HAS_ACTIVE_SESSION)) {
            pstmt.setInt(1, userId);
            pstmt.setDate(2, new java.sql.Date(date.getTime()));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error checking for active session", e);
        }
        return false;
    }
}