package cottontex.graphdep.controllers.info;

import cottontex.graphdep.constants.AppPathsIMG;
import cottontex.graphdep.controllers.common.EBaseDialogController;
import cottontex.graphdep.utils.DialogUtils;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class EAboutDialogController extends EBaseDialogController {

    @FXML private Label titleLabel;
    @FXML private Label versionLabel;
    @FXML private Label copyrightLabel;
    @FXML private ImageView dialogImage;

    private Stage dialogStage;

    @Override
    protected void initializeDependencies() {
        super.initializeDependencies();
        // No dependencies to initialize
    }

    @Override
    protected void initializeComponents() {
        super.initializeComponents();
        LoggerUtility.info("Initializing AboutDialogController components");
        centerDialog();
    }

    @Override
    protected void performRoleSpecificLogout() {

    }

    @Override
    protected void redirectToLogin() {

    }

    @Override
    protected void refreshContent() {
        // No content to refresh in About dialog
    }

    @Override
    protected void loadDialogImage() {
        if (dialogImage != null) {
            setupDialogImage(AppPathsIMG.DIALOG_BOX_IMAGE);
        } else {
            LoggerUtility.error("dialogImage is null in EAboutDialogController. Check if the fx:id in FXML matches the field name.");
        }
    }

    @FXML
    private void closeAboutDialog() {
        if (dialogStage != null) {
            dialogStage.close();
            LoggerUtility.info("About dialog closed");
        } else {
            LoggerUtility.error("Attempted to close dialog, but dialogStage is null");
        }
    }

    public void setContent(String title, String version, String copyright) {
        titleLabel.setText(title);
        versionLabel.setText(version);
        copyrightLabel.setText(copyright);
        LoggerUtility.info("About dialog content set: Title=" + title + ", Version=" + version);
    }

    @Override
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
        LoggerUtility.info("Dialog stage set for EAboutDialogController");
    }

    private void centerDialog() {
        if (dialogStage != null) {
            DialogUtils.centerDialogOnParent(dialogStage);
        } else {
            LoggerUtility.warn("Dialog stage is null, cannot center the About dialog");
        }
    }
}