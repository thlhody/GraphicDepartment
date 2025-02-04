package cottontex.graphdep.database.handlers.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import cottontex.graphdep.constants.JsonPaths;
import cottontex.graphdep.constants.SQLQueries;
import cottontex.graphdep.database.BaseDatabase;
import cottontex.graphdep.database.interfaces.admin.IAdminTimeTableHandler;
import cottontex.graphdep.models.UserOffline;
import cottontex.graphdep.models.UserStatus;
import cottontex.graphdep.models.WorkSessionStateOffline;
import cottontex.graphdep.utils.LoggerUtility;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class AdminTimeTableHandler extends BaseDatabase implements IAdminTimeTableHandler {

    @Override
    public List<UserStatus> getUserStatuses() {
        List<UserStatus> userStatuses = new ArrayList<>();

        if (!isUsingLocalStorage()) {
            // Try to fetch from database
            userStatuses = getUserStatusesFromDatabase();
        }

        if (userStatuses.isEmpty()) {
            // If database fetch failed or we're in offline mode, use JSON
            userStatuses = getUserStatusesFromJson();
        }

        return userStatuses;
    }

    private List<UserStatus> getUserStatusesFromJson() {
        List<WorkSessionStateOffline> workSessionStates = readFromJson(JsonPaths.WORK_SESSION_STATE_JSON, new TypeReference<List<WorkSessionStateOffline>>() {});
        List<UserOffline> users = readFromJson(JsonPaths.USERS_JSON, new TypeReference<List<UserOffline>>() {});

        if (workSessionStates.isEmpty()) {
            // If work_session_state.json is empty, create UserStatus objects from users.json
            return users.stream()
                    .filter(user -> user.getEmployeeId() != 0) // Exclude admin users
                    .map(user -> new UserStatus(
                            user.getUserId(),
                            user.getUsername(),
                            user.getRole(),
                            "N/A",
                            "N/A"
                    ))
                    .collect(Collectors.toList());
        } else {
            Map<Integer, UserOffline> userMap = users.stream()
                    .collect(Collectors.toMap(UserOffline::getUserId, u -> u));

            return workSessionStates.stream()
                    .map(state -> {
                        UserOffline user = userMap.get(state.getUserId());
                        return new UserStatus(
                                state.getUserId(),
                                user != null ? user.getUsername() : "User" + state.getUserId(),
                                user != null ? user.getRole() : "USER",
                                state.getStartTimestamp() != null ? state.getStartTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "N/A",
                                determineEndTime(state)
                        );
                    })
                    .collect(Collectors.toList());
        }
    }

    private String determineEndTime(WorkSessionStateOffline state) {
        if ("ENDED".equals(state.getSessionState())) {
            return state.getPauseTimestamp() != null ? state.getPauseTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "N/A";
        } else {
            return "N/A";
        }
    }
    private List<UserStatus> getUserStatusesFromDatabase() {
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
            LoggerUtility.error("Error fetching user statuses from database", e);
        }
        return userStatuses;
    }

}