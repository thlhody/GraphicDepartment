package cottontex.graphdep.database.queries;

import cottontex.graphdep.database.BaseDatabase;
import cottontex.graphdep.utils.LoggerUtility;

import java.sql.*;

public class ScheduleUserTable extends BaseDatabase {

    public boolean isStartHourExists(Integer userId, Timestamp startTimestamp) {
        String sql = "SELECT COUNT(*) FROM time_processing WHERE user_id = ? AND DATE(time_a) = DATE(?) AND HOUR(time_a) = HOUR(?)";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setTimestamp(2, startTimestamp);
            stmt.setTimestamp(3, startTimestamp);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error checking start hour", e);
        }
        return false;
    }

    public void saveStartHour(Integer userId, Timestamp startTimestamp) {
        if (isStartHourExists(userId, startTimestamp)) {
            LoggerUtility.info("You already started work at this hour. Please pause or end your current session.");
            return;
        }
        String sql = "INSERT INTO time_processing (user_id, time_a) VALUES (?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setTimestamp(2, startTimestamp);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LoggerUtility.error("Error saving start hour", e);
        }
    }

    public boolean hasStartHour(Integer userId, Date date) {
        String sql = "SELECT COUNT(*) FROM time_processing WHERE user_id = ? AND DATE(time_a) = ? AND time_b IS NULL";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setDate(2, date);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error checking active work period", e);
        }
        return false;
    }

    public void savePauseTime(Integer userId, Timestamp pauseTimestamp) {
        String sql = "UPDATE time_processing SET time_b = ? WHERE user_id = ? AND time_b IS NULL ORDER BY time_a DESC LIMIT 1";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, pauseTimestamp);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LoggerUtility.error("Error saving pause time", e);
        }
    }

    public void finalizeWorkDay(int userId, Timestamp endTimestamp, Date specificDate) {
        savePauseTime(userId, endTimestamp);  // Ensure the last session is closed
        String sql = "{CALL calculate_work_interval(?, ?)}";
        try (Connection connection = getConnection();
             CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, userId);
            stmt.setDate(2, specificDate);
            stmt.execute();
        } catch (SQLException e) {
            LoggerUtility.error("Error finalizing work day", e);
        }
    }
}