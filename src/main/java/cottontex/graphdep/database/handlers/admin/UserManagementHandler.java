package cottontex.graphdep.database.handlers.admin;

import cottontex.graphdep.database.BaseDatabase;
import cottontex.graphdep.constants.SQLQueries;
import cottontex.graphdep.database.handlers.AddUserResult;
import cottontex.graphdep.database.interfaces.admin.IUserManagementHandler;
import cottontex.graphdep.utils.LoggerUtility;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserManagementHandler extends BaseDatabase implements IUserManagementHandler {

    @Override
    public boolean isUsernameTaken(String username) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = getPreparedStatement(conn, SQLQueries.CHECK_USERNAME)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error checking username availability", e);
        }
        return false;
    }

    @Override
    public boolean isEmployeeIdTaken(int employeeId) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = getPreparedStatement(conn, SQLQueries.CHECK_EMPLOYEE_ID)) {
            stmt.setInt(1, employeeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error checking employee ID availability", e);
        }
        return false;
    }

    @Override
    public AddUserResult addUser(String name, String username, String password, Integer employeeId, String role) {
        if (isUsernameTaken(username)) {
            LoggerUtility.warn("Attempt to add user with existing username: " + username);
            return AddUserResult.USERNAME_TAKEN;
        }

        if (isEmployeeIdTaken(employeeId)) {
            LoggerUtility.warn("Attempt to add user with existing employee ID: " + employeeId);
            return AddUserResult.EMPLOYEE_ID_TAKEN;
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = getPreparedStatement(conn, SQLQueries.ADD_USER)) {

            stmt.setString(1, name);
            stmt.setString(2, username);
            stmt.setString(3, password);  // Note: In a real application, you should hash the password
            stmt.setString(4, role);
            stmt.setInt(5, employeeId);
            stmt.executeUpdate();
            LoggerUtility.info("Successfully added new user: " + username);
            return AddUserResult.SUCCESS;
        } catch (SQLException e) {
            LoggerUtility.error("Error adding new user", e);
            return AddUserResult.OTHER_ERROR;
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

    public boolean changePassword(String username, String currentPassword, String newPassword) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = getPreparedStatement(conn, SQLQueries.CHANGE_PASSWORD)) {

            stmt.setString(1, newPassword);
            stmt.setString(2, username);
            stmt.setString(3, currentPassword);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                LoggerUtility.info("Password change failed for username: " + username);
                return false;
            }
            return true;
        } catch (SQLException e) {
            LoggerUtility.error("Error changing password for username: " + username, e);
            return false;
        }
    }
}