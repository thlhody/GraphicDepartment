package cottontex.graphdep.models.managers;

import cottontex.graphdep.models.UserSession;
import cottontex.graphdep.models.WorkSessionState;
import cottontex.graphdep.utils.LoggerUtility;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class EUserSessionManager {
    private static final AtomicReference<UserSession> currentSession = new AtomicReference<>();
    private static final AtomicReference<WorkSessionState> currentWorkSessionState = new AtomicReference<>();

    private EUserSessionManager() {
        // Private constructor to prevent instantiation
    }

    public static void setSession(UserSession session) {
        currentSession.set(session);
        LoggerUtility.info("UserSession set in UserSessionManager: " + session);
    }

    public static Optional<UserSession> getSession() {
        return Optional.ofNullable(currentSession.get());
    }

    public static void setWorkSessionState(WorkSessionState state) {
        currentWorkSessionState.set(state);
        LoggerUtility.info("WorkSessionState set in UserSessionManager: " + state);
    }

    public static Optional<WorkSessionState> getWorkSessionState() {
        return Optional.ofNullable(currentWorkSessionState.get());
    }

    public static void clearSession() {
        currentSession.set(null);
        currentWorkSessionState.set(null);
        UserSession.logout();
        LoggerUtility.info("UserSession cleared from UserSessionManager");
    }

    public static void initializeSession(Integer userId, String username, String name, String role, Integer employeeId) {
        if (userId == null || username == null || name == null || role == null) {
            LoggerUtility.error("Attempt to initialize session with null values");
            throw new IllegalArgumentException("All session parameters must be non-null");
        }
        UserSession.initializeSession(userId, username, name, role, employeeId);
        setSession(UserSession.getInstance());
        LoggerUtility.info("UserSession initialized and set in UserSessionManager: " + currentSession.get());
    }

    public static void updateWorkSessionState(WorkSessionState state) {
        setWorkSessionState(state);
        // Here you might want to add logic to persist the state to the database
        LoggerUtility.info("WorkSessionState updated in UserSessionManager: " + state);
    }

    public static boolean isAdmin() {
        return getSession().map(session -> "ADMIN".equals(session.getRole())).orElse(false);
    }

    public static boolean isUser() {
        return getSession().map(session -> "USER".equals(session.getRole())).orElse(false);
    }

    public static String getCurrentUsername() {
        return getSession().map(UserSession::getUsername).orElse("Unknown");
    }
}