package cottontex.graphdep.database.queries;

public class SQLQueries {

    // User-related queries

    public static final String GET_MOST_RECENT_USER_STATUSES =
            "WITH latest_date AS (SELECT MAX(DATE(time_a)) as max_date FROM time_processing) " +
                    "SELECT u.user_id, u.username, u.role, " +
                    "tp.time_a as start_time, tp.time_b as end_time " +
                    "FROM users u " +
                    "LEFT JOIN (SELECT user_id, time_a, time_b, " +
                    "           ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY time_a DESC) as rn " +
                    "           FROM time_processing " +
                    "           WHERE DATE(time_a) = (SELECT max_date FROM latest_date)) tp ON u.user_id = tp.user_id AND tp.rn = 1 " +
                    "WHERE u.role != 'ADMIN' " +
                    "ORDER BY CASE WHEN tp.time_b IS NULL THEN 1 ELSE 0 END, COALESCE(tp.time_b, tp.time_a) DESC";

    public static final String GET_MONTHLY_WORK_HOURS_USER =
            "SELECT * FROM work_interval WHERE user_id = ? ORDER BY first_start_time";

    // Time processing queries
    public static final String SAVE_START_HOUR =
            "INSERT INTO time_processing (user_id, time_a) VALUES (?, ?)";

    public static final String SAVE_PAUSE_TIME =
            "UPDATE time_processing SET time_b = ?, duration = TIMESTAMPDIFF(SECOND, time_a, ?) / 3600.0 " +
                    "WHERE user_id = ? AND time_b IS NULL";

    public static final String FINALIZE_WORK_DAY_TIME_PROCESSING =
            "UPDATE time_processing SET time_b = ?, duration = TIMESTAMPDIFF(SECOND, time_a, ?) / 3600.0 " +
                    "WHERE user_id = ? AND time_b IS NULL";

    public static final String FINALIZE_WORK_DAY_CALL_PROCEDURE =
            "{CALL calculate_work_interval(?, ?)}";

    public static final String HAS_ACTIVE_SESSION =
            "SELECT 1 FROM time_processing WHERE user_id = ? AND DATE(time_a) = ? AND time_b IS NULL LIMIT 1";
    // User login queries
    public static final String ADD_USER =
            "INSERT INTO users (name, username, password, role) VALUES (?, ?, ?, 'USER')";

    public static final String AUTHENTICATE_USER =
            "SELECT role FROM users WHERE username = ? AND password = ?";

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
            "SELECT u.name,\n" +
                    "       DAY(wi.work_date) AS day_number,\n" +
                    "       TIME_FORMAT(SEC_TO_TIME(SUM(wi.total_worked_seconds)), '%H:%i') AS daily_total\n" +
                    "FROM users u\n" +
                    "LEFT JOIN work_interval wi ON u.user_id = wi.user_id\n" +
                    "  AND YEAR(wi.work_date) = ?\n" +
                    "  AND MONTH(wi.work_date) = ?\n" +
                    "GROUP BY u.name, wi.work_date\n" +
                    "ORDER BY u.name, day_number";
}


//package cottontex.graphdep.database.queries;
//
//public class SQLQueries {
//
//    // User-related queries
//
//    public static final String GET_MOST_RECENT_USER_STATUSES =
//            "WITH latest_date AS (SELECT MAX(DATE(time_a)) as max_date FROM time_processing) " +
//                    "SELECT u.user_id, u.username, u.role, " +
//                    "tp.time_a as start_time, tp.time_b as end_time " +
//                    "FROM users u " +
//                    "LEFT JOIN (SELECT user_id, time_a, time_b, " +
//                    "           ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY time_a DESC) as rn " +
//                    "           FROM time_processing " +
//                    "           WHERE DATE(time_a) = (SELECT max_date FROM latest_date)) tp ON u.user_id = tp.user_id AND tp.rn = 1 " +
//                    "WHERE u.role != 'ADMIN' " +
//                    "ORDER BY CASE WHEN tp.time_b IS NULL THEN 1 ELSE 0 END, COALESCE(tp.time_b, tp.time_a) DESC";
//    public static final String GET_MONTHLY_WORK_HOURS_USER =
//            "SELECT first_start_time, breaks, breaks_time, end_time, total_worked_time " +
//                    "FROM work_interval " +
//                    "WHERE user_id = ? " +
//                    "  AND YEAR(first_start_time) = YEAR(CURRENT_DATE()) " +
//                    "  AND MONTH(first_start_time) = MONTH(CURRENT_DATE()) ORDER BY first_start_time DESC;";
//    // Time processing queries
//    public static final String SAVE_START_HOUR =
//            "INSERT INTO time_processing (user_id, time_a) VALUES (?, ?)";
//
//    public static final String SAVE_PAUSE_TIME =
//            "UPDATE time_processing SET time_b = ?, duration = TIMESTAMPDIFF(SECOND, time_a, ?) / 3600.0 WHERE user_id = ? AND time_b IS NULL";
//
//    public static final String FINALIZE_WORK_DAY_TIME_PROCESSING =
//            "UPDATE time_processing SET time_b = ?, duration = TIMESTAMPDIFF(SECOND, time_a, ?) / 3600.0 WHERE user_id = ? AND time_b IS NULL";
//
//    public static final String FINALIZE_WORK_DAY_CALL_PROCEDURE =
//            "{CALL calculate_work_interval(?, ?)}";
//
//    public static final String HAS_ACTIVE_SESSION =
//            "SELECT 1 FROM time_processing WHERE user_id = ? AND DATE(time_a) = ? AND time_b IS NULL LIMIT 1";
//
//    // User login queries
//    public static final String ADD_USER =
//            "INSERT INTO users (name, username, password, role) VALUES (?, ?, ?, 'USER')";
//
//    public static final String AUTHENTICATE_USER =
//            "SELECT role FROM users WHERE username = ? AND password = ?";
//
//    // User management queries
//    public static final String GET_USER_ID =
//            "SELECT user_id FROM users WHERE username = ?";
//
//    public static final String GET_ALL_USERNAMES =
//            "SELECT username FROM users WHERE role != 'ADMIN'";
//
//    public static final String RESET_PASSWORD =
//            "UPDATE users SET password = ? WHERE username = ?";
//    public static final String DELETE_USER =
//            "DELETE FROM users WHERE username = ? AND role != 'ADMIN'";
//
//    public static final String CHANGE_PASSWORD =
//            "UPDATE users SET password = ? WHERE user_id = ? AND password = ?";
//
//    // Admin schedule handle queries
//    public static final String GET_MONTHLY_WORK_DATA =
//            "SELECT u.name,  " +
//                    "       DAY(wi.first_start_time) AS day_number,  " +
//                    "       TIME_FORMAT(SEC_TO_TIME(SUM(TIME_TO_SEC(wi.total_worked_time))), '%H:%i') AS daily_total  " +
//                    "FROM users u  " +
//                    "LEFT JOIN work_interval wi ON u.user_id = wi.user_id  " +
//                    "  AND YEAR(wi.first_start_time) = ?  " +
//                    "  AND MONTH(wi.first_start_time) = ? " +
//                    "GROUP BY u.name, DAY(wi.first_start_time)  " +
//                    "ORDER BY u.name, day_number;";
//
//}