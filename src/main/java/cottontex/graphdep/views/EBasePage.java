package cottontex.graphdep.views;

import cottontex.graphdep.utils.LoggerUtility;
import cottontex.graphdep.windowmanagement.WindowManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class EBasePage extends Application {

    protected abstract String getFxmlPath();
    protected abstract String getTitle();

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(getFxmlPath()));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            WindowManager.initializeMainStage(primaryStage);
            WindowManager.updateMainStage(primaryStage, scene);

            primaryStage.setTitle(getTitle());
            primaryStage.show();
        } catch (IOException e) {
            LoggerUtility.error(e.getMessage());
        }
    }
}