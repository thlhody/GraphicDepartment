package cottontex.graphdep.views;

import cottontex.graphdep.utils.LoggerUtility;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public abstract class BasePage extends Application {
    protected abstract String getFxmlPath();
    protected abstract String getTitle();

    @Override
    public void start(Stage stage) {
        String fxmlPath = getFxmlPath();
        URL fxmlUrl = getClass().getResource(fxmlPath);
        if (fxmlUrl == null) {
            System.err.println("Cannot find FXML file: " + fxmlPath);
            return;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);

        try {
            Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
            stage.setTitle(getTitle());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            LoggerUtility.error(e.getMessage());
        }
    }
}