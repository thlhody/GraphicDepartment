package cottontex.graphdep.models;

import cottontex.graphdep.utils.LoggerUtility;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserSession {

    @Setter
    private static UserSession instance;
    private Integer userId;
    private String username;
    private String role;

    public UserSession() {

    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public static void clearInstance() {
        instance = null;
    }

    public boolean isAdmin() {
        boolean isAdmin = "ADMIN".equals(this.role);
        LoggerUtility.info("Checking if user is admin: " + isAdmin + " (role: " + this.role + ")");
        return isAdmin;
    }
    public boolean isUser() {
        boolean isUser = "USER".equals(this.role);
        LoggerUtility.info("Checking if user is user: " + isUser + " (role: " + this.role + ")");
        return isUser;
    }
}
