package cottontex.graphdep.controllers.user;

import cottontex.graphdep.controllers.BaseController;
import cottontex.graphdep.database.queries.UserManagementHandler;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.IOException;

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
//
//    @FXML
//    protected void onBackToUserPageClick() {
//        loadPage((Stage) statusLabel.getScene().getWindow(), "/cottontex/graphdep/fxml/user/UserPageLayout.fxml", "User Page");
//    }

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
    protected void onCloseClick() {
        Stage stage = (Stage) statusLabel.getScene().getWindow();
        stage.close();
    }

    public static void openSettingsWindow(Integer userID) {
        try {
            FXMLLoader loader = new FXMLLoader(SettingsUserController.class.getResource("/cottontex/graphdep/fxml/user/SettingsUserLayout.fxml"));
            Parent root = loader.load();

            SettingsUserController controller = loader.getController();
            controller.setUserID(userID);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("User Settings");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            LoggerUtility.error(e.getMessage());
        }
    }
}