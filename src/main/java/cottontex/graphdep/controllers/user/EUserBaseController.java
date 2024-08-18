//package cottontex.graphdep.controllers.user;
//
//import cottontex.graphdep.constants.AppPathsFXML;
//import cottontex.graphdep.controllers.ELauncherController;
//import cottontex.graphdep.controllers.common.EBaseController;
//import cottontex.graphdep.database.interfaces.user.IScheduleUserTable;
//import cottontex.graphdep.database.interfaces.user.IUserTimeOffHandler;
//import cottontex.graphdep.database.interfaces.user.IUserTimeTableHandler;
//import cottontex.graphdep.database.interfaces.admin.IUserManagementHandler;
//import cottontex.graphdep.models.UserSession;
//import cottontex.graphdep.models.WorkSessionState;
//import cottontex.graphdep.models.managers.EUserSessionManager;
//import cottontex.graphdep.services.user.UserBaseService;
//import cottontex.graphdep.utils.LoggerUtility;
//import javafx.application.Platform;
//import javafx.stage.Stage;
//
//public abstract class EUserBaseController extends EBaseController {
//
//    protected UserBaseService userBaseService;
//    protected WorkSessionState workSessionState;
//    protected IScheduleUserTable scheduleUserTable;
//    protected IUserTimeOffHandler userTimeOffHandler;
//    protected IUserTimeTableHandler userTimeTableHandler;
//    protected IUserManagementHandler userManagementHandler;
//
//    @Override
//    protected void initializeDependencies() {
//        this.scheduleUserTable = getDependency(IScheduleUserTable.class);
//        this.userTimeOffHandler = getDependency(IUserTimeOffHandler.class);
//        this.userTimeTableHandler = getDependency(IUserTimeTableHandler.class);
//        this.userManagementHandler = getDependency(IUserManagementHandler.class);
//        this.userBaseService = new UserBaseService(scheduleUserTable);
//        LoggerUtility.info("User base dependencies initialized successfully in " + this.getClass().getSimpleName());
//    }
//
//    @Override
//    protected boolean initializeUserData(UserSession session) {
//        if (!session.isUser()) {
//            LoggerUtility.error("Non-user in UserBaseController.initializeUserData()");
//            return false;
//        }
//        initializeWorkState(session);
//        return true;
//    }
//
//    @Override
//    protected void initializeComponents() {
//    }
//
//    @Override
//    protected boolean requiresUserSession() {
//        return true;
//    }
//
//    protected void initializeWorkState(UserSession session) {
//        WorkSessionState state = EUserSessionManager.getWorkSessionState()
//                .orElseGet(() -> userBaseService.initializeWorkState(session.getUserId()));
//        EUserSessionManager.setWorkSessionState(state);
//        LoggerUtility.info("WorkSessionState initialized for user: " + session.getUsername() +
//                ", isWorking: " + state.isWorking() +
//                ", isPaused: " + state.isPaused());
//    }
//
//    protected WorkSessionState getWorkSessionState() {
//        return EUserSessionManager.getWorkSessionState().orElseGet(WorkSessionState::new);
//    }
//
//    protected void updateWorkSessionState(WorkSessionState state) {
//        EUserSessionManager.updateWorkSessionState(state);
//    }
//
//    @Override
//    protected void performRoleSpecificLogout() {
//        userSession.ifPresent(session -> {
//            userBaseService.clearWorkSessionState(session.getUserId());
//            LoggerUtility.info("User-specific logout actions completed for user: " + session.getUsername());
//        });
//    }
//
//    @Override
//    protected void redirectToLogin() {
//        Platform.runLater(() -> {
//            Stage stage = (Stage) getCurrentScene().getWindow();
//            LoggerUtility.switchController(this.getClass(), ELauncherController.class, "Logged out user");
//            loadPage(stage, AppPathsFXML.LAUNCHER, "Login", null);
//            LoggerUtility.info("User redirected to login");
//        });
//    }
//
//    @Override
//    protected void handleUserDataInitializationFailure() {
//        super.handleUserDataInitializationFailure();
//        showAlert("Access Denied", "You do not have the required permissions to access this page.");
//        redirectToLogin();
//    }
//
//    @Override
//    protected void handleNullUserSession() {
//        super.handleNullUserSession();
//        redirectToLogin();
//    }
//}

package cottontex.graphdep.controllers.user;

import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.controllers.ELauncherController;
import cottontex.graphdep.controllers.common.EBaseController;
import cottontex.graphdep.database.interfaces.user.IScheduleUserTable;
import cottontex.graphdep.database.interfaces.user.IUserTimeOffHandler;
import cottontex.graphdep.database.interfaces.user.IUserTimeTableHandler;
import cottontex.graphdep.database.interfaces.admin.IUserManagementHandler;
import cottontex.graphdep.models.UserSession;
import cottontex.graphdep.models.WorkSessionState;
import cottontex.graphdep.models.managers.EUserSessionManager;
import cottontex.graphdep.services.user.UserBaseService;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.application.Platform;
import javafx.stage.Stage;

public abstract class EUserBaseController extends EBaseController {

    protected UserBaseService userBaseService;
    protected IScheduleUserTable scheduleUserTable;
    protected IUserTimeOffHandler userTimeOffHandler;
    protected IUserTimeTableHandler userTimeTableHandler;
    protected IUserManagementHandler userManagementHandler;

    @Override
    protected void initializeDependencies() {
        this.scheduleUserTable = getDependency(IScheduleUserTable.class);
        this.userTimeOffHandler = getDependency(IUserTimeOffHandler.class);
        this.userTimeTableHandler = getDependency(IUserTimeTableHandler.class);
        this.userManagementHandler = getDependency(IUserManagementHandler.class);
        this.userBaseService = getDependency(UserBaseService.class);
        LoggerUtility.info("User base dependencies initialized successfully in " + this.getClass().getSimpleName());
    }

    @Override
    protected boolean initializeUserData(UserSession session) {
        if (!session.isUser()) {
            LoggerUtility.error("Non-user in UserBaseController.initializeUserData()");
            return false;
        }
        initializeWorkSessionState(session);
        return true;
    }

    @Override
    protected void initializeComponents() {
        // This method can remain empty or contain common initialization logic
    }

    @Override
    protected boolean requiresUserSession() {
        return true;
    }

    protected void initializeWorkSessionState(UserSession session) {
        WorkSessionState state = EUserSessionManager.getWorkSessionState()
                .orElseGet(() -> userBaseService.initializeWorkState(session.getUserId()));
        EUserSessionManager.setWorkSessionState(state);
        LoggerUtility.info("WorkSessionState initialized for user: " + session.getUsername() +"sessionState="+state.getSessionState()+
                ", isWorking: " + state.isWorking() +
                ", isPaused: " + state.isPaused());
    }

    protected WorkSessionState getCurrentWorkSessionState() {
        return EUserSessionManager.getWorkSessionState().orElseGet(WorkSessionState::new);
    }

    protected void updateWorkSessionState(WorkSessionState state) {
        EUserSessionManager.updateWorkSessionState(state);
        LoggerUtility.info("WorkSessionState updated: " + state);
    }

    @Override
    protected void performRoleSpecificLogout() {
        userSession.ifPresent(session -> {
            userBaseService.clearWorkSessionState(session.getUserId());
            LoggerUtility.info("User-specific logout actions completed for user: " + session.getUsername());
        });
    }

    @Override
    protected void redirectToLogin() {
        Platform.runLater(() -> {
            Stage stage = (Stage) getCurrentScene().getWindow();
            LoggerUtility.switchController(this.getClass(), ELauncherController.class, "Logged out user");
            loadPage(stage, AppPathsFXML.LAUNCHER, "Login", null);
            LoggerUtility.info("User redirected to login");
        });
    }

    @Override
    protected void handleUserDataInitializationFailure() {
        super.handleUserDataInitializationFailure();
        showAlert("Access Denied", "You do not have the required permissions to access this page.");
        redirectToLogin();
    }

    @Override
    protected void handleNullUserSession() {
        super.handleNullUserSession();
        redirectToLogin();
    }
}