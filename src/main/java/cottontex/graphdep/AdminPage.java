package cottontex.graphdep;

import cottontex.graphdep.loggerUtility.LoggerUtility;
import cottontex.graphdep.controllers.AdminController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminPage extends Application {

    @Override
    public void start(Stage stage) {

        FXMLLoader fxmlLoader = new FXMLLoader(AdminPage.class.getResource("/cottontex/graphdep/adminPage.fxml"));

        try {
            Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
            stage.setTitle("Admin Page");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            LoggerUtility.errorInfo(e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}