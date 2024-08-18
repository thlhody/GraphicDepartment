package cottontex.graphdep.controllers.common;

import cottontex.graphdep.services.info.BaseDialogService;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Optional;

public abstract class EBaseDialogController extends EBaseController {

    protected Stage dialogStage;
    protected BaseDialogService baseDialogService;

    @FXML
    protected Button refreshButton;
    @FXML
    protected ImageView refreshIcon;
    @FXML
    protected ImageView dialogImage;

    @Override
    protected void initializeDependencies() {
        this.baseDialogService = new BaseDialogService();
    }

    @Override
    protected void initializeComponents() {
        setupRefreshButton();
        loadDialogImage();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        LoggerUtility.info("Dialog stage set for " + this.getClass().getSimpleName());
    }

    @FXML
    protected void closeDialog() {
        Optional.ofNullable(dialogStage).ifPresentOrElse(
                stage -> {
                    stage.close();
                    LoggerUtility.info("Closing dialog: " + stage.getTitle());
                },
                () -> LoggerUtility.warn("Attempted to close dialog, but dialogStage is null")
        );
        dispose();
    }

    @Override
    public void setupLogo() {
        if (logoImage != null) {
            super.setupLogo();
        } else {
            LoggerUtility.info("LogoImage not present in this dialog");
        }
    }

    protected void setupDialogImage(String imagePath) {
        try {
            if (dialogImage != null) {
                Image image = baseDialogService.loadImage(imagePath);
                if (image != null) {
                    dialogImage.setImage(image);
                    dialogImage.setFitWidth(100);  // Adjust as needed
                    dialogImage.setFitHeight(100); // Adjust as needed
                    dialogImage.setPreserveRatio(true);
                    LoggerUtility.info("Dialog image set successfully: " + imagePath);
                } else {
                    LoggerUtility.error("Failed to load image: " + imagePath);
                }
            } else {
                LoggerUtility.error("DialogImage is null when setting up dialog image");
            }
        } catch (Exception e) {
            LoggerUtility.error("Error setting up dialog image: " + e.getMessage(), e);
        }
    }

    protected void setupRefreshButton() {
        if (refreshButton != null) {
            refreshButton.setOnAction(e -> refreshContent());
            refreshButton.setTooltip(new Tooltip("Refresh"));
            LoggerUtility.info("Refresh button set up successfully");
        } else {
            LoggerUtility.info("Refresh button not present in this dialog");
        }
    }

    protected void setRefreshButtonImage(String imagePath) {
        if (refreshIcon != null) {
            Image refreshImage = baseDialogService.loadImage(imagePath);
            if (refreshImage != null) {
                refreshIcon.setImage(refreshImage);
                LoggerUtility.info("Refresh icon set successfully");
            } else {
                LoggerUtility.error("Failed to load refresh icon image");
            }
        } else {
            LoggerUtility.error("Refresh icon ImageView is null in setRefreshButtonImage method");
        }
    }

    protected abstract void refreshContent();

    protected abstract void loadDialogImage();

    @Override
    protected boolean requiresUserSession() {
        return false; // Most dialogs don't require a user session, override if needed
    }

    public void dispose() {
        // Clean up resources, unregister listeners, etc.
    }
}