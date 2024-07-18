package cottontex.graphdep.database.queries;

import cottontex.graphdep.database.DatabaseConnection;
import cottontex.graphdep.loggerUtility.LoggerUtility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserLogin {
    private DatabaseConnection dbConnection;

    public UserLogin() {
        dbConnection = DatabaseConnection.getInstance();
    }

    public String authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("role");
                }
            }
        } catch (SQLException e) {
            LoggerUtility.errorInfo("Error validating user: " + e.getMessage());
        }
        return null;
    }

    public Integer getUserID(String username) {
        String query = "SELECT user_id FROM users WHERE username = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            LoggerUtility.errorInfo("Error retrieving user ID: " + e.getMessage());
        }
        return null;
    }
}
