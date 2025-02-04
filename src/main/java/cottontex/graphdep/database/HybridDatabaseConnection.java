package cottontex.graphdep.database;

import cottontex.graphdep.utils.LoggerUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class HybridDatabaseConnection {
    private static final String JSON_DIRECTORY = "local_db/";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        new File(JSON_DIRECTORY).mkdirs();
    }

    public Connection getConnection() throws SQLException {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                return conn;
            }
        } catch (SQLException e) {
            LoggerUtility.warn("Unable to connect to remote database. Switching to local JSON storage.");
        }
        return null; // Indicate that we're using local storage
    }

    public PreparedStatement getPreparedStatement(Connection conn, String sql) throws SQLException {
        if (conn != null) {
            return conn.prepareStatement(sql);
        }
        throw new SQLException("No active connection available");
    }

    public <T> List<T> readFromJson(String fileName, TypeReference<List<T>> typeReference) {
        File file = new File(JSON_DIRECTORY + fileName);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(file, typeReference);
        } catch (IOException e) {
            LoggerUtility.error("Error reading from JSON file: " + fileName, e);
            return new ArrayList<>();
        }
    }

    public <T> void writeToJson(String fileName, List<T> data) {
        try {
            objectMapper.writeValue(new File(JSON_DIRECTORY + fileName), data);
        } catch (IOException e) {
            LoggerUtility.error("Error writing to JSON file: " + fileName, e);
        }
    }

    public void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            LoggerUtility.error("Error closing database resources: " + e.getMessage());
        }
    }
}