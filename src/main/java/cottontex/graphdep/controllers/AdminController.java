package cottontex.graphdep.controllers;

import cottontex.graphdep.loggerUtility.LoggerUtility;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.IOException;

public class AdminController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button adminTestButton;

    @FXML
    private Button logoutButton;

    private String username;

    @Setter
    private Integer userID;

    @FXML
    protected void onAdminTestButtonClick() {
        System.out.println("Admin Test button clicked!");
    }

    @FXML
    protected void onLogoutAdminButtonClick() {
        try {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/cottontex/graphdep/launcher.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
            stage.setScene(scene);
            stage.setTitle("Graphics Department Login");
        } catch (IOException e) {
            LoggerUtility.errorInfo(e.getMessage());
        }
    }
    public void setUsername(String username){
        welcomeLabel.setText("Welcome, "+ username +"!");
    }

}

