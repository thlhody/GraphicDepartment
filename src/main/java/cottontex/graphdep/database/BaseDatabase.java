package cottontex.graphdep.database;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cottontex.graphdep.constants.JsonPaths;
import cottontex.graphdep.utils.LoggerUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.nio.file.Files;
import java.sql.*;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.io.File;
import java.io.IOException;

public abstract class BaseDatabase {
    private static final Map<String, PreparedStatement> preparedStatementCache = new ConcurrentHashMap<>();
    private static final Map<String, CallableStatement> callableStatementCache = new ConcurrentHashMap<>();
    protected static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    private static boolean isUsingLocalStorage = false;

    static {
        File baseDir = new File(JsonPaths.BASE_PATH);
        if (!baseDir.exists()) {
            boolean created = baseDir.mkdirs();
            if (created) {
                LoggerUtility.info("Created base directory: " + baseDir.getAbsolutePath());
            } else {
                LoggerUtility.error("Failed to create base directory: " + baseDir.getAbsolutePath());
            }
        }
    }

    protected Connection getConnection()  {
        if (isUsingLocalStorage()) {
            return null;
        }
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            setUsingLocalStorage(true);
        }
        return conn;
    }

    protected PreparedStatement getPreparedStatement(Connection conn, String sql) throws SQLException {
        if (isUsingLocalStorage) {
            throw new SQLException("Using local storage, SQL operations are not available");
        }
        if (conn == null) {
            throw new SQLException("No active connection available");
        }
        String cacheKey = conn.toString() + ":prepared:" + sql;
        PreparedStatement stmt = preparedStatementCache.get(cacheKey);
        if (stmt == null || stmt.isClosed()) {
            stmt = conn.prepareStatement(sql);
            preparedStatementCache.put(cacheKey, stmt);
        }
        return stmt;
    }

    protected CallableStatement getCallableStatement(Connection conn, String sql) throws SQLException {
        if (isUsingLocalStorage) {
            throw new SQLException("Using local storage, SQL operations are not available");
        }
        if (conn == null) {
            throw new SQLException("No active connection available");
        }
        String cacheKey = conn.toString() + ":callable:" + sql;
        CallableStatement stmt = callableStatementCache.get(cacheKey);
        if (stmt == null || stmt.isClosed()) {
            stmt = conn.prepareCall(sql);
            callableStatementCache.put(cacheKey, stmt);
        }
        return stmt;
    }

    protected <T> List<T> readFromJson(String fileName, TypeReference<List<T>> typeReference) {
        File file = new File(JsonPaths.BASE_PATH, fileName);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            if (content.trim().isEmpty()) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(content, typeReference);
        } catch (IOException e) {
            LoggerUtility.error("Error reading from JSON file: " + file.getAbsolutePath(), e);
            return new ArrayList<>();
        }
    }

    protected <T> void writeToJson(String fileName, List<T> data) {
        try {
            File file = new File(JsonPaths.BASE_PATH + fileName);
            LoggerUtility.info("Writing to JSON file: " + file.getAbsolutePath());
            LoggerUtility.info("Data size: " + data.size());
            objectMapper.writeValue(file, data);
            LoggerUtility.info("Successfully wrote data to JSON file");
        } catch (IOException e) {
            LoggerUtility.error("Error writing to JSON file: " + JsonPaths.BASE_PATH + fileName, e);
        }
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

    public static boolean isUsingLocalStorage() {
        return isUsingLocalStorage;
    }

    public static void setUsingLocalStorage(boolean usingLocalStorage) {
        isUsingLocalStorage = usingLocalStorage;
    }
}