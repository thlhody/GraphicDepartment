package cottontex.graphdep.database.queries.admin;

import cottontex.graphdep.constants.SQLQueries;
import cottontex.graphdep.database.BaseDatabase;
import cottontex.graphdep.models.UserStatus;
import cottontex.graphdep.utils.LoggerUtility;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminTimeTableHandler extends BaseDatabase {

    public List<UserStatus> getUserStatuses() {
        List<UserStatus> userStatuses = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLQueries.GET_MOST_RECENT_USER_STATUSES);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                String startTime = rs.getString("start_time");
                String endTime = rs.getString("end_time");

                String startTimeStr = startTime != null ? startTime : "N/A";
                String endTimeStr = endTime != null ? endTime : "N/A";

                userStatuses.add(new UserStatus(userId, username, startTimeStr, endTimeStr));
                LoggerUtility.info("UserSatus"+ userStatuses.stream().toList()+" "+userStatuses.size());
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error fetching user statuses", e);
        }

        return userStatuses;
    }
}