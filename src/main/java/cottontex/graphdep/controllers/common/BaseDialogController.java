package cottontex.graphdep.controllers.common;

import cottontex.graphdep.services.info.BaseDialogService;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;

@Getter
@Setter
public abstract class BaseDialogController extends BaseController {

    protected Stage dialogStage;
    protected BaseDialogService baseDialogService;


    @FXML
    protected Button refreshButton;
    @FXML
    protected ImageView refreshIcon;

    @Override
    public void initialize() {
        super.initialize();
        LoggerUtility.initialize(this.getClass(), "Initializing BaseDialogController");
        baseDialogService = new BaseDialogService();
        initializeCommonElements();
    }

    protected void initializeCommonElements() {
        setupRefreshButton();
        loadDialogImage();
    }

    @Override
    protected abstract void initializeDependencies();

    public void setDialogStage(Stage dialogStage) {
        LoggerUtility.info("Setting dialog stage for " + this.getClass().getSimpleName());
        this.dialogStage = dialogStage;
    }

    protected void closeDialog() {
        if (dialogStage != null) {
            LoggerUtility.info("Closing dialog: " + dialogStage.getTitle());
            dialogStage.close();
        } else {
            LoggerUtility.warn("Attempted to close dialog, but dialogStage is null");
        }
    }

    @Override
    public void setupLogo() {
        if (logoImage != null) {
            super.setupLogo();
        } else {
            LoggerUtility.info("LogoImage not present in this dialog");
        }
    }

    protected void setupDialogImage(ImageView imageView, String imagePath) {
        try {
            LoggerUtility.info("Setting up dialog image: " + imagePath);
            if (imageView != null) {
                Image image = baseDialogService.loadImage(imagePath);
                if (image != null) {
                    imageView.setImage(image);
                    imageView.setFitWidth(100);  // Adjust as needed
                    imageView.setFitHeight(100); // Adjust as needed
                    imageView.setPreserveRatio(true);
                }
            } else {
                LoggerUtility.error("ImageView is null when setting up dialog image");
            }
        } catch (Exception e) {
            LoggerUtility.error("Error setting up dialog image: " + e.getMessage(), e);
        }
    }

    protected void setupRefreshButton() {
        if (refreshButton != null) {
            refreshButton.setOnAction(e -> refreshContent());
            refreshButton.setTooltip(new Tooltip("Refresh"));
        } else {
            LoggerUtility.info("Refresh button not present in this dialog");
        }
    }

    protected void setRefreshButtonImage(String imagePath) {
        if (refreshIcon != null) {
            InputStream imageStream = getClass().getResourceAsStream(imagePath);
            if (imageStream != null) {
                Image refreshImage = new Image(imageStream);
                refreshIcon.setImage(refreshImage);
            } else {
                LoggerUtility.error("Failed to load refresh icon image");
            }
        } else {
            LoggerUtility.error("Refresh icon ImageView is null in setRefreshButtonImage method");
        }
    }

    protected abstract void refreshContent();

    protected abstract void loadDialogImage();
}