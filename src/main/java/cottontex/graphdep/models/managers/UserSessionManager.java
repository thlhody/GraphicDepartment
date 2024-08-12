package cottontex.graphdep.models.managers;

import cottontex.graphdep.models.UserSession;
import cottontex.graphdep.utils.LoggerUtility;

public class UserSessionManager {
    private static UserSession currentSession;

    public static void setSession(UserSession session) {
        currentSession = session;
        LoggerUtility.info("UserSession set in UserSessionManager: " + session);
    }

    public static UserSession getSession() {
        if (currentSession == null) {
            currentSession = UserSession.getInstance();
        }
        return currentSession;
    }

    public static void clearSession() {
        currentSession = null;
        UserSession.logout();
        LoggerUtility.info("UserSession cleared from UserSessionManager");
    }

    public static void initializeSession(Integer userId, String username, String name, String role, Integer employeeId) {
        UserSession.initializeSession(userId, username, name, role, employeeId);
        currentSession = UserSession.getInstance();
        LoggerUtility.info("UserSession initialized and set in UserSessionManager: " + currentSession);
    }
}