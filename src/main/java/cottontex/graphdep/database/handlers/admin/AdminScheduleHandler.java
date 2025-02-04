package cottontex.graphdep.database.handlers.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import cottontex.graphdep.constants.JsonPaths;
import cottontex.graphdep.constants.SQLQueries;
import cottontex.graphdep.constants.TimeOffCodes;
import cottontex.graphdep.database.BaseDatabase;
import cottontex.graphdep.database.interfaces.admin.IAdminScheduleHandler;
import cottontex.graphdep.models.HolidaySaveResult;
import cottontex.graphdep.models.UserOffline;
import cottontex.graphdep.models.WorkIntervalOffline;
import cottontex.graphdep.utils.LoggerUtility;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class AdminScheduleHandler extends BaseDatabase implements IAdminScheduleHandler {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public Map<String, Map<Integer, String>> getMonthlyWorkData(int year, int month) {
        Map<String, Map<Integer, String>> result = new LinkedHashMap<>();

        if (!isUsingLocalStorage()) {
            fetchMonthlyWorkDataFromDatabase(result, year, month);
        }

        if (result.isEmpty()) {
            LoggerUtility.info("Using JSON data for monthly work data");
            processJsonMonthlyWorkData(result, year, month);
        }

        return result;
    }

    private void fetchMonthlyWorkDataFromDatabase(Map<String, Map<Integer, String>> result, int year, int month) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = getPreparedStatement(conn, SQLQueries.GET_MONTHLY_WORK_DATA)) {

            pstmt.setInt(1, year);
            pstmt.setInt(2, month);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    processMonthlyWorkDataRow(result, rs);
                }
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error fetching monthly work data from database", e);
        }
    }

    private void processMonthlyWorkDataRow(Map<String, Map<Integer, String>> result, ResultSet rs) throws SQLException {
        String name = rs.getString("name");
        int dayNumber = rs.getInt("day_number");
        String dailyTotal = rs.getString("daily_total");
        String timeOffType = rs.getString("time_off_type");
        int dayOfWeek = rs.getInt("day_of_week");

        if (timeOffType != null && !timeOffType.isEmpty()) {
            dailyTotal = timeOffType;
        }

        if (isWorkday(DayOfWeek.of(dayOfWeek)) || !dailyTotal.equals("00:00")) {
            result.computeIfAbsent(name, k -> new HashMap<>()).put(dayNumber, dailyTotal);
        }
    }

    private void processJsonMonthlyWorkData(Map<String, Map<Integer, String>> result, int year, int month) {
        List<WorkIntervalOffline> workIntervals = getWorkIntervals();
        LoggerUtility.info("Processing " + workIntervals.size() + " work intervals for " + year + "-" + month);

        for (WorkIntervalOffline interval : workIntervals) {
            if (isRelevantWorkInterval(interval, year, month)) {
                processWorkInterval(result, interval);
            }
        }
        LoggerUtility.info("Processed data for " + result.size() + " users");
    }

    private boolean isRelevantWorkInterval(WorkIntervalOffline interval, int year, int month) {
        return interval.getWorkDate() != null
                && interval.getWorkDate().getYear() == year
                && interval.getWorkDate().getMonthValue() == month;
    }

    private void processWorkInterval(Map<String, Map<Integer, String>> result, WorkIntervalOffline interval) {
        String name = String.valueOf(interval.getUserId());
        int dayNumber = interval.getWorkDate().getDayOfMonth();
        String dailyTotal = formatTotalWorkedTime(interval);
        String timeOffType = interval.getTimeOffType();

        if (timeOffType != null && !timeOffType.isEmpty()) {
            dailyTotal = timeOffType;
        }

        DayOfWeek dayOfWeek = interval.getWorkDate().getDayOfWeek();
        if (isWorkday(dayOfWeek) || !dailyTotal.equals("00:00")) {
            result.computeIfAbsent(name, k -> new HashMap<>()).put(dayNumber, dailyTotal);
            LoggerUtility.info("Added entry: User " + name + ", Day " + dayNumber + ", Value: " + dailyTotal);
        }
    }

    private boolean isWorkday(DayOfWeek dayOfWeek) {
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }

    private String formatTotalWorkedTime(WorkIntervalOffline interval) {
        if (interval.getTotalWorkedTime() != null) {
            return interval.getTotalWorkedTime().format(TIME_FORMATTER);
        } else if (interval.getTotalWorkedSeconds() > 0) {
            long hours = interval.getTotalWorkedSeconds() / 3600;
            long minutes = (interval.getTotalWorkedSeconds() % 3600) / 60;
            return String.format("%02d:%02d", hours, minutes);
        } else {
            return "00:00";
        }
    }

    @Override
    public HolidaySaveResult saveNationalHoliday(LocalDate date) {
        LoggerUtility.info("Attempting to save national holiday for date: " + date);

        if (!isValidHolidayDate(date)) {
            return createInvalidDateResult(date);
        }

        List<WorkIntervalOffline> workIntervals = getWorkIntervals();
        LoggerUtility.info("Current work intervals before update: " + workIntervals.size());

        boolean jsonUpdated = updateJsonWithHoliday(workIntervals, date);
        LoggerUtility.info("JSON update result: " + jsonUpdated);

        int affectedRows = 0;
        if (!jsonUpdated) {
            affectedRows = addNewHolidayEntriesForAllUsers(workIntervals, date);
            LoggerUtility.info("Added new holiday entries. Affected rows: " + affectedRows);
        }

        LoggerUtility.info("Work intervals after update: " + workIntervals.size());

        writeToJson(JsonPaths.WORK_INTERVAL_JSON, workIntervals);

        if (!isUsingLocalStorage()) {
            syncJsonToDatabase();
        }

        return createHolidaySaveResult(affectedRows, false, jsonUpdated, date);
    }

    private boolean isValidHolidayDate(LocalDate date) {
        return date != null && isWorkday(date.getDayOfWeek());
    }

    private HolidaySaveResult createInvalidDateResult(LocalDate date) {
        String message = (date == null) ? "Invalid date provided." :
                "Cannot add national holiday on weekends. Please choose a weekday.";
        return new HolidaySaveResult(false, date, message, false);
    }

    private boolean updateHolidayInDatabase(LocalDate date) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            insertOrUpdateHolidayForUsers(conn, date);
            deleteDuplicates(conn);
            conn.commit();
            LoggerUtility.info("National holiday saved to database for date: " + date);
            return true;
        } catch (SQLException e) {
            LoggerUtility.error("Error saving national holiday to database for date: " + date, e);
            return false;
        }
    }

    private boolean updateJsonWithHoliday(List<WorkIntervalOffline> workIntervals, LocalDate date) {
        boolean updated = false;
        for (WorkIntervalOffline interval : workIntervals) {
            if (interval.getWorkDate() != null && interval.getWorkDate().equals(date)) {
                interval.setTimeOffType(TimeOffCodes.NATIONAL_HOLIDAY_CODE);
                interval.setTotalWorkedTime(LocalTime.of(0, 0));
                updated = true;
                LoggerUtility.info("Updated existing entry for date: " + date);
            }
        }
        return updated;
    }

    private int addNewHolidayEntriesForAllUsers(List<WorkIntervalOffline> workIntervals, LocalDate date) {
        List<Integer> userIds = getAllUserIds();
        int addedEntries = 0;
        for (Integer userId : userIds) {
            WorkIntervalOffline newInterval = createHolidayInterval(userId, date);
            workIntervals.add(newInterval);
            addedEntries++;
        }
        LoggerUtility.info("Added " + addedEntries + " new holiday entries for date: " + date);
        return addedEntries;
    }

    private WorkIntervalOffline createHolidayInterval(Integer userId, LocalDate date) {
        WorkIntervalOffline newInterval = new WorkIntervalOffline();
        newInterval.setUserId(userId);
        newInterval.setWorkDate(date);
        newInterval.setTimeOffType(TimeOffCodes.NATIONAL_HOLIDAY_CODE);
        newInterval.setTotalWorkedTime(LocalTime.of(0, 0));
        newInterval.setFirstStartTime(date.atStartOfDay());
        newInterval.setEndTime(date.atTime(23, 59, 59));
        return newInterval;
    }

    private List<WorkIntervalOffline> getWorkIntervals() {
        return readFromJson(JsonPaths.WORK_INTERVAL_JSON, new TypeReference<List<WorkIntervalOffline>>() {});
    }

    private void insertOrUpdateHolidayForUsers(Connection conn, LocalDate date) throws SQLException {
        List<Integer> userIds = getNonAdminUserIds(conn);
        try (PreparedStatement stmt = conn.prepareStatement(SQLQueries.INSERT_OR_UPDATE_HOLIDAY)) {
            Timestamp startTime = Timestamp.valueOf(date.atStartOfDay());
            Timestamp endTime = Timestamp.valueOf(date.atTime(23, 59, 59));

            for (int userId : userIds) {
                stmt.setInt(1, userId);
                stmt.setTimestamp(2, startTime);
                stmt.setTimestamp(3, endTime);
                stmt.executeUpdate();
            }
        }
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

    private void deleteDuplicates(Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SQLQueries.DELETE_DUPLICATES)) {
            stmt.executeUpdate();
        }
    }

    public void syncJsonToDatabase() {
        if (isUsingLocalStorage()) {
            LoggerUtility.info("Using local storage. Skipping database sync.");
            return;
        }

        try (Connection conn = getConnection()) {
            List<WorkIntervalOffline> workIntervals = getWorkIntervals();
            conn.setAutoCommit(false);
            for (WorkIntervalOffline interval : workIntervals) {
                saveWorkIntervalToDatabase(conn, interval);
            }
            conn.commit();
            LoggerUtility.info("Successfully synced JSON data to database");
        } catch (SQLException e) {
            LoggerUtility.error("Error syncing JSON data to database", e);
        }
    }

    private void saveWorkIntervalToDatabase(Connection conn, WorkIntervalOffline interval) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SQLQueries.TIME_OFF_UPDATE)) {
            stmt.setInt(1, interval.getUserId());
            stmt.setTimestamp(2, Timestamp.valueOf(interval.getFirstStartTime()));
            stmt.setTimestamp(3, Timestamp.valueOf(interval.getEndTime()));
            stmt.setTime(4, Time.valueOf(interval.getTotalWorkedTime()));
            stmt.setString(5, interval.getTimeOffType());
            stmt.executeUpdate();
        }
    }

    private List<Integer> getAllUserIds() {
        List<Integer> userIds = new ArrayList<>();

        if (!isUsingLocalStorage()) {
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = getPreparedStatement(conn, "SELECT user_id FROM users WHERE role != 'ADMIN'");
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    userIds.add(rs.getInt("user_id"));
                }
            } catch (SQLException e) {
                LoggerUtility.error("Error fetching user IDs from database", e);
            }
        }

        if (userIds.isEmpty()) {
            List<UserOffline> users = readFromJson(JsonPaths.USERS_JSON, new TypeReference<List<UserOffline>>() {});
            userIds = users.stream()
                    .filter(user -> !"ADMIN".equals(user.getRole()))
                    .map(UserOffline::getUserId)
                    .collect(Collectors.toList());
        }

        if (userIds.isEmpty()) {
            LoggerUtility.warn("No user IDs found. Using default list.");
            userIds = Arrays.asList(1, 2, 3);
        }

        return userIds;
    }

    private HolidaySaveResult createHolidaySaveResult(int affectedRows, boolean databaseUpdated, boolean jsonUpdated, LocalDate date) {
        if (affectedRows > 0 || jsonUpdated) {
            String message = databaseUpdated ?
                    "National holiday added/updated in database and JSON for " + affectedRows + " users." :
                    "National holiday added/updated in JSON for " + affectedRows + " users. Will sync to database when available.";
            return new HolidaySaveResult(true, date, message, false);
        } else {
            return new HolidaySaveResult(false, date, "No changes were made. All users might already have worked hours for this date.", true);
        }
    }
}