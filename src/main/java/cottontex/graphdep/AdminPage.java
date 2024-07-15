package cottontex.graphdep;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminPage extends Application {

    @Override
    public void start(Stage stage) {

        FXMLLoader fxmlLoader = new FXMLLoader(AdminPage.class.getResource("adminPage.fxml"));

        try {
            Scene scene = new Scene(fxmlLoader.load(), 320, 240);
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