package cottontex.graphdep.controllers;

import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.controllers.common.BaseController;
import cottontex.graphdep.controllers.info.AboutDialogController;
import cottontex.graphdep.database.queries.UserLogin;
import cottontex.graphdep.models.UserSession;
import cottontex.graphdep.utils.DependencyFactory;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class LauncherController extends BaseController {

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
        super.setupLogo();
        setupMainImage();
    }

    @FXML
    protected void onLoginButtonClick() {
        DependencyFactory dependencyFactory = DependencyFactory.getInstance();
        UserLogin userLogin = dependencyFactory.createUserLogin();
        LoggerUtility.info("UserLogin: "+ userLogin);
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = userLogin.authenticateUser(username, password);

        if (role != null) {
            LoggerUtility.info("User authenticated successfully: " + username + ", Role: " + role);
            Integer userId = userLogin.getUserID(username);
            if (userId == null) {
                LoggerUtility.error("Failed to retrieve user ID for: " + username);
                showAlert("Login Error", "Failed to retrieve user information.");
                return;
            }

            // Initialize and set UserSession with complete information
            UserSession userSession = new UserSession();
            userSession.setUserId(userId);
            userSession.setUsername(username);
            userSession.setRole(role);
            UserSession.setInstance(userSession);

            LoggerUtility.info("UserSession created: " + userSession + ", Role: " + userSession.getRole());

            Stage stage = (Stage) usernameField.getScene().getWindow();
            String fxmlPath = "ADMIN".equals(role) ? AppPathsFXML.ADMIN_PAGE_LAYOUT : AppPathsFXML.USER_PAGE_LAYOUT;
            LoggerUtility.info("Loading FXML: " + fxmlPath + " for role: " + role);

            loadPage(stage, fxmlPath, "ADMIN".equals(role) ? "Admin Page" : "User Page", UserSession.getInstance());
        } else {
            showAlert("Login Error", "Invalid username or password.");
        }
    }

    @FXML
    protected void onAboutButtonClick() {
        Stage aboutStage = createCustomDialog(AppPathsFXML.ABOUT_DIALOG, "About", AboutDialogController.class);
        if (aboutStage != null) {
            AboutDialogController controller = (AboutDialogController) aboutStage.getScene().getUserData();
            controller.setContent("Creative Time And Task Tracker", "Version 1.0", "© 2024 thlhody");
            aboutStage.showAndWait();
        } else {
            LoggerUtility.error("Failed to create About dialog");
        }
    }
}
