package cottontex.graphdep.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cottontex.graphdep.database.interfaces.*;
import cottontex.graphdep.database.handlers.admin.*;
import cottontex.graphdep.database.interfaces.admin.*;
import cottontex.graphdep.database.interfaces.user.*;
import cottontex.graphdep.database.handlers.user.*;
import cottontex.graphdep.database.handlers.UserLogin;
import cottontex.graphdep.offlinedatadao.TimeProcessingJsonDao;
import cottontex.graphdep.offlinedatadao.UsersJsonDao;
import cottontex.graphdep.offlinedatadao.WorkIntervalJsonDao;
import cottontex.graphdep.offlinedatadao.WorkSessionStateJsonDao;
import cottontex.graphdep.services.SyncService;
import cottontex.graphdep.services.user.UserBaseService;
import cottontex.graphdep.services.user.UserService;

import java.util.HashMap;
import java.util.Map;

public class DependencyFactory {
    private static DependencyFactory instance;
    private final Map<Class<?>, Object> dependencies = new HashMap<>();

    private DependencyFactory() {
        initializeDependencies();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        dependencies.put(ObjectMapper.class, objectMapper);
    }

    public static synchronized DependencyFactory getInstance() {
        if (instance == null) {
            instance = new DependencyFactory();
        }
        return instance;
    }

    private void initializeDependencies() {
        LoggerUtility.info("Initializing dependencies...");

        // Initialize DAOs first
        UsersJsonDao usersJsonDao = new UsersJsonDao();
        dependencies.put(UsersJsonDao.class, usersJsonDao);
        dependencies.put(TimeProcessingJsonDao.class, new TimeProcessingJsonDao());
        dependencies.put(WorkSessionStateJsonDao.class, new WorkSessionStateJsonDao());
        dependencies.put(WorkIntervalJsonDao.class, new WorkIntervalJsonDao());

        // Initialize handlers
        dependencies.put(IAdminScheduleHandler.class, new AdminScheduleHandler());
        dependencies.put(IAdminTimeTableHandler.class, new AdminTimeTableHandler());
        dependencies.put(IUserManagementHandler.class, new UserManagementHandler());
        dependencies.put(IUserTimeOffHandler.class, new UserTimeOffHandler());
        dependencies.put(IUserTimeTableHandler.class, new UserTimeTableHandler());
        dependencies.put(IScheduleUserTable.class, new ScheduleUserTable());
        dependencies.put(IUserLogin.class, new UserLogin(usersJsonDao));

        // Initialize services
        IScheduleUserTable scheduleUserTable = (IScheduleUserTable) dependencies.get(IScheduleUserTable.class);
        dependencies.put(UserBaseService.class, new UserBaseService(scheduleUserTable));
        dependencies.put(UserService.class, new UserService(scheduleUserTable));

        // Initialize SyncService
        SyncService syncService = new SyncService(
                usersJsonDao,
                (TimeProcessingJsonDao) dependencies.get(TimeProcessingJsonDao.class),
                (WorkSessionStateJsonDao) dependencies.get(WorkSessionStateJsonDao.class),
                (WorkIntervalJsonDao) dependencies.get(WorkIntervalJsonDao.class),
                (IUserLogin) dependencies.get(IUserLogin.class),
                scheduleUserTable,
                (IUserTimeTableHandler) dependencies.get(IUserTimeTableHandler.class)
        );
        dependencies.put(SyncService.class, syncService);

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

    // New method to reinitialize SyncService if needed
    public void reinitializeSyncService() {
        SyncService syncService = new SyncService(
                get(UsersJsonDao.class),
                get(TimeProcessingJsonDao.class),
                get(WorkSessionStateJsonDao.class),
                get(WorkIntervalJsonDao.class),
                get(IUserLogin.class),
                get(IScheduleUserTable.class),
                get(IUserTimeTableHandler.class)
        );
        dependencies.put(SyncService.class, syncService);
        LoggerUtility.info("SyncService reinitialized");
    }
}