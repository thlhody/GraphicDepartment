package cottontex.graphdep.database.handlers;

import cottontex.graphdep.constants.SQLQueries;
import cottontex.graphdep.database.BaseDatabase;
import cottontex.graphdep.database.interfaces.IUserLogin;
import cottontex.graphdep.utils.LoggerUtility;

import java.sql.*;

public class UserLogin extends BaseDatabase implements IUserLogin {

    public String authenticateUser(String username, String password) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = getPreparedStatement(conn, SQLQueries.AUTHENTICATE_USER)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error validating user", e);
        }
        return null;
    }

    public Integer getUserID(String username) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = getPreparedStatement(conn, SQLQueries.GET_USER_ID)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error retrieving user ID", e);
        }
        return null;
    }

    @Override
    public Integer getEmployeeId(String username) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = getPreparedStatement(conn, SQLQueries.GET_EMPLOYEE_ID)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("employee_id");
                }
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error retrieving employee ID for user: " + username, e);
        }
        return null;
    }

    @Override
    public String getName(Integer userId) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = getPreparedStatement(conn, SQLQueries.GET_NAME)) {
            stmt.setInt(1, userId);  // Set the user_id as an integer
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error retrieving name", e);
        }
        return null;
    }

}