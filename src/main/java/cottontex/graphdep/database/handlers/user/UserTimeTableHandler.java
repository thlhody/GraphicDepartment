package cottontex.graphdep.database.handlers.user;

import cottontex.graphdep.database.*;
import cottontex.graphdep.constants.*;
import cottontex.graphdep.database.interfaces.user.IUserTimeTableHandler;
import cottontex.graphdep.models.WorkHourEntry;
import cottontex.graphdep.utils.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserTimeTableHandler extends BaseDatabase implements IUserTimeTableHandler {

    public List<WorkHourEntry> getUserMonthlyWorkHours(Integer userId) {
        List<WorkHourEntry> workHours = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = getPreparedStatement(conn, SQLQueries.GET_MONTHLY_WORK_HOURS_USER);
            pstmt.setInt(1, userId);
            LoggerUtility.info("Executing query: " + SQLQueries.GET_MONTHLY_WORK_HOURS_USER + " with userId: " + userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String timeOffType = rs.getString("time_off_type");
                WorkHourEntry entry = new WorkHourEntry(
                        rs.getDate("work_date") != null ? rs.getDate("work_date").toLocalDate() : null,
                        timeOffType == null ? (rs.getTimestamp("first_start_time") != null ? rs.getTimestamp("first_start_time").toLocalDateTime() : null) : null,
                        timeOffType == null ? rs.getInt("breaks") : null,
                        timeOffType == null ? (rs.getTime("breaks_time") != null ? rs.getTime("breaks_time").toLocalTime() : null) : null,
                        timeOffType == null ? (rs.getTimestamp("end_time") != null ? rs.getTimestamp("end_time").toLocalDateTime() : null) : null,
                        timeOffType == null ? (rs.getTime("total_worked_time") != null ? rs.getTime("total_worked_time").toLocalTime() : null) : null,
                        timeOffType
                );
                workHours.add(entry);
                LoggerUtility.info("Added work hour entry: " + entry);
            }
            LoggerUtility.info("Retrieved " + workHours.size() + " work hour entries for user " + userId);
        } catch (SQLException e) {
            LoggerUtility.error("Error fetching user monthly work hours for userId: " + userId, e);
        } finally {
            closeResources(conn, pstmt, rs);
        }

        return workHours;
    }
}