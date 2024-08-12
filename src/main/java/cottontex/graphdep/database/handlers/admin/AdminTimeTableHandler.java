package cottontex.graphdep.database.handlers.admin;

import cottontex.graphdep.constants.SQLQueries;
import cottontex.graphdep.database.BaseDatabase;
import cottontex.graphdep.database.interfaces.admin.IAdminTimeTableHandler;
import cottontex.graphdep.models.UserStatus;
import cottontex.graphdep.utils.LoggerUtility;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminTimeTableHandler extends BaseDatabase implements IAdminTimeTableHandler {

    @Override
    public List<UserStatus> getUserStatuses() {
        List<UserStatus> userStatuses = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = getPreparedStatement(conn, SQLQueries.GET_MOST_RECENT_USER_STATUSES);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                UserStatus status = new UserStatus(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("start_time") != null ? rs.getString("start_time") : "N/A",
                        rs.getString("end_time") != null ? rs.getString("end_time") : "N/A"
                );
                userStatuses.add(status);
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error fetching user statuses", e);
        }
        return userStatuses;
    }
}