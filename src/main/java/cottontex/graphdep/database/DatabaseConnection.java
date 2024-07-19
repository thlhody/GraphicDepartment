package cottontex.graphdep.database;

import cottontex.graphdep.utils.LoggerUtility;
import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/grafic_dep_database";
    private static final String USERNAME = "rootTesting";
    private static final String PASSWORD = "rootTesting";

    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                LoggerUtility.info("Connected to database!");
            }
        } catch (SQLException e) {
            LoggerUtility.error("Failed to connect to the database: " + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                LoggerUtility.info("Database connection closed!");
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error closing the database connection: " + e.getMessage());
        }
    }
}