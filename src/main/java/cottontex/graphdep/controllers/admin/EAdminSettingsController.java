package cottontex.graphdep.controllers.admin;

import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.database.handlers.AddUserResult;
import cottontex.graphdep.database.interfaces.admin.IUserManagementHandler;
import cottontex.graphdep.models.managers.EUserSessionManager;
import cottontex.graphdep.services.admin.AdminSettingsService;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class EAdminSettingsController extends EAdminBaseController {

    private AdminSettingsService adminSettingsService;

    @FXML private TextField nameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField employeeIdField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private ComboBox<String> userComboBox;

    @Override
    protected void initializeDependencies() {
        super.initializeDependencies();
        this.adminSettingsService = new AdminSettingsService(getDependency(IUserManagementHandler.class));
    }

    @Override
    protected void initializeComponents() {
        super.initializeComponents();
        initializeRoleComboBox();
        refreshUserComboBox();
    }

    private void initializeRoleComboBox() {
        roleComboBox.getItems().addAll("USER", "USERADMIN");
        roleComboBox.setValue("USER");
    }

    @FXML
    protected void onAddUserButtonClick() {
        LoggerUtility.buttonInfo("Add User", EUserSessionManager.getCurrentUsername());

        if (!validateInputFields()) {
            return;
        }

        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        int employeeId = Integer.parseInt(employeeIdField.getText().trim());
        String role = roleComboBox.getValue();

        try {
            AddUserResult result = adminSettingsService.addUser(name, username, password, employeeId, role);
            handleAddUserResult(result, username, employeeId);
        } catch (Exception e) {
            LoggerUtility.error("Error adding user", e);
            showAlert("Error", "An unexpected error occurred while adding the user.");
        }
    }

    private boolean validateInputFields() {
        if (nameField.getText().trim().isEmpty() || usernameField.getText().trim().isEmpty() ||
                passwordField.getText().trim().isEmpty() || employeeIdField.getText().trim().isEmpty() ||
                roleComboBox.getValue() == null) {
            showAlert("Error", "All fields must be filled.");
            return false;
        }

        try {
            Integer.parseInt(employeeIdField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Error", "Employee ID must be a valid number.");
            return false;
        }

        return true;
    }

    private void handleAddUserResult(AddUserResult result, String username, Integer employeeId) {
        switch (result) {
            case SUCCESS:
                showAlert("Success", "User added successfully.");
                clearFields();
                refreshUserComboBox();
                break;
            case USERNAME_TAKEN:
                showAlert("Error", "The username '" + username + "' is already taken. Please choose a different username.");
                break;
            case EMPLOYEE_ID_TAKEN:
                showAlert("Error", "The employee ID '" + employeeId + "' is already assigned. Please use a different ID.");
                break;
            case OTHER_ERROR:
                showAlert("Error", "An unexpected error occurred while adding the user. Please try again.");
                break;
        }
    }

    @FXML
    protected void onResetPasswordButtonClick() {
        LoggerUtility.buttonInfo("Reset Password", EUserSessionManager.getCurrentUsername());
        String selectedUser = userComboBox.getValue();
        if (selectedUser == null) {
            showAlert("Error", "Please select a user.");
            return;
        }

        Optional<ButtonType> result = showConfirmationAlert("Confirm Password Reset",
                "Are you sure you want to reset the password for user: " + selectedUser + "?");

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = adminSettingsService.resetPassword(selectedUser, "cottontex123");
                if (success) {
                    showAlert("Success", "Password reset successfully to 'cottontex123'.");
                } else {
                    showAlert("Error", "Failed to reset password.");
                }
            } catch (Exception e) {
                LoggerUtility.error("Error resetting password", e);
                showAlert("Error", "An unexpected error occurred while resetting the password.");
            }
        }
    }

    @FXML
    protected void onDeleteUserButtonClick() {
        LoggerUtility.buttonInfo("Delete User", EUserSessionManager.getCurrentUsername());
        String selectedUser = userComboBox.getValue();
        if (selectedUser == null) {
            showAlert("Error", "Please select a user to delete.");
            return;
        }

        Optional<ButtonType> result = showConfirmationAlert("Confirm User Deletion",
                "Are you sure you want to delete the user: " + selectedUser + "?");

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = adminSettingsService.deleteUser(selectedUser);
                if (success) {
                    showAlert("Success", "User deleted successfully.");
                    refreshUserComboBox();
                    userComboBox.setValue(null);
                } else {
                    showAlert("Error", "Failed to delete user.");
                }
            } catch (Exception e) {
                LoggerUtility.error("Error deleting user", e);
                showAlert("Error", "An unexpected error occurred while deleting the user.");
            }
        }
    }

    @FXML
    protected void onBackButtonClick() {
        LoggerUtility.buttonInfo("Back to Admin Page", EUserSessionManager.getCurrentUsername());
        loadAdminPage((Stage) nameField.getScene().getWindow(), AppPathsFXML.ADMIN_PAGE_LAYOUT, "Admin Page", EAdminController.class);
    }

    private void clearFields() {
        nameField.clear();
        usernameField.clear();
        passwordField.clear();
        employeeIdField.clear();
        roleComboBox.setValue("USER");
    }

    private void refreshUserComboBox() {
        try {
            List<String> usernames = adminSettingsService.getAllUsernames();
            ObservableList<String> observableUsernames = FXCollections.observableArrayList(usernames);
            userComboBox.setItems(observableUsernames);
            LoggerUtility.info("User ComboBox refreshed with " + usernames.size() + " usernames");
        } catch (Exception e) {
            LoggerUtility.error("Error refreshing user ComboBox: " + e.getMessage(), e);
            showAlert("Error", "Failed to refresh user list. Please try again.");
        }
    }

    private Optional<ButtonType> showConfirmationAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        return alert.showAndWait();
    }
}