package cottontex.graphdep.controllers.user;

import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.controllers.LauncherController;
import cottontex.graphdep.controllers.common.BaseController;
import cottontex.graphdep.database.handlers.admin.UserManagementHandler;
import cottontex.graphdep.database.interfaces.admin.IUserManagementHandler;
import cottontex.graphdep.database.interfaces.user.IScheduleUserTable;
import cottontex.graphdep.database.interfaces.user.IUserTimeOffHandler;
import cottontex.graphdep.database.interfaces.user.IUserTimeTableHandler;
import cottontex.graphdep.database.handlers.user.UserTimeOffHandler;
import cottontex.graphdep.database.handlers.user.UserTimeTableHandler;
import cottontex.graphdep.models.WorkSessionState;
import cottontex.graphdep.models.managers.UserSessionManager;
import cottontex.graphdep.services.user.UserBaseService;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.Date;

public abstract class UserBaseController extends BaseController {

    protected UserBaseService userBaseService;
    protected WorkSessionState workSessionState;
    protected IScheduleUserTable scheduleUserTable;
    protected IUserTimeOffHandler userTimeOffHandler;
    protected IUserTimeTableHandler userTimeTableHandler;
    protected IUserManagementHandler userManagementHandler;

    @FXML
    protected Label welcomeLabel;

    @FXML
    @Override
    public void initialize() {
        super.initialize();
        LoggerUtility.initialize(this.getClass(), "Initializing UserBaseController");
        initializeUserDependencies();
        initializeUserSession();
        initializeUserComponents();
        if (requiresUserSession()) {
            if (userSession == null) {
                userSession = UserSessionManager.getSession();
            }
            if (userSession != null) {
                initializeUserData();
            } else {
                LoggerUtility.error("UserSession is null and couldn't be retrieved from UserSessionManager.");
            }
        }
    }

    protected void initializeUserDependencies() {
        this.scheduleUserTable = getHandler(IScheduleUserTable.class);
        this.userTimeOffHandler = getHandler(IUserTimeOffHandler.class);
        this.userTimeTableHandler = getHandler(IUserTimeTableHandler.class);
        this.userManagementHandler = getHandler(IUserManagementHandler.class);
        this.userBaseService = new UserBaseService(scheduleUserTable);
        LoggerUtility.info("User base dependencies initialized successfully in " + this.getClass().getSimpleName());
    }

    protected void initializeUserSession() {
        this.userSession = UserSessionManager.getSession();
        if (this.userSession != null) {
            boolean initialized = initializeUserData();
            LoggerUtility.info("User data initialization result in " + this.getClass().getSimpleName() + ": " + initialized);
            if (!initialized) {
                LoggerUtility.error("Failed to initialize user data in " + this.getClass().getSimpleName());
            }
        } else {
            LoggerUtility.error("UserSession is null in " + this.getClass().getSimpleName() + " initialize method");
        }
    }

    protected abstract void initializeUserComponents();


    @Override
    protected boolean initializeUserData() {
        if (!userSession.isUser()) {
            LoggerUtility.error("Non-user in UserBaseController.initializeRoleSpecificData()");
            return false;
        }

        if (scheduleUserTable != null) {
            Integer userId = userSession.getUserId();
            Date currentDate = new Date(System.currentTimeMillis());
            workSessionState = scheduleUserTable.getWorkSessionState(userId);

            if (!workSessionState.isWorking() && scheduleUserTable.hasActiveSession(userId, currentDate)) {
                LoggerUtility.info("Active session found for user " + userId + ". Resuming work state.");
                workSessionState.setWorking(true);
                workSessionState.setPaused(true);
                scheduleUserTable.saveWorkSessionState(userId, workSessionState);
            }
        } else {
            LoggerUtility.error("scheduleUserTable is null in UserBaseController.initializeUserData()");
        }

        LoggerUtility.info("User data initialized successfully for user in UserBaseController");
        return true;
    }

    @Override
    protected boolean initializeRoleSpecificData() {
        LoggerUtility.info("Initializing user-specific data in UserBaseController");
        if (userSession == null || !userSession.isUser()) {
            LoggerUtility.error("Invalid user session or non-user attempting to access user-specific controller");
            return false;
        }

        initializeWorkState();
        return true;
    }

    protected void initializeWorkState() {
        if (userSession != null) {
            Integer userId = userSession.getUserId();
            workSessionState = userBaseService.initializeWorkState(userId);
        } else {
            LoggerUtility.error("UserSession is null in UserBaseController.initializeWorkState()");
        }
    }

    protected void saveWorkSessionState() {
        if (userSession != null) {
            userBaseService.saveWorkSessionState(userSession.getUserId(), workSessionState);
        } else {
            LoggerUtility.error("Unable to save work session state. UserSession is null.");
        }
    }

    protected void setWelcomeMessage() {
        if (userSession != null && userSession.getName() != null) {
            String welcomeMessage = "Welcome, " + UserSessionManager.getSession().getName() + "!";
            welcomeLabel.setText(welcomeMessage);
            LoggerUtility.info("Welcome message set for user: " + UserSessionManager.getSession().getName());
        } else {
            welcomeLabel.setText("Welcome!");
            LoggerUtility.warn("Welcome message set without user name. UserSession or user name may be null.");
        }
    }

    protected Scene getCurrentScene() {
        if (logoutButton != null) {
            return logoutButton.getScene();
        } else {
            LoggerUtility.error("Cannot get scene as logoutButton is null");
            return null;
        }
    }

    @Override
    protected void performRoleSpecificLogout() {
        if (userSession != null) {
            userBaseService.clearWorkSessionState(userSession.getUserId());
            LoggerUtility.info("User-specific logout actions completed for user: " + userSession.getUsername());
        } else {
            LoggerUtility.error("Unable to perform role-specific logout. UserSession is null.");
        }
    }

    @Override
    protected void redirectToLogin() {
        Platform.runLater(() -> {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            LauncherController launcherController = new LauncherController();
            launcherController.initialize(); // Ensure proper initialization
            LoggerUtility.switchController(this.getClass(), LauncherController.class, "Logged out user");
            loadPage(stage, AppPathsFXML.LAUNCHER, "Login", null);
            LoggerUtility.info("User redirected to login");
        });
    }
}