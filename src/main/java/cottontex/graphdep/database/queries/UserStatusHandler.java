package cottontex.graphdep.database.queries;

import cottontex.graphdep.database.DatabaseConnection;
import cottontex.graphdep.models.UserStatus;
import cottontex.graphdep.utils.LoggerUtility;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserStatusHandler {

    public List<UserStatus> getUserStatuses() {
        List<UserStatus> userStatuses = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(SQLQueries.GET_USER_STATUSES);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                UserStatus status = new UserStatus(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getBoolean("is_online"),
                        rs.getString("start_time"),
                        rs.getString("end_time")
                );
                userStatuses.add(status);
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error retrieving user statuses: " + e.getMessage());
        }
        return userStatuses;
    }

    public UserStatus getUserStatus(Integer userId) {

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(SQLQueries.GET_USER_STATUS)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new UserStatus(
                            userId,
                            rs.getString("username"),
                            rs.getBoolean("is_online"),
                            rs.getString("start_time"),
                            rs.getString("end_time")
                    );
                }
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error fetching user status", e);
        }
        return null;
    }

    public void saveStartTime(Integer userId, Timestamp startTime) {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(SQLQueries.SAVE_START_TIME)) {
            pstmt.setInt(1, userId);
            pstmt.setTimestamp(2, startTime);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LoggerUtility.error("Error saving start time", e);
        }
    }

    public void savePauseTime(Integer userId, Timestamp pauseTime) {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(SQLQueries.SAVE_PAUSE_TIME)) {
            pstmt.setTimestamp(1, pauseTime);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LoggerUtility.error("Error saving pause time", e);
        }
    }

    public void saveEndTime(Integer userId, Timestamp endTime) {
        savePauseTime(userId, endTime);  // End time is essentially a pause time that doesn't get resumed
    }

    public static String getUsernameById(int userId) {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(SQLQueries.GET_USERNAME_BY_ID)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error fetching username for user ID: " + userId, e);
        }
        return "User"; // Default value if username can't be fetched
    }

}
