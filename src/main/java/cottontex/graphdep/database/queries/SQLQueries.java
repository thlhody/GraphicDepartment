package cottontex.graphdep.database.queries;

public class SQLQueries {

    // User-related queries

    public static final String GET_USER_STATUS =
            "SELECT username, " +
                    "MAX(CASE WHEN time_b IS NULL THEN time_a ELSE NULL END) as start_time, " +
                    "MAX(time_b) as end_time, " +
                    "CASE WHEN MAX(CASE WHEN time_b IS NULL THEN 1 ELSE 0 END) = 1 THEN TRUE ELSE FALSE END as is_online " +
                    "FROM users u " +
                    "LEFT JOIN time_processing tp ON u.user_id = tp.user_id " +
                    "WHERE u.user_id = ? AND DATE(tp.time_a) = CURDATE() " +
                    "GROUP BY u.user_id, u.username";


    public static final String GET_USER_STATUSES =
            "SELECT u.user_id, u.username, " +
                    "MAX(CASE WHEN tp.time_b IS NULL THEN 1 ELSE 0 END) as is_online, " +
                    "MAX(tp.time_a) as start_time, " +
                    "MAX(tp.time_b) as end_time " +
                    "FROM users u " +
                    "LEFT JOIN time_processing tp ON u.user_id = tp.user_id " +
                    "WHERE DATE(tp.time_a) = CURDATE() OR tp.time_a IS NULL " +
                    "GROUP BY u.user_id, u.username";

    // Time processing queries
    public static final String SAVE_START_TIME =
            "INSERT INTO time_processing (user_id, time_a) VALUES (?, ?)";

    public static final String SAVE_PAUSE_TIME =
            "UPDATE time_processing SET time_b = ? WHERE user_id = ? AND time_b IS NULL";

    public static final String CHECK_ACTIVE_SESSION =
            "SELECT COUNT(*) FROM time_processing WHERE user_id = ? AND DATE(time_a) = ? AND time_b IS NULL";

    // User login queries
    public static final String ADD_USER =
            "INSERT INTO users (name, username, password, role) VALUES (?, ?, ?, 'USER')";

    public static final String AUTHENTICATE_USER =
            "SELECT role FROM users WHERE username = ? AND password = ?";

    public static final String GET_USERNAME_BY_ID =
            "SELECT username FROM users WHERE user_id = ?";

    // User management queries
    public static final String GET_USER_ID =
            "SELECT user_id FROM users WHERE username = ?";

    public static final String GET_ALL_USERNAMES =
            "SELECT username FROM users WHERE role != 'ADMIN'";

    public static final String RESET_PASSWORD =
            "UPDATE users SET password = ? WHERE username = ?";
    public static final String DELETE_USER =
            "DELETE FROM users WHERE username = ? AND role != 'ADMIN'";

    public static final String CHANGE_PASSWORD =
            "UPDATE users SET password = ? WHERE user_id = ? AND password = ?";

    // Admin schedule handle queries

    public static final String GET_MONTHLY_WORK_DATA =
            "SELECT u.name, " +
                    "DAY(wi.first_start_time) AS day_number, " +
                    "TIME_FORMAT(SEC_TO_TIME(SUM(TIME_TO_SEC(wi.total_worked_time))), '%H:%i') AS daily_total " +
                    "FROM users u " +
                    "LEFT JOIN work_interval wi ON u.user_id = wi.user_id " +
                    "WHERE MONTH(wi.first_start_time) = ? AND YEAR(wi.first_start_time) = ? " +
                    "GROUP BY u.name, DAY(wi.first_start_time) " +
                    "ORDER BY u.name, day_number";
}