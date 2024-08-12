package cottontex.graphdep.controllers.user;

import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.models.UserSession;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class UserSettingsController extends UserBaseController {

    @FXML
    private PasswordField currentPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        LoggerUtility.initialize(this.getClass(), null);
        super.setupLogo();
        initializeDependencies();
        this.userSession = UserSession.getInstance();
        if (this.userSession != null) {
            boolean initialized = super.initializeUserData();
            LoggerUtility.info("User data initialization result in SettingsUserController: " + initialized);
            if (!initialized) {
                LoggerUtility.error("Failed to initialize user data in SettingsUserController");
            }
        } else {
            LoggerUtility.error("UserSession is null in SettingsUserController initialize method");
        }
    }

    @Override
    protected void initializeWorkState() {
        // No specific work state to initialize for settings
        LoggerUtility.info("Initializing work state for SettingsUserController");
    }

    @Override
    protected void initializeUserComponents() {

    }

    @FXML
    protected void onChangePasswordClick() {
        LoggerUtility.buttonInfo("Change Password", userSession.getUsername());
        LoggerUtility.info("Attempting to change password for user ID: " + userSession.getUserId());

        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            LoggerUtility.warn("Attempt to change password with empty fields");
            setStatusMessage("All fields are required", false);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            LoggerUtility.warn("New passwords do not match in change password attempt");
            setStatusMessage("New passwords do not match", false);
            return;
        }

        boolean success = userManagementHandler.changePassword(userSession.getUsername(), currentPassword, newPassword);
        if (success) {
            LoggerUtility.info("Password changed successfully for user: " + userSession.getUsername());
            setStatusMessage("Password changed successfully", true);
            clearFields();
        } else {
            LoggerUtility.error("Failed to change password for user: " + userSession.getUsername());
            setStatusMessage("Failed to change password. Please check your current password.", false);
        }
    }

    @FXML
    protected void onBackToUserPageClick() {
        LoggerUtility.buttonInfo("Back to User Page", userSession.getUsername());
        Stage stage = (Stage) statusLabel.getScene().getWindow();
        LoggerUtility.switchController(this.getClass(), UserController.class, userSession.getUsername());
        loadPage(stage, AppPathsFXML.USER_PAGE_LAYOUT, "User Page", userSession);
    }

    private void setStatusMessage(String message, boolean isSuccess) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: " + (isSuccess ? "green" : "red") + ";");
        LoggerUtility.info("Status message set: " + message + " (Success: " + isSuccess + ")");
    }

    private void clearFields() {
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
        LoggerUtility.info("Password fields cleared");
    }
}