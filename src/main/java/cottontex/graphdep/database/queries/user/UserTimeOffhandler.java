package cottontex.graphdep.database.queries.user;

import cottontex.graphdep.utils.LoggerUtility;
import cottontex.graphdep.constants.SQLQueries;
import cottontex.graphdep.database.BaseDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class UserTimeOffHandler extends BaseDatabase {

    public boolean saveTimeOff(Integer userID, List<LocalDate> workDays, String type) {
        String sql = SQLQueries.TIME_OFF_UPDATE;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (LocalDate date : workDays) {
                pstmt.setInt(1, userID);
                pstmt.setTimestamp(2, Timestamp.valueOf(date.atStartOfDay()));
                pstmt.setTimestamp(3, Timestamp.valueOf(date.atTime(23, 59, 59)));
                pstmt.setTime(4, Time.valueOf("00:00:00")); // Set total_worked_time to 0
                pstmt.setString(5, type.startsWith("CO") ? "CO" : "CM");

                pstmt.addBatch();
            }

            int[] results = pstmt.executeBatch();
            return results.length > 0;
        } catch (SQLException e) {
            LoggerUtility.error("Error saving time off", e);
            return false;
        }
    }
}