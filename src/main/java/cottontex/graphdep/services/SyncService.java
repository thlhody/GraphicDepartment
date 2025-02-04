package cottontex.graphdep.services;

import cottontex.graphdep.database.DatabaseConnection;
import cottontex.graphdep.database.interfaces.IUserLogin;
import cottontex.graphdep.database.interfaces.user.IScheduleUserTable;
import cottontex.graphdep.database.interfaces.user.IUserTimeTableHandler;
import cottontex.graphdep.offlinedatadao.TimeProcessingJsonDao;
import cottontex.graphdep.offlinedatadao.UsersJsonDao;
import cottontex.graphdep.offlinedatadao.WorkIntervalJsonDao;
import cottontex.graphdep.offlinedatadao.WorkSessionStateJsonDao;
import cottontex.graphdep.utils.LoggerUtility;
import cottontex.graphdep.models.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.*;

public class SyncService {

    private static final int ONLINE_CHECK_TIMEOUT_SECONDS = 5; // Reduced from 10 to 5 seconds

    private final UsersJsonDao userJsonDao;
    private final TimeProcessingJsonDao timeProcessingJsonDao;
    private final WorkSessionStateJsonDao workSessionStateJsonDao;
    private final WorkIntervalJsonDao workIntervalJsonDao;

    private final IUserLogin dbUserDao;
    private final IScheduleUserTable dbScheduleUserTable;
    private final IUserTimeTableHandler dbUserTimeTableHandler;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public SyncService(UsersJsonDao userJsonDao,
                       TimeProcessingJsonDao timeProcessingJsonDao,
                       WorkSessionStateJsonDao workSessionStateJsonDao,
                       WorkIntervalJsonDao workIntervalJsonDao,
                       IUserLogin dbUserDao,
                       IScheduleUserTable dbScheduleUserTable,
                       IUserTimeTableHandler dbUserTimeTableHandler) {
        this.userJsonDao = userJsonDao;
        this.timeProcessingJsonDao = timeProcessingJsonDao;
        this.workSessionStateJsonDao = workSessionStateJsonDao;
        this.workIntervalJsonDao = workIntervalJsonDao;
        this.dbUserDao = dbUserDao;
        this.dbScheduleUserTable = dbScheduleUserTable;
        this.dbUserTimeTableHandler = dbUserTimeTableHandler;
    }

    public boolean startAutoSync() {
        LoggerUtility.info("Starting sync process...");
        boolean isOnline = isOnline();
        if (isOnline) {
            CompletableFuture.runAsync(this::sync)
                    .thenRun(() -> LoggerUtility.info("Sync process completed."))
                    .exceptionally(e -> {
                        LoggerUtility.error("Error during sync process", e);
                        return null;
                    });
        } else {
            LoggerUtility.info("Application is in offline mode. Scheduling hourly sync attempts.");
            scheduler.scheduleAtFixedRate(this::attemptSync, 1, 1, TimeUnit.HOURS);
        }
        return isOnline;
    }
    private void attemptSync() {
        if (isOnline()) {
            sync();
            scheduler.shutdown();
            LoggerUtility.info("Sync successful. Stopping scheduled sync attempts.");
        } else {
            LoggerUtility.warn("Application still in offline mode. Will try again in 1 hour.");
        }
    }

    public boolean isOnline() {
        LoggerUtility.info("Checking online status...");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executor.submit(() -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                return conn != null && !conn.isClosed();
            } catch (SQLException e) {
                LoggerUtility.info("Database is not available: " + e.getMessage());
                return false;
            }
        });

        try {
            boolean result = future.get(ONLINE_CHECK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            LoggerUtility.info("Online status check completed. Result: " + (result ? "Online" : "Offline"));
            return result;
        } catch (TimeoutException e) {
            LoggerUtility.info("Online status check timed out. Assuming offline mode.");
            return false;
        } catch (Exception e) {
            LoggerUtility.error("Error during online status check", e);
            return false;
        } finally {
            executor.shutdownNow();
        }
    }


    public void sync() {
        if (!isOnline()) {
            LoggerUtility.warn("Cannot sync: Offline mode");
            return;
        }

        try {
            syncUsers();
            syncTimeProcessing();
            syncWorkSessionState();
            syncWorkInterval();
            LoggerUtility.info("Synchronization completed successfully");
        } catch (Exception e) {
            LoggerUtility.error("Error during synchronization", e);
        }
    }

    private void syncUsers() {
        List<UserOffline> jsonUsers = userJsonDao.findAll();
        List<UserOffline> dbUsers = dbUserDao.getAllUsers();
        syncLists(jsonUsers, dbUsers, dbUserDao::saveUser, userJsonDao::save);
    }

    private void syncTimeProcessing() {
        List<TimeProcessingOffline> jsonTimeProcessing = timeProcessingJsonDao.findAll();
        List<TimeProcessingOffline> dbTimeProcessing = dbUserTimeTableHandler.getAllTimeProcessing();
        syncLists(jsonTimeProcessing, dbTimeProcessing, dbUserTimeTableHandler::saveTimeProcessing, timeProcessingJsonDao::save);
    }

    private void syncWorkSessionState() {
        List<WorkSessionStateOffline> jsonWSS = workSessionStateJsonDao.findAll();
        List<WorkSessionStateOffline> dbWSS = dbUserTimeTableHandler.getAllWorkSessionStates();
        syncLists(jsonWSS, dbWSS, dbUserTimeTableHandler::saveWorkSessionState, workSessionStateJsonDao::save);
    }

    private void syncWorkInterval() {
        List<WorkIntervalOffline> jsonWI = workIntervalJsonDao.findAll();
        List<WorkIntervalOffline> dbWI = dbScheduleUserTable.getAllWorkIntervals();
        syncLists(jsonWI, dbWI, dbScheduleUserTable::saveWorkInterval, workIntervalJsonDao::save);
    }

    private <T> void syncLists(List<T> jsonList, List<T> dbList,
                               java.util.function.Consumer<T> dbSaveFunction,
                               java.util.function.Consumer<T> jsonSaveFunction) {
        for (T jsonItem : jsonList) {
            if (!dbList.contains(jsonItem)) {
                try {
                    dbSaveFunction.accept(jsonItem);
                } catch (Exception e) {
                    LoggerUtility.error("Error saving item to database", e);
                }
            }
        }
        for (T dbItem : dbList) {
            if (!jsonList.contains(dbItem)) {
                try {
                    jsonSaveFunction.accept(dbItem);
                } catch (Exception e) {
                    LoggerUtility.error("Error saving item to JSON", e);
                }
            }
        }
    }
}