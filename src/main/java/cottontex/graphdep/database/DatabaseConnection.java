package cottontex.graphdep.database;

import cottontex.graphdep.loggerUtility.LoggerUtility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;
    private String url = "jdbc:mysql://localhost:3306/grafic_dep_database";
    private String username = "rootTesting";
    private String password = "rootTesting";

    public DatabaseConnection() {
        try {
            this.connection = DriverManager.getConnection(url, username, password);
            LoggerUtility.infoTest("Connected to database!");
        } catch (SQLException e) {
            LoggerUtility.errorInfo("Failed to create the database connection: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, username, password);
                LoggerUtility.infoTest("Reconnected to database!");
            }
        } catch (SQLException e) {
            LoggerUtility.errorInfo("Failed to reconnect to the database: " + e.getMessage());
        }
        return connection;
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                LoggerUtility.infoTest("Database connection closed!");
            }
        } catch (SQLException e) {
            LoggerUtility.errorInfo("Error closing the database connection: " + e.getMessage());
        }
    }
}
