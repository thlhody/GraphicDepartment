package cottontex.graphdep;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
    private Button loginButton;

    private DatabaseConnection databaseConnection;

    @FXML
    protected void initialize() {
        databaseConnection = new DatabaseConnection();
    }

    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = databaseConnection.authenticateUser(username, password);

        if (role != null) {
            try {
                Stage stage = (Stage) loginButton.getScene().getWindow();
                if(role.equals("ADMIN")) {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("adminPage.fxml"));
                    stage.setTitle("Admin Page");
                    stage.setScene(new Scene(fxmlLoader.load(), 320, 240));
                    databaseConnection.closeConnection();
                } else {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("userPage.fxml"));
                    stage.setTitle("User Page");
                    stage.setScene(new Scene(fxmlLoader.load(), 320, 240));
                    databaseConnection.closeConnection();
                }
            } catch (IOException e) {
                LoggerUtility.errorInfo(e.getMessage());
            }
        } else {
            LoggerUtility.infoTest("Incorrect username or password!");
            databaseConnection.closeConnection();
        }
    }
}
