package cottontex.graphdep.controllers;

import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.controllers.common.BaseController;
import cottontex.graphdep.controllers.info.AboutDialogController;
import cottontex.graphdep.database.interfaces.IUserLogin;
import cottontex.graphdep.models.UserSession;
import cottontex.graphdep.models.managers.UserSessionManager;
import cottontex.graphdep.utils.LoggerUtility;
import cottontex.graphdep.windowmanagement.WindowManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class LauncherController extends BaseController {

    private IUserLogin userLogin;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ImageView logoImage;
    @FXML
    private Button aboutButton;

    @FXML
    public void initialize() {
        LoggerUtility.initialize(this.getClass(), "Initializing LauncherController");
        super.initialize();
        setupMainImage();
        LoggerUtility.info("LauncherController initialized successfully");
    }

    @Override
    protected boolean requiresUserSession() {
        return false; // LauncherController doesn't require a user session
    }

    @Override
    protected void initializeDependencies() {
        LoggerUtility.initialize(this.getClass(), "Initializing dependencies");
        userLogin = getHandler(IUserLogin.class);
        if (userLogin != null) {
            LoggerUtility.info("UserLogin dependency initialized successfully");
        } else {
            LoggerUtility.error("Failed to initialize UserLogin dependency");
        }
    }
    @Override
    protected boolean initializeRoleSpecificData() {
        // LauncherController doesn't need to initialize user data
        return true;
    }

    @Override
    protected boolean initializeUserData() {
        // Do nothing as LauncherController doesn't need user data
        return true;
    }

    @Override
    protected void performRoleSpecificLogout() {
        // No specific logout actions needed for LauncherController
    }

    @Override
    protected void redirectToLogin() {
        // LauncherController is already the login screen, so we don't need to redirect
        LoggerUtility.info("Already on login screen, no redirection needed");
    }


    @FXML
    protected void onLoginButtonClick() {
        LoggerUtility.buttonInfo("Login", "Attempt");
        if (userLogin == null) {
            LoggerUtility.error("UserLogin is not initialized");
            showAlert("Error", "Application is not properly initialized. Please restart.");
            return;
        }

        String username = usernameField.getText();
        String password = passwordField.getText();
        LoggerUtility.info("Attempting to authenticate user: " + username);
        String role = userLogin.authenticateUser(username, password);

        if (role != null) {
            LoggerUtility.info("User authenticated successfully: " + username + ", Role: " + role);
            Integer userId = userLogin.getUserID(username);
            Integer employeeId = userLogin.getEmployeeId(username);
            String name = userLogin.getName(userId);

            if (userId == null) {
                LoggerUtility.error("Failed to retrieve user ID for: " + username);
                showAlert("Login Error", "Failed to retrieve user information.");
                return;
            }

            UserSessionManager.initializeSession(userId, username, name, role, employeeId);

            UserSession userSession = UserSessionManager.getSession();
            LoggerUtility.info("UserSession created: " + userSession + ", Role: " + userSession.getRole());



            Stage stage = (Stage) usernameField.getScene().getWindow();
            String fxmlPath = "ADMIN".equals(role) ? AppPathsFXML.ADMIN_PAGE_LAYOUT : AppPathsFXML.USER_PAGE_LAYOUT;
            LoggerUtility.info("Loading FXML: " + fxmlPath + " for role: " + role);

            loadPage(stage, fxmlPath, "ADMIN".equals(role) ? "Admin Page" : "User Page", userSession);
            LoggerUtility.switchController(this.getClass(), ("ADMIN".equals(role) ? "AdminBaseController" : "UserBaseController").getClass(), username);
        } else {
            LoggerUtility.warn("Failed login attempt for user: " + username);
            showAlert("Login Error", "Invalid username or password.");
        }
    }

    @FXML
    protected void onAboutButtonClick() {
        LoggerUtility.buttonInfo("About", "Click");
        try {
            LoggerUtility.info("Creating About dialog");
            Stage aboutStage = createCustomDialog(AppPathsFXML.ABOUT_DIALOG, "About", AboutDialogController.class);
            if (aboutStage != null) {
                AboutDialogController controller = (AboutDialogController) aboutStage.getScene().getUserData();
                if (controller != null) {
                    controller.setContent("Creative Time And Task Tracker", "Version 1.0", "© 2024 thlhody");
                    LoggerUtility.info("About dialog content set successfully");

                    // Ensure dialog is set up correctly
                    WindowManager.initializeDialogStage(aboutStage);
                    aboutStage.setResizable(false);
                    aboutStage.sizeToScene();

                    // Center the dialog on the parent window
                    Stage parentStage = (Stage) aboutButton.getScene().getWindow();
                    aboutStage.setX(parentStage.getX() + (parentStage.getWidth() - aboutStage.getWidth()) / 2);
                    aboutStage.setY(parentStage.getY() + (parentStage.getHeight() - aboutStage.getHeight()) / 2);

                    LoggerUtility.info("Displaying About dialog");
                    aboutStage.showAndWait();
                    LoggerUtility.info("About dialog closed");
                } else {
                    LoggerUtility.error("AboutDialogController is null");
                    showAlert("Error", "Failed to initialize About dialog controller.");
                }
            } else {
                LoggerUtility.error("Failed to create About dialog stage");
                showAlert("Error", "Failed to create About dialog.");
            }
        } catch (Exception e) {
            LoggerUtility.error("Error displaying About dialog: " + e.getMessage(), e);
            showAlert("Error", "An error occurred while displaying the About dialog.");
        }
    }
}
