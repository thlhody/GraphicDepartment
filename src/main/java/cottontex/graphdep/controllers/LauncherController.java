package cottontex.graphdep.controllers;

import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.database.queries.UserLogin;
import cottontex.graphdep.models.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class LauncherController extends BaseController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorMessageLabel;
    @FXML private Button loginButton;
    @FXML private ImageView logoImage;

    private UserLogin userLogin = new UserLogin();

    @FXML
    public void initialize() {
        setupLogo();
    }

    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = userLogin.authenticateUser(username, password);

        if (role != null) {
            int userID = userLogin.getUserID(username);
            UserSession session = UserSession.getInstance();
            session.setUserID(userID);
            session.setUsername(username);
            session.setRole(role);

            Stage stage = (Stage) loginButton.getScene().getWindow();
            String fxmlPath = role.equals("ADMIN") ? AppPathsFXML.ADMIN_PAGE_LAYOUT : AppPathsFXML.USER_PAGE_LAYOUT;
            String title = role.equals("ADMIN") ? "Admin Page" : "User Page";

            FXMLLoader loader = loadPage(stage, fxmlPath, title);

            if (loader != null) {
                BaseController controller = loader.getController();
                controller.initializeUserData();
            } else {
                showError(errorMessageLabel, "Error loading page.");
            }
        } else {
            showError(errorMessageLabel, "Incorrect username or password!");
        }
    }

    @Override
    public void initializeUserData() {
        // This method is not needed for LauncherController
        // but we need to implement it to satisfy the abstract method in BaseController
    }
}