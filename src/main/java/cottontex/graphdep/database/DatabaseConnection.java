package cottontex.graphdep.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import cottontex.graphdep.constants.DatabaseConfig;
import cottontex.graphdep.utils.LoggerUtility;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
    private static HikariDataSource dataSource;

    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(DatabaseConfig.URL);
            config.setUsername(DatabaseConfig.USERNAME);
            config.setPassword(DatabaseConfig.PASSWORD);
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(5);
            config.setIdleTimeout(300000);
            config.setMaxLifetime(1800000);
            config.setConnectionTimeout(30000);

            dataSource = new HikariDataSource(config);
            LoggerUtility.info("HikariCP connection pool initialized!");
        } catch (Exception e) {
            LoggerUtility.error("*****ERROR****** Failed to initialize HikariCP: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            LoggerUtility.info("HikariCP connection pool closed!");
        }
    }
}