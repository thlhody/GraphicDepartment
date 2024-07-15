package cottontex.graphdep;

import java.sql.*;

public class DatabaseConnection {

    private Connection connection;

    public DatabaseConnection() {
        prepareConnection();
    }

    private void prepareConnection() {
        String url = "jdbc:mysql://localhost:3306/grafic_dep_database";
        String username = "rootTesting";
        String password = "rootTesting";
        try {
            connection = DriverManager.getConnection(url, username, password);
            LoggerUtility.infoTest("Connected to database!");
        } catch (SQLException e) {
            LoggerUtility.errorInfo(e.getMessage());
        }
    }

    public void closeConnection() {
        try {
            connection.close();
            LoggerUtility.infoTest("Database closed!");
        } catch (SQLException e) {
            LoggerUtility.errorInfo(e.getMessage());
        }
    }

    public String authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
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

    public Connection getConnection() {
        return connection;
    }
}

