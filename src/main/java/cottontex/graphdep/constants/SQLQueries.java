package cottontex.graphdep.constants;

public class SQLQueries {

    // User-related handlers
    public static final String GET_MOST_RECENT_USER_STATUSES =
            "WITH latest_date AS (SELECT MAX(DATE(time_a)) as max_date FROM time_processing) \n" +
                    "SELECT u.user_id, u.username, u.role, \n" +
                    "       tp.time_a as start_time, tp.time_b as end_time \n" +
                    "FROM users u \n" +
                    "LEFT JOIN (SELECT user_id, time_a, time_b, \n" +
                    "           ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY time_a DESC) as rn \n" +
                    "           FROM time_processing \n" +
                    "           WHERE DATE(time_a) = (SELECT max_date FROM latest_date)) tp ON u.user_id = tp.user_id AND tp.rn = 1 \n" +
                    "WHERE u.employee_id != '00000' \n" +
                    "ORDER BY CASE WHEN tp.time_b IS NULL THEN 1 ELSE 0 END, COALESCE(tp.time_b, tp.time_a) DESC";

    public static final String GET_MONTHLY_WORK_HOURS_USER =
            "SELECT * FROM work_interval WHERE user_id = ? ORDER BY first_start_time";

    public static final String SAVE_START_HOUR =
            "INSERT INTO time_processing (user_id, time_a) VALUES (?, ?)";

    public static final String SAVE_PAUSE_TIME =
            "UPDATE time_processing SET time_b = ?, duration = TIMESTAMPDIFF(SECOND, time_a, ?) / 3600.0 " +
                    "WHERE user_id = ? AND time_b IS NULL";

    public static final String GET_WORK_SESSION_STATE =
            "SELECT time_a, session_state " +
                    "FROM time_processing " +
                    "WHERE user_id = ? " +
                    "ORDER BY time_a DESC LIMIT 1";


    public static final String SAVE_WORK_SESSION_STATE =
            "INSERT INTO work_session_state (user_id, is_working, is_paused, start_timestamp, pause_timestamp, session_state) " +
                    "VALUES (?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "is_working = VALUES(is_working), " +
                    "is_paused = VALUES(is_paused), " +
                    "start_timestamp = VALUES(start_timestamp), " +
                    "pause_timestamp = VALUES(pause_timestamp), " +
                    "session_state = VALUES(session_state), " +
                    "created_at = CURRENT_TIMESTAMP";

    public static final String CLEAR_WORK_SESSION_STATE =
            "DELETE FROM work_session_state WHERE user_id = ?";

    // Modified query to include session_state
    public static final String INSERT_TIME_PROCESSING =
            "INSERT INTO time_processing (user_id, time_a, session_state) VALUES (?, ?, ?)";

    // Modified query to include session_state
    public static final String UPDATE_TIME_PROCESSING =
            "UPDATE time_processing SET time_b = ?, session_state = ? WHERE user_id = ? AND time_b IS NULL";

    // Modified query to include session_state
    public static final String FINALIZE_WORK_DAY_TIME_PROCESSING =
            "UPDATE time_processing SET time_b = ?, duration = TIMESTAMPDIFF(SECOND, time_a, ?) / 3600.0, session_state = 'ENDED' " +
                    "WHERE user_id = ? AND time_b IS NULL";


    public static final String FINALIZE_WORK_DAY_CALL_PROCEDURE =
            "{CALL calculate_work_interval(?, ?)}";

    public static final String TIME_OFF_UPDATE =
            "INSERT INTO work_interval (user_id, first_start_time, end_time, total_worked_time, time_off_type) VALUES (?, ?, ?, ?, ?)";

    // User login handlers
    public static final String AUTHENTICATE_USER =
            "SELECT role FROM users WHERE username = ? AND password = ?";

    // User management handlers
    public static final String GET_EMPLOYEE_ID =
            "SELECT employee_id FROM users WHERE user_id = ?";

    public static final String GET_NAME =
            "SELECT name FROM users WHERE user_id = ?";

    public static final String GET_USER_ID =
            "SELECT user_id FROM users WHERE username = ?";

    public static final String ADD_USER =
            "INSERT INTO users (name, username, password, role, employee_id) VALUES (?, ?, ?, ?, ?)";

    public static final String CHECK_EMPLOYEE_ID= "SELECT COUNT(*) FROM users WHERE employee_id = ?";

    public static final String CHECK_USERNAME= "SELECT COUNT(*) FROM users WHERE username = ?";

    public static final String GET_ALL_USERNAMES =
            "SELECT username FROM users WHERE employee_id != '00000'";

    public static final String RESET_PASSWORD =
            "UPDATE users SET password = ? WHERE username = ?";

    public static final String DELETE_USER =
            "DELETE FROM users WHERE username = ? AND employee_id != '00000'";

    public static final String CHANGE_PASSWORD =
            "UPDATE users SET password = ? WHERE username = ? AND password = ?";

    // Admin schedule handle handlers
    public static final String GET_MONTHLY_WORK_DATA =
            "SELECT u.name, u.employee_id, \n" +
                    "       DAY(wi.work_date) AS day_number,\n" +
                    "       COALESCE(wi.time_off_type, TIME_FORMAT(SEC_TO_TIME(SUM(wi.total_worked_seconds)), '%H:%i')) AS daily_total,\n" +
                    "       wi.time_off_type,\n" +
                    "       DAYOFWEEK(wi.work_date) AS day_of_week\n" +
                    "FROM users u\n" +
                    "LEFT JOIN work_interval wi ON u.user_id = wi.user_id\n" +
                    "  AND YEAR(wi.work_date) = ?\n" +
                    "  AND MONTH(wi.work_date) = ?\n" +
                    "WHERE (wi.total_worked_seconds > 0 OR wi.time_off_type IS NOT NULL) AND u.employee_id != '00000'\n" +
                    "GROUP BY u.name, u.employee_id, wi.work_date, wi.time_off_type\n" +
                    "ORDER BY u.name, day_number";

    // admin add national holiday
    public static final String GET_NON_ADMIN_USERS =
            "SELECT user_id FROM users WHERE role != 'ADMIN'";

    public static final String INSERT_OR_UPDATE_HOLIDAY =
            "INSERT INTO work_interval (user_id, first_start_time, end_time, total_worked_time, time_off_type) " +
                    "VALUES (?, ?, ?, '00:00:00', 'SN') " +
                    "ON DUPLICATE KEY UPDATE " +
                    "first_start_time = VALUES(first_start_time), " +
                    "end_time = VALUES(end_time), " +
                    "time_off_type = CASE " +
                    "    WHEN total_worked_time > '00:00:00' THEN time_off_type " +
                    "    ELSE 'SN' " +
                    "END, " +
                    "total_worked_time = CASE " +
                    "    WHEN total_worked_time > '00:00:00' THEN total_worked_time " +
                    "    ELSE '00:00:00' " +
                    "END";

    public static final String DELETE_DUPLICATES =
            "DELETE t1 FROM work_interval t1 " +
                    "INNER JOIN work_interval t2 " +
                    "WHERE t1.id > t2.id " +
                    "AND t1.user_id = t2.user_id " +
                    "AND t1.work_date = t2.work_date";
}