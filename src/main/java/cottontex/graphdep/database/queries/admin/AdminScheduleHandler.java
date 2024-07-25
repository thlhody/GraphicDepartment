package cottontex.graphdep.database.queries.admin;

import cottontex.graphdep.database.BaseDatabase;
import cottontex.graphdep.constants.SQLQueries;
import cottontex.graphdep.utils.LoggerUtility;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;


public class AdminScheduleHandler extends BaseDatabase {

    public Map<String, Map<Integer, String>> getMonthlyWorkData(int year, int month) {
        Map<String, Map<Integer, String>> result = new LinkedHashMap<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQLQueries.GET_MONTHLY_WORK_DATA)) {

            pstmt.setInt(1, year);
            pstmt.setInt(2, month);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    int dayNumber = rs.getInt("day_number");
                    String dailyTotal = rs.getString("daily_total");
                    String timeOffType = rs.getString("time_off_type");
                    int dayOfWeek = rs.getInt("day_of_week");

                    if (timeOffType != null && !timeOffType.isEmpty()) {
                        dailyTotal = timeOffType;
                    }

                    // Only add non-weekend days or days with work/time off
                    if (dayOfWeek != 1 && dayOfWeek != 7 || !dailyTotal.equals("00:00")) {
                        result.computeIfAbsent(name, k -> new HashMap<>()).put(dayNumber, dailyTotal);
                    }
                }
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error fetching monthly work data", e);
        }
        return result;
    }

    public boolean saveNationalHoliday(LocalDate date) {

        String checkExistingSql = "SELECT COUNT(*) FROM work_interval WHERE DATE(first_start_time) = ? AND time_off_type = 'NH'";
        String selectUsersSql = "SELECT user_id FROM users WHERE role != 'admin'";
        String insertHolidaySql = "INSERT INTO work_interval (user_id, first_start_time, end_time, total_worked_time, time_off_type) " +
                "SELECT ?, ?, ?, '00:00:00', 'NH' " +
                "WHERE NOT EXISTS (SELECT 1 FROM work_interval WHERE user_id = ? AND DATE(first_start_time) = ? AND time_off_type = 'NH')";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            // Check if holiday already exists for this date
            try (PreparedStatement checkStmt = conn.prepareStatement(SQLQueries.CHECK_EXISTING_SQL)) {
                checkStmt.setDate(1, java.sql.Date.valueOf(date));
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    return false; // Holiday already exists, no need to insert
                }
            }

            try (PreparedStatement selectStmt = conn.prepareStatement(SQLQueries.SELECT_USERS_SQL);
                 PreparedStatement insertStmt = conn.prepareStatement(SQLQueries.INSERT_HOLIDAY_SQL)) {

                ResultSet rs = selectStmt.executeQuery();

                Timestamp startTime = Timestamp.valueOf(date.atStartOfDay());
                Timestamp endTime = Timestamp.valueOf(date.atTime(23, 59, 59));

                int count = 0;
                while (rs.next()) {
                    int userId = rs.getInt("user_id");
                    insertStmt.setInt(1, userId);
                    insertStmt.setTimestamp(2, startTime);
                    insertStmt.setTimestamp(3, endTime);
                    insertStmt.setInt(4, userId);
                    insertStmt.setDate(5, java.sql.Date.valueOf(date));
                    insertStmt.addBatch();
                    count++;
                }
                System.out.println("Prepared batch insert for " + count + " users");

                int[] results = insertStmt.executeBatch();
                conn.commit();

                int insertedCount = 0;
                for (int result : results) {
                    if (result > 0) insertedCount++;
                }
                System.out.println("Inserted " + insertedCount + " new holiday entries for date: " + date);
                return insertedCount > 0;
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Error occurred, rolling back transaction");
                e.printStackTrace();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error saving national holiday for date: " + date, e);
            e.printStackTrace();
            return false;
        }
    }
}