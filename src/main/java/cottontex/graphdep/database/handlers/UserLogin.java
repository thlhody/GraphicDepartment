package cottontex.graphdep.database.handlers;


import cottontex.graphdep.constants.SQLQueries;
import cottontex.graphdep.offlinedatadao.UsersJsonDao;
import cottontex.graphdep.database.BaseDatabase;
import cottontex.graphdep.database.interfaces.IUserLogin;
import cottontex.graphdep.models.UserOffline;
import cottontex.graphdep.utils.LoggerUtility;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


public class UserLogin extends BaseDatabase implements IUserLogin {

    private final UsersJsonDao userJsonDao;
    private Map<String, UserOffline> userCache;


    public UserLogin(UsersJsonDao userJsonDao) {
        this.userJsonDao = userJsonDao;
        this.userCache = new HashMap<>();
    }

    @Override
    public String authenticateUser(String username, String password) {
        if (isUsingLocalStorage()) {
            return authenticateUserOffline(username, password);
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = getPreparedStatement(conn, SQLQueries.AUTHENTICATE_USER)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error authenticating user in database", e);
            // If database authentication fails, fall back to offline authentication
            return authenticateUserOffline(username, password);
        }
        return null;
    }

    private String authenticateUserOffline(String username, String password) {
        LoggerUtility.info("Attempting offline authentication for user: " + username);

        if (userCache.isEmpty()) {
            loadUsersIntoCache();
        }

        UserOffline user = userCache.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user.getRole();
        }
        return null;
    }

    private void loadUsersIntoCache() {
        long startTime = System.currentTimeMillis();
        List<UserOffline> users = userJsonDao.findAll();
        userCache = users.stream().collect(Collectors.toMap(UserOffline::getUsername, Function.identity()));
        long endTime = System.currentTimeMillis();
        LoggerUtility.info("Loaded " + users.size() + " users into cache in " + (endTime - startTime) + " ms");
    }

    @Override
    public Integer getUserID(String username) {
        if (isUsingLocalStorage()) {
            return getUserIDOffline(username);
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = getPreparedStatement(conn, SQLQueries.GET_USER_ID)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error retrieving user ID from database", e);
            return getUserIDOffline(username);
        }
        return null;
    }

    private Integer getUserIDOffline(String username) {
        return userJsonDao.findAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .map(UserOffline::getUserId)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Integer getEmployeeId(String username) {
        if (isUsingLocalStorage()) {
            return getEmployeeIdOffline(username);
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = getPreparedStatement(conn, SQLQueries.GET_EMPLOYEE_ID)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("employee_id");
                }
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error retrieving employee ID from database", e);
            return getEmployeeIdOffline(username);
        }
        return null;
    }

    private Integer getEmployeeIdOffline(String username) {
        return userJsonDao.findAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .map(UserOffline::getEmployeeId)
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getName(String username) {
        if (isUsingLocalStorage()) {
            return getNameOffline(username);
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = getPreparedStatement(conn, SQLQueries.GET_NAME_BY_USERNAME)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error retrieving name from database", e);
            return getNameOffline(username);
        }
        return null;
    }

    private String getNameOffline(String username) {
        return userJsonDao.findAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .map(UserOffline::getName)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<UserOffline> getAllUsers() {
        if (isUsingLocalStorage()) {
            return userJsonDao.findAll();
        }

        List<UserOffline> users = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = getPreparedStatement(conn, SQLQueries.GET_ALL_USERS);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                UserOffline user = new UserOffline(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getInt("employee_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error retrieving all users from database", e);
            return userJsonDao.findAll();
        }
        return users;
    }

    @Override
    public void saveUser(UserOffline user) {
        if (isUsingLocalStorage()) {
            userJsonDao.save(user);
            return;
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = getPreparedStatement(conn, SQLQueries.INSERT_USER)) {
            stmt.setString(1, user.getName());
            stmt.setInt(2, user.getEmployeeId());
            stmt.setString(3, user.getUsername());
            stmt.setString(4, user.getPassword());
            stmt.setString(5, user.getRole());
            stmt.executeUpdate();
        } catch (SQLException e) {
            LoggerUtility.error("Error saving user to database", e);
            userJsonDao.save(user);
        }
    }
}