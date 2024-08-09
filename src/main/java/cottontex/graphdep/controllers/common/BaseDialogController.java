package cottontex.graphdep.controllers.common;

import cottontex.graphdep.constants.AppPathsIMG;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public abstract class BaseDialogController extends BaseController {
    @Setter
    protected Stage dialogStage;
    protected ImageView dialogImage;

    protected void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    public void setDialogImage(ImageView imageView) {
        this.dialogImage = imageView;
        loadDialogImage();
    }

    protected void loadDialogImage() {
        if (dialogImage != null) {
            try {
                Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(AppPathsIMG.DIALOG_BOX_IMAGE)));
                dialogImage.setSmooth(true);
                dialogImage.setImage(image);
            } catch (Exception e) {
                System.err.println("Error loading dialog image: " + e.getMessage());
            }
        }
    }
}