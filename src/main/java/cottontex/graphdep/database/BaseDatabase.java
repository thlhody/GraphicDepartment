package cottontex.graphdep.database;

import cottontex.graphdep.utils.LoggerUtility;

import java.sql.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseDatabase {
    private static final Map<String, PreparedStatement> preparedStatementCache = new ConcurrentHashMap<>();
    private static final Map<String, CallableStatement> callableStatementCache = new ConcurrentHashMap<>();

    protected Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    protected PreparedStatement getPreparedStatement(Connection conn, String sql) throws SQLException {
        String cacheKey = conn.toString() + ":prepared:" + sql;
        PreparedStatement stmt = preparedStatementCache.get(cacheKey);
        if (stmt == null || stmt.isClosed()) {
            stmt = conn.prepareStatement(sql);
            preparedStatementCache.put(cacheKey, stmt);
        }
        return stmt;
    }

    protected CallableStatement getCallableStatement(Connection conn, String sql) throws SQLException {
        String cacheKey = conn.toString() + ":callable:" + sql;
        CallableStatement stmt = callableStatementCache.get(cacheKey);
        if (stmt == null || stmt.isClosed()) {
            stmt = conn.prepareCall(sql);
            callableStatementCache.put(cacheKey, stmt);
        }
        return stmt;
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

    public static void clearStatementCaches() {
        clearCache(preparedStatementCache);
        clearCache(callableStatementCache);
    }

    private static void clearCache(Map<String, ? extends Statement> cache) {
        for (Statement stmt : cache.values()) {
            try {
                stmt.close();
            } catch (SQLException e) {
                LoggerUtility.error("Error closing cached statement: " + e.getMessage());
            }
        }
        cache.clear();
    }
}