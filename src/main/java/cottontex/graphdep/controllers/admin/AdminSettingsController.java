package cottontex.graphdep.controllers.admin;

import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.database.interfaces.admin.IUserManagementHandler;
import cottontex.graphdep.database.handlers.AddUserResult;
import cottontex.graphdep.models.managers.UserSessionManager;
import cottontex.graphdep.services.admin.AdminSettingsService;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AdminSettingsController extends AdminBaseController {

    private AdminSettingsService adminSettingsService;

    @FXML
    private TextField nameField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField employeeIdField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private ComboBox<String> userComboBox;
    @FXML
    private Button backButton;

    @FXML
    @Override
    public void initialize() {
        super.initialize();
        initializeRoleComboBox();
        refreshUserComboBox();
    }

    @Override
    protected void initializeDependencies() {
        super.initializeDependencies();
        adminSettingsService = new AdminSettingsService(getHandler(IUserManagementHandler.class));
    }

    private void initializeRoleComboBox() {
        roleComboBox.setItems(FXCollections.observableArrayList("USER", "USERADMIN"));
        roleComboBox.setValue("USER");
    }

    @FXML
    protected void onAddUserButtonClick() {
        LoggerUtility.buttonInfo("Add User", UserSessionManager.getSession().getUsername());

        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String employeeIdStr = employeeIdField.getText().trim();
        String role = roleComboBox.getValue();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || employeeIdStr.isEmpty() || role == null) {
            showAlert("Error", "All fields must be filled.");
            return;
        }

        Integer employeeId;
        try {
            employeeId = Integer.parseInt(employeeIdStr);
        } catch (NumberFormatException e) {
            showAlert("Error", "Employee ID must be a valid number.");
            return;
        }

        AddUserResult result = adminSettingsService.addUser(name, username, password, employeeId, role);

        handleAddUserResult(result, username, employeeId);
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
        LoggerUtility.buttonInfo("Reset Password", UserSessionManager.getSession().getUsername());
        String selectedUser = userComboBox.getValue();
        if (selectedUser == null) {
            LoggerUtility.error("No user selected for password reset");
            showAlert("Error", "Please select a user.");
            return;
        }

        boolean success = adminSettingsService.resetPassword(selectedUser, "cottontex123");
        if (success) {
            LoggerUtility.info("Password reset successfully for user: " + selectedUser);
            showAlert("Success", "Password reset successfully (cottontex123).");
        } else {
            LoggerUtility.error("Failed to reset password for user: " + selectedUser);
            showAlert("Error", "Failed to reset password.");
        }
    }

    @FXML
    protected void onDeleteUserButtonClick() {
        LoggerUtility.buttonInfo("Delete User", UserSessionManager.getSession().getUsername());
        String selectedUser = userComboBox.getValue();
        if (selectedUser == null) {
            LoggerUtility.error("No user selected for deletion");
            showAlert("Error", "Please select a user to delete.");
            return;
        }

        boolean success = adminSettingsService.deleteUser(selectedUser);
        if (success) {
            LoggerUtility.info("User deleted successfully: " + selectedUser);
            showAlert("Success", "User deleted successfully.");
            refreshUserComboBox();
            userComboBox.setValue(null);  // Clear the selection
        } else {
            LoggerUtility.error("Failed to delete user: " + selectedUser);
            showAlert("Error", "Failed to delete user.");
        }
    }

    @FXML
    protected void onBackButtonClick() {
        LoggerUtility.buttonInfo("Back to Admin Page", UserSessionManager.getSession().getUsername());
        loadAdminPage((Stage) backButton.getScene().getWindow(), AppPathsFXML.ADMIN_PAGE_LAYOUT, "Admin Page", AdminController.class);
    }

    private void clearFields() {
        nameField.clear();
        usernameField.clear();
        passwordField.clear();
        employeeIdField.clear();
        roleComboBox.setValue("USER");
    }

    private void refreshUserComboBox() {
        userComboBox.setItems(FXCollections.observableArrayList(adminSettingsService.getAllUsernames()));
    }
}