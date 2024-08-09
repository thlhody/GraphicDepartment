package cottontex.graphdep.windowmanagement;

import javafx.stage.Stage;
import javafx.scene.Scene;

public class WindowManager {
    private static final double DEFAULT_WIDTH = 1000;
    private static final double DEFAULT_HEIGHT = 800;

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

    public static void initializeStage(Stage stage) {
        setStageSize(stage, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        maintainSize(stage);
    }

    public static void updateStage(Stage stage, Scene newScene) {
        resetSize(stage);
        stage.setScene(newScene);
        setStageSize(stage, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        maintainSize(stage);
    }
}