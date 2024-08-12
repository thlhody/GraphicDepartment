package cottontex.graphdep.database.handlers.user;

import cottontex.graphdep.database.interfaces.user.IUserTimeOffHandler;
import cottontex.graphdep.utils.LoggerUtility;
import cottontex.graphdep.constants.SQLQueries;
import cottontex.graphdep.database.BaseDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class UserTimeOffHandler extends BaseDatabase implements IUserTimeOffHandler {


    public boolean saveTimeOff(Integer userId, List<LocalDate> workDays, String type) {
        if (userId == null) {
            LoggerUtility.error("Attempt to save time off with null userId");
            return false;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            pstmt = getPreparedStatement(conn, SQLQueries.TIME_OFF_UPDATE);

            LoggerUtility.info("Executing query: " + SQLQueries.TIME_OFF_UPDATE);
            LoggerUtility.info("User ID: " + userId + ", Number of days: " + workDays.size() + ", Type: " + type);

            for (LocalDate date : workDays) {
                pstmt.setInt(1, userId);
                pstmt.setTimestamp(2, Timestamp.valueOf(date.atStartOfDay()));
                pstmt.setTimestamp(3, Timestamp.valueOf(date.atTime(23, 59, 59)));
                pstmt.setTime(4, Time.valueOf("00:00:00")); // Set total_worked_time to 0
                pstmt.setString(5, type.startsWith("CO") ? "CO" : "CM");

                pstmt.addBatch();
                LoggerUtility.info("Added batch for date: " + date);
            }

            int[] results = pstmt.executeBatch();
            conn.commit();
            LoggerUtility.info("Batch execution completed. Affected rows: " + results.length);
            return results.length > 0;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    LoggerUtility.info("Transaction rolled back due to error");
                } catch (SQLException ex) {
                    LoggerUtility.error("Error rolling back transaction", ex);
                }
            }
            LoggerUtility.error("Error saving time off", e);
            return false;
        } finally {
            closeResources(conn, pstmt, null);
        }
    }
}
