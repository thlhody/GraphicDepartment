package cottontex.graphdep.database.queries.user;

import cottontex.graphdep.database.BaseDatabase;
import cottontex.graphdep.database.DatabaseConnection;
import cottontex.graphdep.constants.SQLQueries;
import cottontex.graphdep.models.UserStatus;
import cottontex.graphdep.models.WorkHourEntry;
import cottontex.graphdep.utils.DateTimeUtils;
import cottontex.graphdep.utils.LoggerUtility;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserTimeTableHandler extends BaseDatabase {

    public List<UserStatus> getMostRecentUserStatuses() {
        List<UserStatus> userStatuses = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = getPreparedStatement(conn, SQLQueries.GET_MOST_RECENT_USER_STATUSES);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String startTime = rs.getString("start_time");
                String endTime = rs.getString("end_time");

                UserStatus status = new UserStatus(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        DateTimeUtils.formatTimeForDisplay(startTime),
                        DateTimeUtils.formatTimeForDisplay(endTime)
                );
                userStatuses.add(status);
                LoggerUtility.debug("Added user status: " + status);
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error retrieving most recent user statuses: " + e.getMessage());
        }
        LoggerUtility.debug("Total unique user statuses retrieved: " + userStatuses.size());
        return userStatuses;
    }

    public List<WorkHourEntry> getUserMonthlyWorkHours(Integer userId) {
        List<WorkHourEntry> workHours = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(SQLQueries.GET_MONTHLY_WORK_HOURS_USER)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                WorkHourEntry entry = new WorkHourEntry(
                        rs.getDate("work_date").toLocalDate(),
                        rs.getTimestamp("first_start_time").toLocalDateTime(),
                        rs.getInt("breaks"),
                        rs.getTime("breaks_time").toLocalTime(),
                        rs.getTimestamp("end_time").toLocalDateTime(),
                        rs.getTime("total_worked_time").toLocalTime()
                );
                workHours.add(entry);
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error fetching user monthly work hours", e);
        }

        return workHours;
    }
}