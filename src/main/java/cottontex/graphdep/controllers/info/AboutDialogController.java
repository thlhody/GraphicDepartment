package cottontex.graphdep.controllers.info;

import cottontex.graphdep.constants.AppPathsIMG;
import cottontex.graphdep.controllers.common.BaseDialogController;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class AboutDialogController extends BaseDialogController {

    @FXML
    private Label titleLabel;
    @FXML
    private Label versionLabel;
    @FXML
    private Label copyrightLabel;
    @FXML
    private ImageView dialogImage;

    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    protected void initializeDependencies() {
        // No dependencies to initialize
    }

    @Override
    protected boolean initializeRoleSpecificData() {
        return false;
    }

    @Override
    protected void performRoleSpecificLogout() {

    }

    @Override
    protected void redirectToLogin() {
        // LauncherController is already the login screen, so we don't need to redirect
        LoggerUtility.info("Already on login screen, no redirection needed");
    }

    @Override
    protected void refreshContent() {
        // No content to refresh in About dialog
    }

    @Override
    protected void loadDialogImage() {
        if (dialogImage != null) {
            setupDialogImage(dialogImage, AppPathsIMG.DIALOG_BOX_IMAGE);
        } else {
            LoggerUtility.error("dialogImage is null in AboutDialogController. Check if the fx:id in FXML matches the field name.");
        }
    }

    @FXML
    private void closeAboutDialog() {
        super.closeDialog();
    }

    public void setContent(String title, String version, String copyright) {
        titleLabel.setText(title);
        versionLabel.setText(version);
        copyrightLabel.setText(copyright);
    }
}