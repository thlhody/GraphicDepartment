package cottontex.graphdep.utils;

import cottontex.graphdep.database.interfaces.*;
import cottontex.graphdep.database.handlers.admin.*;
import cottontex.graphdep.database.interfaces.admin.IAdminScheduleHandler;
import cottontex.graphdep.database.interfaces.admin.IAdminTimeTableHandler;
import cottontex.graphdep.database.interfaces.admin.IUserManagementHandler;
import cottontex.graphdep.database.interfaces.user.IScheduleUserTable;
import cottontex.graphdep.database.interfaces.user.IUserTimeOffHandler;
import cottontex.graphdep.database.interfaces.user.IUserTimeTableHandler;
import cottontex.graphdep.database.handlers.user.*;
import cottontex.graphdep.database.handlers.UserLogin;

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
        dependencies.put(IAdminScheduleHandler.class, new AdminScheduleHandler());
        dependencies.put(IAdminTimeTableHandler.class, new AdminTimeTableHandler());
        dependencies.put(IUserManagementHandler.class, new UserManagementHandler());
        dependencies.put(IUserTimeOffHandler.class, new UserTimeOffHandler());
        dependencies.put(IUserTimeTableHandler.class, new UserTimeTableHandler());
        dependencies.put(IScheduleUserTable.class, new ScheduleUserTable());
        dependencies.put(IUserLogin.class, new UserLogin());
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
    }

    // Clear all dependencies (useful for testing)
    void clearDependencies() {
        dependencies.clear();
    }
}