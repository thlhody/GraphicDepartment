package cottontex.graphdep;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class UserController {

    @FXML
    private Button userTestButton;

    @FXML
    private Button logoutButton;

    @FXML
    protected void onUserTestButtonClick() {
        System.out.println("User Test button clicked!");
    }
    @FXML
    protected void onLogoutButtonClick() {
        try {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("launcher.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 320, 240);
            stage.setScene(scene);
            stage.setTitle("Graphics Department Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

