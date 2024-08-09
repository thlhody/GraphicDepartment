package cottontex.graphdep.controllers.admin;

import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.controllers.common.BaseController;
import cottontex.graphdep.models.UserSession;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class AdminBaseController extends BaseController {
    protected UserSession userSession;

    public void setUserSession(UserSession session) {
        LoggerUtility.info("Setting UserSession in AdminBaseController: " + session);
        this.userSession = session;
        if (!initializeUserData()) {
            LoggerUtility.error("******ERROR-setUserSession****** - Failed to initialize user data for admin.");
            Platform.runLater(() -> {
                showAlert("Access Denied", "You do not have the required permissions to access this page.");
                // Redirect to login page
                Stage stage = (Stage) getScene().getWindow();
                loadPage(stage, AppPathsFXML.LAUNCHER, "Login", new UserSession());
            });
        }
    }

    protected boolean initializeUserData() {
        LoggerUtility.info("Initializing user data in AdminBaseController");
        if (userSession == null) {
            LoggerUtility.error("******ERROR-initializeUserData****** - UserSession is null in AdminBaseController.initializeUserData()");
            return false;
        }
        if (!userSession.isAdmin()) {
            LoggerUtility.error("******ERROR-setUserSession****** - User is not an admin in AdminBaseController.initializeUserData()");
            return false;
        }
        LoggerUtility.info("User data initialized successfully for admin");
        return true;
    }

    protected abstract Scene getScene();
}

