package cottontex.graphdep.utils;

import cottontex.graphdep.database.interfaces.*;
import cottontex.graphdep.database.handlers.admin.*;
import cottontex.graphdep.database.interfaces.admin.*;
import cottontex.graphdep.database.interfaces.user.*;
import cottontex.graphdep.database.handlers.user.*;
import cottontex.graphdep.database.handlers.UserLogin;
import cottontex.graphdep.services.user.UserBaseService;
import cottontex.graphdep.services.user.UserService;

import java.util.HashMap;
import java.util.Map;

public class DependencyFactory {
    private static DependencyFactory instance;
    private final Map<Class<?>, Object> dependencies = new HashMap<>();

    private DependencyFactory() {
        initializeDependencies();
    }

    public static synchronized DependencyFactory getInstance() {
        if (instance == null) {
            instance = new DependencyFactory();
        }
        return instance;
    }

    private void initializeDependencies() {
        LoggerUtility.info("Initializing dependencies...");
        dependencies.put(IAdminScheduleHandler.class, new AdminScheduleHandler());
        dependencies.put(IAdminTimeTableHandler.class, new AdminTimeTableHandler());
        dependencies.put(IUserManagementHandler.class, new UserManagementHandler());
        dependencies.put(IUserTimeOffHandler.class, new UserTimeOffHandler());
        dependencies.put(IUserTimeTableHandler.class, new UserTimeTableHandler());
        dependencies.put(IScheduleUserTable.class, new ScheduleUserTable());
        dependencies.put(IUserLogin.class, new UserLogin());

        IScheduleUserTable scheduleUserTable = (IScheduleUserTable) dependencies.get(IScheduleUserTable.class);
        dependencies.put(UserBaseService.class, new UserBaseService(scheduleUserTable));
        dependencies.put(UserService.class, new UserService(scheduleUserTable));
        LoggerUtility.info("Dependencies initialized successfully");
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type) {
        T dependency = (T) dependencies.get(type);
        if (dependency == null) {
            throw new IllegalArgumentException("No dependency found for type: " + type.getName());
        }
        return dependency;
    }

    // For testing purposes
    void setDependency(Class<?> type, Object implementation) {
        dependencies.put(type, implementation);
        LoggerUtility.info("Set custom dependency for type: " + type.getName());
    }

    // Clear all dependencies (useful for testing)
    void clearDependencies() {
        dependencies.clear();
        LoggerUtility.info("All dependencies cleared");
    }

    // Reinitialize dependencies if needed
    public void reinitializeDependencies() {
        clearDependencies();
        initializeDependencies();
        LoggerUtility.info("Dependencies reinitialized");
    }

    // Get the current IScheduleUserTable instance
    public IScheduleUserTable getScheduleUserTable() {
        return get(IScheduleUserTable.class);
    }

    // Reinitialize UserService with the current IScheduleUserTable
    public void reinitializeUserService() {
        IScheduleUserTable scheduleUserTable = getScheduleUserTable();
        dependencies.put(UserService.class, new UserService(scheduleUserTable));
        LoggerUtility.info("UserService reinitialized with current IScheduleUserTable");
    }
}