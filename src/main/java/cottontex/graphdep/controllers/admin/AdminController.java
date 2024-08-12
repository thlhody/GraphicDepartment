package cottontex.graphdep.controllers.admin;

import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.controllers.info.UserStatusDialogController;
import cottontex.graphdep.models.managers.UserSessionManager;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AdminController extends AdminBaseController {

    @FXML
    private Button manageUsersButton;
    @FXML
    private Button viewReportsButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button logoutButton;

    @FXML
    @Override
    public void initialize() {
        super.initialize();
        LoggerUtility.info("Initializing AdminController");
        if (logoutButton == null) {
            LoggerUtility.error("logoutButton is null in AdminController");
        }
        if (userSession == null) {
            userSession = UserSessionManager.getSession();
            LoggerUtility.info("Retrieved UserSession in AdminController: " + userSession);
        }
        if (userSession == null) {
            LoggerUtility.error("UserSession is still null in AdminController after retrieval attempt");
        } else {
            boolean initialized = initializeUserData();
            LoggerUtility.info("User data initialization result in AdminController: " + initialized);
        }
    }

    @Override
    protected void initializeDependencies() {
        // Initialize any admin-specific dependencies here
        LoggerUtility.initialize(this.getClass(), "Initializing AdminController dependencies");
    }

    @FXML
    protected void onUserStatusButtonClick() {
        LoggerUtility.buttonInfo("User Status", UserSessionManager.getSession().getUsername());
        Stage dialogStage = createCustomDialog(AppPathsFXML.USER_STATUS_DIALOG, "Users", UserStatusDialogController.class);
        if (dialogStage != null) {
            dialogStage.showAndWait();
        }
    }

    @FXML
    protected void onManageUsersClick() {
        LoggerUtility.buttonInfo("Manage Users", UserSessionManager.getSession().getUsername());
        Stage stage = (Stage) manageUsersButton.getScene().getWindow();
        loadAdminPage(stage, AppPathsFXML.USER_PAGE_LAYOUT, "Manage Users", UserStatusDialogController.class);
    }

    @FXML
    protected void viewReportsButton() {
        LoggerUtility.buttonInfo("View Reports", UserSessionManager.getSession().getUsername());
        if (viewReportsButton == null) {
            LoggerUtility.error("viewReportsButton is null when trying to view monthly time");
            return;
        }
        Stage stage = (Stage) viewReportsButton.getScene().getWindow();
        loadAdminPage(stage, AppPathsFXML.VIEW_MONTHLY_TIME_LAYOUT, "View Reports", AdminMonthlyController.class);
    }

    @FXML
    protected void onSettingsButtonClick() {
        LoggerUtility.buttonInfo("Settings", UserSessionManager.getSession().getUsername());
        Stage stage = (Stage) settingsButton.getScene().getWindow();
        loadAdminPage(stage, AppPathsFXML.SETTINGS_ADMIN_LAYOUT, "Settings", AdminSettingsController.class);
    }

    @FXML
    protected void onLogoutButtonClick() {
        LoggerUtility.buttonInfo("Logout", UserSessionManager.getSession().getUsername());
        performRoleSpecificLogout();
        UserSessionManager.clearSession();
        redirectToLogin();
    }

}