package cottontex.graphdep.database.queries;

import cottontex.graphdep.database.DatabaseConnection;
import cottontex.graphdep.models.UserStatus;
import cottontex.graphdep.utils.LoggerUtility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserStatusHandler {

    public List<UserStatus> getUserStatuses() {
        List<UserStatus> statuses = new ArrayList<>();
        String sql = "SELECT u.user_id, u.username, " +
                "MIN(CASE WHEN tp.time_b IS NULL THEN tp.time_a ELSE NULL END) as start_time, " +
                "MAX(tp.time_b) as end_time " +
                "FROM users u " +
                "LEFT JOIN time_processing tp ON u.user_id = tp.user_id " +
                "WHERE u.user_id != 1 AND (DATE(tp.time_a) = CURDATE() OR tp.time_a IS NULL) " +
                "GROUP BY u.user_id, u.username " +
                "ORDER BY u.username";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                UserStatus status = new UserStatus(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        formatTime(rs.getString("start_time")),
                        formatTime(rs.getString("end_time"))
                );
                statuses.add(status);
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error fetching user statuses", e);
        }

        return statuses;
    }

    private String formatTime(String time) {
        return time != null ? time.substring(11, 16) : "N/A"; // Extract HH:mm from timestamp
    }
}