package cottontex.graphdep.database.queries;

import cottontex.graphdep.database.BaseDatabase;
import cottontex.graphdep.utils.LoggerUtility;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserManagementHandler extends BaseDatabase {

    public boolean addUser(String name, String username, String password) {
        String sql = "INSERT INTO users (name, username, password, role) VALUES (?, ?, ?, 'USER')";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, username);
            stmt.setString(3, password);  // Note: In a real application, you should hash the password
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerUtility.error("Error adding new user", e);
            return false;
        }
    }
    public List<String> getAllUsernames() {
        List<String> usernames = new ArrayList<>();
        String sql = "SELECT username FROM users WHERE role != 'ADMIN'";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                usernames.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error fetching usernames", e);
        }
        return usernames;
    }

    public boolean resetPassword(String username, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, username);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerUtility.error("Error resetting password", e);
            return false;
        }
    }

    public boolean deleteUser(String username) {
        String sql = "DELETE FROM users WHERE username = ? AND role != 'ADMIN'";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerUtility.error("Error deleting user", e);
            return false;
        }
    }
    public boolean changePassword(Integer userID, String currentPassword, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE user_id = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPassword);
            stmt.setInt(2, userID);
            stmt.setString(3, currentPassword);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                LoggerUtility.info("Password change failed for user ID: " + userID);
                return false;
            }

            return true;
        } catch (SQLException e) {
            LoggerUtility.error("Error changing password for user ID: " + userID, e);
            return false;
        }
    }
}