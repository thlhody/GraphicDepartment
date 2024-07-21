package cottontex.graphdep.controllers;

import cottontex.graphdep.utils.LoggerUtility;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public abstract class BaseController {

    protected FXMLLoader loadPage(Stage stage, String fxmlPath, String title) {
        try {
            URL location = getClass().getResource(fxmlPath);
            if (location == null) {
                throw new IOException("FXML file not found: " + fxmlPath);
            }
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
            stage.setScene(scene);
            stage.setTitle(title);
            BaseController controller = fxmlLoader.getController();
            controller.setupLogo();
            return fxmlLoader;
        } catch (IOException e) {
            LoggerUtility.error("Error loading page: " + e.getMessage(), e);
            return null;
        }
    }

    protected void showError(Label errorLabel, String message) {
        errorLabel.setText(message);
        LoggerUtility.error(message);
    }

    protected void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    protected ImageView logoImageView;

    protected void setupLogo() {
        if (logoImageView != null) {
            try {
                Image logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/cottontex/graphdep/images/ct.png")));
                logoImageView.setImage(logo);
            } catch (Exception e) {
                LoggerUtility.error("Error loading logo: " + e.getMessage(), e);
            }
        }
    }
}