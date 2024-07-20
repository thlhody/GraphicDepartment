package cottontex.graphdep.controllers;

import cottontex.graphdep.database.queries.UserManagementHandler;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import lombok.Setter;

public class SettingsUserController extends BaseController {

    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label statusLabel;

    private UserManagementHandler userManagementHandler = new UserManagementHandler();

    @Setter private Integer userID; // You need to set this when loading the page

    @FXML
    protected void onChangePasswordClick() {
        System.out.println("Attempting to change password for user ID: " + userID); // Debug print

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

        boolean success = userManagementHandler.changePassword(userID, currentPassword, newPassword);
        if (success) {
            setStatusMessage("Password changed successfully", true);
            clearFields();
        } else {
            setStatusMessage("Failed to change password. Please check your current password.", false);
        }
    }

    @FXML
    protected void onBackToUserPageClick() {
        loadPage((Stage) statusLabel.getScene().getWindow(), "/cottontex/graphdep/fxml/UserPageLayout.fxml", "User Page");
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
}