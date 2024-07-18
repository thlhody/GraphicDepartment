package cottontex.graphdep.database.queries;

import cottontex.graphdep.database.DatabaseConnection;
import cottontex.graphdep.loggerUtility.LoggerUtility;

import java.sql.*;

public class ScheduleUserTable {
    private DatabaseConnection dbConnection;

    public ScheduleUserTable() {
        this.dbConnection = new DatabaseConnection();
    }

    public void updateWorkIntervalsStartHour(int userId, Timestamp updateTime) {
        // Implement any additional logic if necessary, for now it can be left empty
    }

    public boolean isStartHourExists(int userId, Timestamp startTimestamp) {
        String sql = "SELECT COUNT(*) FROM time_processing WHERE user_id = ? AND DATE(time_a) = DATE(?) AND HOUR(time_a) = HOUR(?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setTimestamp(2, startTimestamp);
            stmt.setTimestamp(3, startTimestamp);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LoggerUtility.errorInfo("Error checking start hour: " + e.getMessage());
        }
        return false;
    }


    public void saveStartHour(int userId, Timestamp startTimestamp) {
        // Check if the start hour already exists
        if (isStartHourExists(userId, startTimestamp)) {
            LoggerUtility.errorInfo("You already started work at this hour. Please pause or end your current session.");
            // You can display a message to the user indicating they have already started work
            return;
        }
        String sql = "INSERT INTO time_processing (user_id, time_a) VALUES (?, ?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setTimestamp(2, startTimestamp);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LoggerUtility.errorInfo("Error saving start hour: " + e.getMessage());
        }
    }

    public boolean hasStartHour(int userId, Date date) {
        String sql = "SELECT COUNT(*) FROM time_processing WHERE user_id = ? AND DATE(time_a) = ? AND time_b IS NULL";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setDate(2, date);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LoggerUtility.errorInfo("Error checking active work period: " + e.getMessage());
        }
        return false;
    }

    public void savePauseTime(int userId, Timestamp pauseTimestamp) {
        String sql = "UPDATE time_processing SET time_b = ? WHERE user_id = ? AND time_b IS NULL ORDER BY time_a DESC LIMIT 1";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, pauseTimestamp);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LoggerUtility.errorInfo("Error saving pause time: " + e.getMessage());
        }
    }

    public void finalizeWorkDay(int userId, Timestamp endTimestamp,Date specificDate) {
        savePauseTime(userId, endTimestamp);  // Ensure the last session is closed
        String sql = "{CALL calculate_work_interval(?, ?)}";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, userId);
            stmt.setDate(2,specificDate);
            stmt.execute();
        } catch (SQLException e) {
            LoggerUtility.errorInfo("Error saving final time: " + e.getMessage());
        }
    }
}

