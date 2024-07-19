package cottontex.graphdep.database;

import cottontex.graphdep.utils.LoggerUtility;
import java.sql.*;

public abstract class BaseDatabase {
    protected Connection getConnection() {
        return DatabaseConnection.getConnection();
    }

    protected void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            LoggerUtility.error("Error closing database resources: " + e.getMessage());
        }
    }
}