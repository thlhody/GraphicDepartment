package cottontex.graphdep.controllers.admin;

import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.controllers.info.EUserStatusDialogController;
import cottontex.graphdep.database.interfaces.admin.IAdminTimeTableHandler;
import cottontex.graphdep.database.interfaces.admin.IUserManagementHandler;
import cottontex.graphdep.models.UserSession;
import cottontex.graphdep.models.managers.EUserSessionManager;
import cottontex.graphdep.services.admin.AdminService;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class EAdminController extends EAdminBaseController {

    private AdminService adminService;
    private IAdminTimeTableHandler adminTimeTableHandler;
    private IUserManagementHandler userManagementHandler;

    @FXML
    private Button manageUsersButton;
    @FXML
    private Button viewReportsButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button userStatusButton;
    @FXML
    private Label welcomeLabel;

    @Override
    protected void initializeDependencies() {
        super.initializeDependencies();
//        this.adminTimeTableHandler = getDependency(IAdminTimeTableHandler.class);
//        this.userManagementHandler = getDependency(IUserManagementHandler.class);
    }

    @Override
    protected void initializeComponents() {
        super.initializeComponents();
    }

//    @FXML
//    protected void onUserStatusButtonClick() {
//        LoggerUtility.buttonInfo("User Status", EUserSessionManager.getCurrentUsername());
//        Stage dialogStage = createCustomDialog(EUserStatusDialogController.class);
//        if (dialogStage != null) {
//            dialogStage.showAndWait();
//        }
//    }

    @FXML
    protected void onUserStatusButtonClick() {
        LoggerUtility.buttonInfo("User Status", EUserSessionManager.getCurrentUsername());
        try {
            Stage dialogStage = createCustomDialog(EUserStatusDialogController.class);
            if (dialogStage != null) {
                dialogStage.showAndWait();
            } else {
                LoggerUtility.error("Failed to create User Status dialog");
                showAlert("Error", "Failed to open User Status dialog. Please try again.");
            }
        } catch (Exception e) {
            LoggerUtility.error("Error opening User Status dialog", e);
            showAlert("Error", "An unexpected error occurred while opening the User Status dialog.");
        }
    }

    @FXML
    protected void onManageUsersClick() {
        LoggerUtility.buttonInfo("Manage Users", getCurrentUsername());
        Stage stage = (Stage) manageUsersButton.getScene().getWindow();
        loadAdminPage(stage, AppPathsFXML.USER_PAGE_LAYOUT, "Manage Users", EUserStatusDialogController.class);
    }


    @FXML
    protected void onViewReportsButtonClick() {
        LoggerUtility.buttonInfo("View Reports", EUserSessionManager.getCurrentUsername());
        Stage stage = (Stage) viewReportsButton.getScene().getWindow();
        loadAdminPage(stage, AppPathsFXML.VIEW_MONTHLY_TIME_LAYOUT, "View Reports", EAdminMonthlyController.class);
    }

    @FXML
    protected void onSettingsButtonClick() {
        LoggerUtility.buttonInfo("Settings", EUserSessionManager.getCurrentUsername());
        Stage stage = (Stage) settingsButton.getScene().getWindow();
        loadAdminPage(stage, AppPathsFXML.SETTINGS_ADMIN_LAYOUT, "Settings", EAdminSettingsController.class);
    }

//    private void setWelcomeMessage() {
//        String welcomeMessage = EUserSessionManager.getSession()
//                .map(session -> "Welcome, Admin " + session.getName() + "!")
//                .orElse("Welcome, Admin!");
//        welcomeLabel.setText(welcomeMessage);
//        LoggerUtility.info("Welcome message set: " + welcomeMessage);
//    }

    @Override
    protected void handleUserDataInitializationFailure() {
        super.handleUserDataInitializationFailure();
        showAlert("Access Denied", "You do not have the required permissions to access this page.");
        redirectToLogin();
    }

    private String getCurrentUsername() {
        return EUserSessionManager.getSession()
                .map(UserSession::getUsername)
                .orElse("Unknown");
    }

    @Override
    protected void performRoleSpecificLogout() {
        super.performRoleSpecificLogout();
        EUserSessionManager.clearSession();
    }
}