package cottontex.graphdep.controllers;

import cottontex.graphdep.database.DatabaseConnection;
import cottontex.graphdep.database.queries.UserLogin;
import cottontex.graphdep.loggerUtility.LoggerUtility;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LauncherController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorMessageLabel;

    @FXML
    private Button loginButton;

    @FXML
    protected void onLoginButtonClick() {
        UserLogin userLogin = new UserLogin();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = userLogin.authenticateUser(username, password);


        if (role != null) {
            try {
                Integer userID = userLogin.getUserID(username);
                Stage stage = (Stage) loginButton.getScene().getWindow();
                FXMLLoader fxmlLoader;
                if (role.equals("ADMIN")) {
                    fxmlLoader = new FXMLLoader(getClass().getResource("/cottontex/graphdep/adminPage.fxml"));
                    stage.setTitle("Admin Page");
                } else {
                    fxmlLoader = new FXMLLoader(getClass().getResource("/cottontex/graphdep/userPage.fxml"));
                    stage.setTitle("User Page");
                }
                Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
                stage.setScene(scene);

                if (role.equals("ADMIN")) {
                    AdminController controllerAdmin = fxmlLoader.getController();
                    controllerAdmin.setUsername(username);
                    controllerAdmin.setUserID(userID);
                } else {
                    UserController controllerUser = fxmlLoader.getController();
                    controllerUser.setUsername(username);
                    controllerUser.setUserID(userID);
                }

            } catch (IOException e) {
                LoggerUtility.errorInfo(e.getMessage());
                errorMessageLabel.setText("Problem with the page!");
            }
        } else {
            LoggerUtility.infoTest("Incorrect username or password!");
            errorMessageLabel.setText("Incorrect username or password!");
        }
    }
}
