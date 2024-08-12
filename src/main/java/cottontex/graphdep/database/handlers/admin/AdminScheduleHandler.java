package cottontex.graphdep.database.handlers.admin;

import cottontex.graphdep.database.BaseDatabase;
import cottontex.graphdep.constants.SQLQueries;
import cottontex.graphdep.database.interfaces.admin.IAdminScheduleHandler;
import cottontex.graphdep.models.HolidaySaveResult;
import cottontex.graphdep.utils.LoggerUtility;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;


public class AdminScheduleHandler extends BaseDatabase implements IAdminScheduleHandler {

    public Map<String, Map<Integer, String>> getMonthlyWorkData(int year, int month) {
        Map<String, Map<Integer, String>> result = new LinkedHashMap<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = getPreparedStatement(conn, SQLQueries.GET_MONTHLY_WORK_DATA);
            pstmt.setInt(1, year);
            pstmt.setInt(2, month);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                int dayNumber = rs.getInt("day_number");
                String dailyTotal = rs.getString("daily_total");
                String timeOffType = rs.getString("time_off_type");
                int dayOfWeek = rs.getInt("day_of_week");

                if (timeOffType != null && !timeOffType.isEmpty()) {
                    dailyTotal = timeOffType;
                }

                if (dayOfWeek != 1 && dayOfWeek != 7 || !dailyTotal.equals("00:00")) {
                    result.computeIfAbsent(name, k -> new HashMap<>()).put(dayNumber, dailyTotal);
                }
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error fetching monthly work data", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return result;
    }


    public HolidaySaveResult saveNationalHoliday(LocalDate date) {
        if (date == null) {
            return new HolidaySaveResult(false, null, "Invalid date provided.", false);
        }

        if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return new HolidaySaveResult(false, null, "Cannot add national holiday on weekends. Please choose a weekday.", false);
        }

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            int affectedRows = insertOrUpdateHolidayForUsers(conn, date);
            deleteDuplicates(conn);  // Add this line
            conn.commit();

            if (affectedRows > 0) {
                return new HolidaySaveResult(true, date, "National holiday added/updated for " + affectedRows + " users.", false);
            } else {
                return new HolidaySaveResult(false, date, "No changes were made. All users might already have worked hours for this date.", true);
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error saving national holiday for date: " + date, e);
            return new HolidaySaveResult(false, null, "Error occurred while saving national holiday: " + e.getMessage(), false);
        }
    }

    private void deleteDuplicates(Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SQLQueries.DELETE_DUPLICATES)) {
            stmt.executeUpdate();
        }
    }

    private int insertOrUpdateHolidayForUsers(Connection conn, LocalDate date) throws SQLException {
        List<Integer> userIds = getNonAdminUserIds(conn);
        int affectedRows = 0;

        try (PreparedStatement stmt = conn.prepareStatement(SQLQueries.INSERT_OR_UPDATE_HOLIDAY)) {
            Timestamp startTime = Timestamp.valueOf(date.atStartOfDay());
            Timestamp endTime = Timestamp.valueOf(date.atTime(23, 59, 59));

            for (int userId : userIds) {
                stmt.setInt(1, userId);
                stmt.setTimestamp(2, startTime);
                stmt.setTimestamp(3, endTime);
                int result = stmt.executeUpdate();
                if (result > 0) {
                    affectedRows++;
                }
            }
        }

        return affectedRows;
    }

    private List<Integer> getNonAdminUserIds(Connection conn) throws SQLException {
        List<Integer> userIds = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SQLQueries.GET_NON_ADMIN_USERS);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                userIds.add(rs.getInt("user_id"));
            }
        }
        return userIds;
    }
}

