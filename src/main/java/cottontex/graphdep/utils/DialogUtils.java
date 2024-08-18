package cottontex.graphdep.utils;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.function.Supplier;

public class DialogUtils {

    public static void setupDynamicSizeDialog(Stage dialogStage, ScrollPane contentScrollPane, Region contentRegion,
                                              double minWidth, double maxWidth,
                                              double minHeight, double maxHeight) {
        // Set size constraints
        dialogStage.setMinWidth(minWidth);
        dialogStage.setMaxWidth(maxWidth);
        dialogStage.setMinHeight(minHeight);
        dialogStage.setMaxHeight(maxHeight);

        // Make sure the dialog is resizable
        dialogStage.setResizable(true);

        // Configure ScrollPane
        contentScrollPane.setFitToWidth(true);
        contentScrollPane.setFitToHeight(false);

        // Bind content region width to ScrollPane width
        contentRegion.prefWidthProperty().bind(contentScrollPane.widthProperty());

        // Ensure the Scene is set and sized correctly
        if (dialogStage.getScene() == null) {
            Scene scene = new Scene(contentScrollPane);
            dialogStage.setScene(scene);
        }
        dialogStage.getScene().getWindow().sizeToScene();
    }

    public static void adjustDialogSize(Stage dialogStage, Supplier<Double> contentWidthSupplier,
                                        Supplier<Double> contentHeightSupplier,
                                        double minWidth, double maxWidth,
                                        double minHeight, double maxHeight,
                                        double padding) {
        if (dialogStage != null && contentWidthSupplier != null && contentHeightSupplier != null) {
            Platform.runLater(() -> {
                double contentWidth = contentWidthSupplier.get() + padding;
                double contentHeight = contentHeightSupplier.get() + padding;

                double newWidth = Math.max(minWidth, Math.min(contentWidth, maxWidth));
                double newHeight = Math.max(minHeight, Math.min(contentHeight, maxHeight));

                dialogStage.setWidth(newWidth);
                dialogStage.setHeight(newHeight);
                dialogStage.centerOnScreen();
            });
        }
    }

    public static void centerDialogOnParent(Stage dialogStage) {
        Platform.runLater(() -> {
            Window parentWindow = dialogStage.getOwner();
            if (parentWindow != null) {
                double centerXPosition = parentWindow.getX() + parentWindow.getWidth() / 2d;
                double centerYPosition = parentWindow.getY() + parentWindow.getHeight() / 2d;

                dialogStage.setOnShown(event -> {
                    dialogStage.setX(centerXPosition - dialogStage.getWidth() / 2d);
                    dialogStage.setY(centerYPosition - dialogStage.getHeight() / 2d);
                });
            } else {
                dialogStage.centerOnScreen();
            }
        });
    }
}