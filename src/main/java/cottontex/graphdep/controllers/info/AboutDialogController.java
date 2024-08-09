package cottontex.graphdep.controllers.info;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import cottontex.graphdep.controllers.common.BaseDialogController;

public class AboutDialogController extends BaseDialogController {
    @FXML private Label titleLabel;
    @FXML private Label versionLabel;
    @FXML private Label copyrightLabel;

    @FXML
    public void initialize() {
        setDialogImage(dialogImage);
    }

    public void setContent(String title, String version, String copyright) {
        if (titleLabel != null) titleLabel.setText(title);
        if (versionLabel != null) versionLabel.setText(version);
        if (copyrightLabel != null) copyrightLabel.setText(copyright);
    }

    @FXML
    private void closeDDialog() {
        super.closeDialog();
    }
}