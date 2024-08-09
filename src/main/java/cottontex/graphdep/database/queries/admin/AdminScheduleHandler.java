package cottontex.graphdep.database.queries.admin;

import cottontex.graphdep.database.BaseDatabase;
import cottontex.graphdep.constants.SQLQueries;
import cottontex.graphdep.utils.LoggerUtility;

import java.sql.*;
import java.time.DayOfWeek;
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

    public static class HolidaySaveResult {
        public final boolean success;
        public final LocalDate savedDate;
        public final String message;

        public HolidaySaveResult(boolean success, LocalDate savedDate, String message) {
            this.success = success;
            this.savedDate = savedDate;
            this.message = message;
        }

        public boolean isSuccess() {
            return false;
        }

        public boolean isDuplicate() {
            return false;
        }
    }

    public HolidaySaveResult saveNationalHoliday(LocalDate date) {
        LocalDate actualDate = adjustToWorkingDay(date);

        if (isExistingTimeOff(actualDate)) {
            return new HolidaySaveResult(false, null, "Cannot add national holiday. CO or CM already exists for " + actualDate);
        }

        if (isExistingHoliday(actualDate)) {
            return new HolidaySaveResult(false, null, "National holiday already exists for " + actualDate);
        }

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            int insertedCount = insertHoliday(conn, actualDate);

            conn.commit();

            if (insertedCount > 0) {
                String message = date.equals(actualDate)
                        ? "National holiday added successfully for " + actualDate
                        : "National holiday adjusted and added for " + actualDate + " (next working day)";
                return new HolidaySaveResult(true, actualDate, message);
            } else {
                return new HolidaySaveResult(false, null, "No new holiday entries were inserted for " + actualDate);
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error saving national holiday for date: " + date, e);
            return new HolidaySaveResult(false, null, "Error occurred while saving national holiday: " + e.getMessage());
        }
    }

    private LocalDate adjustToWorkingDay(LocalDate date) {
        LocalDate adjustedDate = date;
        while (adjustedDate.getDayOfWeek() == DayOfWeek.SATURDAY || adjustedDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            adjustedDate = adjustedDate.plusDays(1);
        }
        return adjustedDate;
    }

    private boolean isExistingTimeOff(LocalDate date) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLQueries.CHECK_EXISTING_TIME_OFF_SQL)) {
            stmt.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            LoggerUtility.error(e.getMessage());
        }
        return false;
    }

    private boolean isExistingHoliday(LocalDate date) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLQueries.CHECK_EXISTING_SQL)) {
            stmt.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            LoggerUtility.error(e.getMessage());
        }
        return false;
    }

    private int insertHoliday(Connection conn, LocalDate date) {
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
            return Arrays.stream(results).sum();
        } catch (SQLException e) {
            LoggerUtility.error(e.getMessage());
        }
        return 0;
    }

}