package cottontex.graphdep.database.queries;

import cottontex.graphdep.database.BaseDatabase;
import cottontex.graphdep.utils.LoggerUtility;

import java.sql.*;

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
}