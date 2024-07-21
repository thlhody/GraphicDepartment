package cottontex.graphdep.controllers;

import cottontex.graphdep.controllers.admin.AdminController;
import cottontex.graphdep.controllers.user.UserController;
import cottontex.graphdep.database.queries.UserLogin;
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
    @FXML private ImageView logoImageView;


    private UserLogin userLogin = new UserLogin();

    @FXML public void initialize(){
        setupLogo();
    }

    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = userLogin.authenticateUser(username, password);

        if (role != null) {
            int userID = userLogin.getUserID(username);
            Stage stage = (Stage) loginButton.getScene().getWindow();
            String fxmlPath = role.equals("ADMIN") ? "/cottontex/graphdep/fxml/admin/AdminPageLayout.fxml" : "/cottontex/graphdep/fxml/user/UserPageLayout.fxml";
            String title = role.equals("ADMIN") ? "Admin Page" : "User Page";

            FXMLLoader loader = loadPage(stage, fxmlPath, title);

            if (loader != null) {
                initializeController(loader, role, username, userID);
            } else {
                showError(errorMessageLabel, "Error loading page.");
            }
        } else {
            showError(errorMessageLabel, "Incorrect username or password!");
        }
    }

    private void initializeController(FXMLLoader loader, String role, String username, int userID) {
        if (role.equals("ADMIN")) {
            AdminController controller = loader.getController();
            controller.setUsername(username);
            controller.setUserID(userID);
        } else {
            UserController controller = loader.getController();
            controller.setUserInfo(userID, username);
        }
    }
}