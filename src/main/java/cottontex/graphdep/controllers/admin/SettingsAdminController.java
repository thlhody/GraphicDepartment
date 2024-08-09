package cottontex.graphdep.controllers.admin;

import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.database.queries.admin.UserManagementHandler;
import cottontex.graphdep.models.UserSession;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SettingsAdminController extends AdminBaseController {

    @FXML private TextField nameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> userComboBox;
    @FXML private Button backButton;

    private UserManagementHandler userManagementHandler = new UserManagementHandler();

    @FXML
    public void initialize() {
        LoggerUtility.info("Initializing SettingsAdminController");
        super.setupLogo();
    }

    private void refreshUserComboBox() {
        userComboBox.setItems(FXCollections.observableArrayList(userManagementHandler.getAllUsernames()));
    }

    @FXML
    protected void onAddUserButtonClick() {
        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "All fields must be filled.");
            return;
        }

        boolean success = userManagementHandler.addUser(name, username, password);
        if (success) {
            showAlert("Success", "User added successfully.");
            clearFields();
            refreshUserComboBox();
        } else {
            showAlert("Error", "Failed to add user. Username may already exist.");
        }
    }

    @FXML
    protected void onResetPasswordButtonClick() {
        String selectedUser = userComboBox.getValue();
        if (selectedUser == null) {
            showAlert("Error", "Please select a user.");
            return;
        }

        boolean success = userManagementHandler.resetPassword(selectedUser, "password123");
        if (success) {
            showAlert("Success", "Password reset successfully.");
        } else {
            showAlert("Error", "Failed to reset password.");
        }
    }

    @FXML
    protected void onDeleteUserButtonClick() {
        String selectedUser = userComboBox.getValue();
        if (selectedUser == null) {
            showAlert("Error", "Please select a user to delete.");
            return;
        }

        boolean success = userManagementHandler.deleteUser(selectedUser);
        if (success) {
            showAlert("Success", "User deleted successfully.");
            refreshUserComboBox();
            userComboBox.setValue(null);  // Clear the selection
        } else {
            showAlert("Error", "Failed to delete user.");
        }
    }


    private void clearFields() {
        nameField.clear();
        usernameField.clear();
        passwordField.clear();
    }
    @FXML
    protected void onBackButtonClick() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        loadPage(stage, AppPathsFXML.ADMIN_PAGE_LAYOUT, "Admin Page", userSession);
    }

    @Override
    protected Scene getScene() {
        return null;
    }
}
