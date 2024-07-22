package cottontex.graphdep.database.queries.admin;

import cottontex.graphdep.database.BaseDatabase;
import cottontex.graphdep.constants.SQLQueries;
import cottontex.graphdep.utils.LoggerUtility;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserManagementHandler extends BaseDatabase {

    public boolean addUser(String name, String username, String password) {
        try (Connection conn = getConnection(); PreparedStatement stmt = getPreparedStatement(conn, SQLQueries.ADD_USER)) {

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
        try (Connection conn = getConnection(); PreparedStatement stmt = getPreparedStatement(conn, SQLQueries.GET_ALL_USERNAMES);
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
        try (Connection conn = getConnection(); PreparedStatement stmt = getPreparedStatement(conn, SQLQueries.RESET_PASSWORD)) {

            stmt.setString(1, newPassword);
            stmt.setString(2, username);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerUtility.error("Error resetting password", e);
            return false;
        }
    }

    public boolean deleteUser(String username) {
        try (Connection conn = getConnection(); PreparedStatement stmt = getPreparedStatement(conn, SQLQueries.DELETE_USER)) {

            stmt.setString(1, username);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerUtility.error("Error deleting user", e);
            return false;
        }
    }
    public boolean changePassword(Integer userID, String currentPassword, String newPassword) {
        try (Connection conn = getConnection(); PreparedStatement stmt = getPreparedStatement(conn, SQLQueries.CHANGE_PASSWORD)) {

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