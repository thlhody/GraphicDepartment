package cottontex.graphdep.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import cottontex.graphdep.constants.DatabaseConfig;
import cottontex.graphdep.utils.LoggerUtility;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {

    private static HikariDataSource dataSource;
    private static boolean isInitialized = false;

    public static synchronized void initialize() {
        if (isInitialized) {
            return;
        }

        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(DatabaseConfig.URL);
            config.setUsername(DatabaseConfig.USERNAME);
            config.setPassword(DatabaseConfig.PASSWORD);
            config.setMaximumPoolSize(30);
            config.setMinimumIdle(5);
            config.setIdleTimeout(3000);
            config.setMaxLifetime(1800000);
            config.setConnectionTimeout(3000);

            // Suppress HikariCP's own logging
            config.setInitializationFailTimeout(-1);
            config.setLeakDetectionThreshold(0);

            dataSource = new HikariDataSource(config);
            isInitialized = true;
            LoggerUtility.info("HikariCP connection pool initialized!");
        } catch (Exception e) {
            LoggerUtility.info("Failed to initialize HikariCP. Application will use local storage.");
        }
    }

    public static Connection getConnection() {
        if (!isInitialized) {
            initialize();
        }
        if (dataSource != null && !dataSource.isClosed()) {
            try {
                return dataSource.getConnection();
            } catch (SQLException e) {
                LoggerUtility.info("Expected database connection failure. Using local storage.");
                return null;
            }
        }
        return null;
    }

    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            LoggerUtility.info("HikariCP connection pool closed!");
        }
    }

    public static boolean isInitialized() {
        return isInitialized;
    }
}