package cottontex.graphdep.controllers.user;

import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.controllers.common.BaseController;
import cottontex.graphdep.database.queries.admin.UserManagementHandler;
import cottontex.graphdep.database.queries.user.UserTimeOffHandler;
import cottontex.graphdep.database.queries.user.UserTimeTableHandler;
import cottontex.graphdep.models.UserSession;
import cottontex.graphdep.utils.DependencyFactory;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;


public abstract class UserBaseController extends BaseController {

    protected UserSession userSession;
    protected UserTimeTableHandler timeTableHandler;
    protected UserManagementHandler userManagementHandler;
    protected UserTimeOffHandler userTimeOffHandler;


    protected void initializeDependencies() {
        // Initialize common dependencies
        timeTableHandler = DependencyFactory.getInstance().createUserTimeTableHandler();
        userManagementHandler = DependencyFactory.getInstance().createUserManagementHandler();
        userTimeOffHandler = DependencyFactory.getInstance().createUserTimeOffHandler();
        // Initialize other common dependencies

        // Call method for controller-specific dependencies
        initializeControllerDependencies();
    }

    // This method can be overridden by subclasses to add their specific dependencies
    protected void initializeControllerDependencies() {
        // Default implementation does nothing
    }


    protected boolean initializeUserData() {
        LoggerUtility.info("Initializing user data in UserBaseController");
        if (userSession == null) {
            LoggerUtility.error("UserSession is null in UserBaseController.initializeUserData()");
            return false;
        }
        LoggerUtility.info("UserSession in UserBaseController.initializeUserData(): " + userSession);
        LoggerUtility.info("User role: " + userSession.getRole());
        if (!userSession.isUser()) {
            LoggerUtility.error("User is not a regular user in UserBaseController.initializeUserData(). Role: " + userSession.getRole());
            return false;
        }
        LoggerUtility.info("User data initialized successfully for user");
        return true;
    }

    public void setUserSession(UserSession session) {
        LoggerUtility.info("Setting UserSession in UserBaseController: " + session);
        this.userSession = session;
        initializeDependencies();
        boolean initialized = initializeUserData();
        LoggerUtility.info("User data initialization result: " + initialized);

        if (!initialized) {
            LoggerUtility.error("Failed to initialize user data for user.");
            Platform.runLater(() -> {
                showAlert("Access Denied", "You do not have the required permissions to access this page.");
                // Redirect to login page
                Stage stage = (Stage) getScene().getWindow();
                loadPage(stage, AppPathsFXML.LAUNCHER, "Login", new UserSession());
            });
        }
    }


    protected abstract Scene getScene();

}
