package cottontex.graphdep.views;

import cottontex.graphdep.utils.LoggerUtility;
import cottontex.graphdep.windowmanagement.WindowManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class BasePage extends Application {

    protected abstract String getFxmlPath();
    protected abstract String getTitle();

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(getFxmlPath()));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            WindowManager.initializeStage(primaryStage);
            WindowManager.updateStage(primaryStage, scene);

            primaryStage.setTitle(getTitle());
            primaryStage.show();
        } catch (IOException e) {
            LoggerUtility.error(e.getMessage());
        }
    }

    public void loadNewScene(Stage stage, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            WindowManager.updateStage(stage, scene);

            stage.setTitle(title);
        } catch (IOException e) {
            LoggerUtility.error(e.getMessage());
        }
    }
}