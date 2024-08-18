package cottontex.graphdep.controllers.user;

import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.models.managers.EUserSessionManager;
import cottontex.graphdep.services.user.UserSettingsService;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class EUserSettingsController extends EUserBaseController {

    @FXML private ImageView logoImage;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label statusLabel;

    private UserSettingsService userSettingsService;

    @Override
    protected void initializeDependencies() {
        super.initializeDependencies();
        this.userSettingsService = new UserSettingsService(userManagementHandler);
        LoggerUtility.info("Initializing EUserSettingsController dependencies");
    }

    @Override
    protected void initializeComponents() {
        super.initializeComponents();
        LoggerUtility.info("Initializing EUserSettingsController components");
        clearStatusLabel();
    }

    private void clearStatusLabel() {
        statusLabel.setText("");
    }

    @FXML
    protected void onChangePasswordClick() {
        LoggerUtility.buttonInfo("Change Password", EUserSessionManager.getCurrentUsername());

        EUserSessionManager.getSession().ifPresentOrElse(
                session -> {
                    String currentPassword = currentPasswordField.getText();
                    String newPassword = newPasswordField.getText();
                    String confirmPassword = confirmPasswordField.getText();

                    if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                        setStatusMessage("All fields are required", false);
                        return;
                    }

                    if (!newPassword.equals(confirmPassword)) {
                        setStatusMessage("New passwords do not match", false);
                        return;
                    }

                    boolean success = userSettingsService.changePassword(session.getUsername(), currentPassword, newPassword);
                    if (success) {
                        setStatusMessage("Password changed successfully", true);
                        clearFields();
                    } else {
                        setStatusMessage("Failed to change password. Please check your current password.", false);
                    }
                },
                () -> {
                    LoggerUtility.error("Attempt to change password without active session");
                    setStatusMessage("Session expired. Please log in again.", false);
                }
        );
    }

    private void setStatusMessage(String message, boolean isSuccess) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().removeAll("error-label", "success-label");
        statusLabel.getStyleClass().add(isSuccess ? "success-label" : "error-label");
    }

    private void clearFields() {
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }

    @FXML
    private void onBackToUserPageClick() {
        LoggerUtility.info("Navigating back to user dashboard");
        Stage stage = (Stage) logoImage.getScene().getWindow();
        loadPage(stage, AppPathsFXML.USER_PAGE_LAYOUT, "User Dashboard", userSession.orElse(null));
    }

    @Override
    protected void handleUserDataInitializationFailure() {
        super.handleUserDataInitializationFailure();
        showAlert("Access Denied", "You do not have the required permissions to access this page.");
        redirectToLogin();
    }
}