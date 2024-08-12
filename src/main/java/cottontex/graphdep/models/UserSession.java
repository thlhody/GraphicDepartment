package cottontex.graphdep.models;

import cottontex.graphdep.utils.LoggerUtility;
import lombok.Getter;

public class UserSession {

    private static UserSession instance;

    @Getter
    private Integer userId;
    @Getter
    private String username;
    @Getter
    private String name;
    @Getter
    private String role;
    @Getter
    private Integer employeeId;  // Keep this for backward compatibility if needed

    private UserSession() {
        // Private constructor to prevent direct instantiation
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public static void initializeSession(Integer userId, String username, String name, String role, Integer employeeId) {
        UserSession session = getInstance();
        session.userId = userId;
        session.username = username;
        session.name = name;
        session.role = role;
        session.employeeId = employeeId;
    }

    public static void logout() {
        instance = null;
    }

    public boolean isAdmin() {
        boolean isAdmin = "ADMIN".equals(this.role);
        LoggerUtility.info("Checking if user is admin: " + isAdmin + " (role: " + this.role + ")");
        return isAdmin;
    }

    public boolean isUserAdmin() {
        boolean isUserAdmin = "USERADMIN".equals(this.role);
        LoggerUtility.info("Checking if user is user admin: " + isUserAdmin + " (role: " + this.role + ")");
        return isUserAdmin;
    }

    public boolean isUser() {
        boolean isUser = "USER".equals(this.role);
        LoggerUtility.info("Checking if user is regular user: " + isUser + " (role: " + this.role + ")");
        return isUser;
    }
}