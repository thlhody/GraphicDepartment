package cottontex.graphdep.controllers.user;

import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.models.UserSession;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class SettingsUserController extends UserBaseController {

    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        super.setupLogo();
        initializeDependencies();
    }

    @Override
    protected void initializeControllerDependencies() {
        // If there are any SettingsUserController-specific dependencies, initialize them here
    }

    @FXML
    protected void onChangePasswordClick() {
        System.out.println("Attempting to change password for user ID: " + userSession.getUserId()); // Debug print

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

        boolean success = userManagementHandler.changePassword(userSession.getUserId(), currentPassword, newPassword);
        if (success) {
            setStatusMessage("Password changed successfully", true);
            clearFields();
        } else {
            setStatusMessage("Failed to change password. Please check your current password.", false);
        }
    }

    private void setStatusMessage(String message, boolean isSuccess) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: " + (isSuccess ? "green" : "red") + ";");
    }

    private void clearFields() {
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }
    @FXML
    protected void onBackToUserPageClick() {
        Stage stage = (Stage) statusLabel.getScene().getWindow();
        loadPage(stage, AppPathsFXML.USER_PAGE_LAYOUT, "User Page", UserSession.getInstance());
    }

    @Override
    protected Scene getScene() {
        return null;
    }
}
