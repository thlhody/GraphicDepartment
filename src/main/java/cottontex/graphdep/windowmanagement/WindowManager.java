package cottontex.graphdep.windowmanagement;

import cottontex.graphdep.controllers.common.EBaseController;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.stage.Screen;

import java.io.IOException;

public class WindowManager {
    private static final double DEFAULT_MAIN_WIDTH = 1000;
    private static final double DEFAULT_MAIN_HEIGHT = 800;
    private static final double DEFAULT_DIALOG_WIDTH = 400;
    private static final double DEFAULT_DIALOG_HEIGHT = 300;

    public static void setStageSize(Stage stage, double width, double height) {
        stage.setWidth(width);
        stage.setHeight(height);
    }

    public static void maintainSize(Stage stage) {
        double currentWidth = stage.getWidth();
        double currentHeight = stage.getHeight();
        stage.setMinWidth(currentWidth);
        stage.setMinHeight(currentHeight);
        stage.setMaxWidth(currentWidth);
        stage.setMaxHeight(currentHeight);
    }

    public static void resetSize(Stage stage) {
        stage.setMinWidth(0);
        stage.setMinHeight(0);
        stage.setMaxWidth(Double.MAX_VALUE);
        stage.setMaxHeight(Double.MAX_VALUE);
    }

    // Keep the original methods for backwards compatibility
    public static void initializeStage(Stage stage) {
        initializeMainStage(stage);
    }

    public static void updateStage(Stage stage, Scene newScene) {
        updateMainStage(stage, newScene);
    }

    // New methods for main stage
    public static void initializeMainStage(Stage stage) {
        setStageSize(stage, DEFAULT_MAIN_WIDTH, DEFAULT_MAIN_HEIGHT);
        maintainSize(stage);
    }

    public static void updateMainStage(Stage stage, Scene newScene) {
        resetSize(stage);
        stage.setScene(newScene);
        setStageSize(stage, DEFAULT_MAIN_WIDTH, DEFAULT_MAIN_HEIGHT);
        maintainSize(stage);
    }

    // Updated methods for dialog stage
    public static void initializeDialogStage(Stage stage) {
        setDialogSize(stage, DEFAULT_DIALOG_WIDTH, DEFAULT_DIALOG_HEIGHT);
    }

    public static void updateDialogStage(Stage stage, Scene newScene) {
        resetSize(stage);
        stage.setScene(newScene);
        setDialogSize(stage, DEFAULT_DIALOG_WIDTH, DEFAULT_DIALOG_HEIGHT);
    }

    public static void setDialogSize(Stage stage, double minWidth, double minHeight) {
        stage.setMinWidth(minWidth);
        stage.setMinHeight(minHeight);

        double maxWidth = Screen.getPrimary().getVisualBounds().getWidth() * 0.8;
        double maxHeight = Screen.getPrimary().getVisualBounds().getHeight() * 0.8;

        stage.setMaxWidth(maxWidth);
        stage.setMaxHeight(maxHeight);

        stage.sizeToScene();
    }

    public static void loadNewScene(Stage stage, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(WindowManager.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            EBaseController controller = loader.getController();
            if (controller != null) {
                scene.setUserData(controller);
            } else {
                LoggerUtility.error("Controller is null after loading FXML: " + fxmlPath);
            }

            updateMainStage(stage, scene);

            stage.setTitle(title);
        } catch (IOException e) {
            LoggerUtility.error("Error loading new scene: " + e.getMessage(), e);
        }
    }
}