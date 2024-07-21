package cottontex.graphdep.database.queries;

import cottontex.graphdep.database.BaseDatabase;
import cottontex.graphdep.utils.LoggerUtility;

import java.sql.*;
import java.util.*;


public class AdminScheduleHandler extends BaseDatabase {

    public Map<String, Map<Integer, String>> getMonthlyWorkData(int year, int month) {
        Map<String, Map<Integer, String>> result = new LinkedHashMap<>();
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(SQLQueries.GET_MONTHLY_WORK_DATA)) {
            pstmt.setInt(1, month);
            pstmt.setInt(2, year);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    int dayNumber = rs.getInt("day_number");
                    String dailyTotal = rs.getString("daily_total");
                    result.computeIfAbsent(name, k -> new HashMap<>()).put(dayNumber, dailyTotal);
                }
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error fetching monthly work data", e);
        }
        return result;
    }
}