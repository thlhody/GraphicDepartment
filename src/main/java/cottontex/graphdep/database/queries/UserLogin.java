package cottontex.graphdep.database.queries;

import cottontex.graphdep.constants.SQLQueries;
import cottontex.graphdep.database.BaseDatabase;
import cottontex.graphdep.utils.LoggerUtility;

import java.sql.*;

public class UserLogin extends BaseDatabase {

    public String authenticateUser(String username, String password) {
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(SQLQueries.AUTHENTICATE_USER)) {
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
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(SQLQueries.GET_USER_ID)) {
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

}