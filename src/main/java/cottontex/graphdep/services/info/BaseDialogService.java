package cottontex.graphdep.services.info;

import cottontex.graphdep.utils.LoggerUtility;
import javafx.scene.image.Image;

import java.io.InputStream;

public class BaseDialogService {

    public Image loadImage(String imagePath) {
        try {
            LoggerUtility.info("Loading image: " + imagePath);
            InputStream imageStream = getClass().getResourceAsStream(imagePath);
            if (imageStream != null) {
                return new Image(imageStream);
            } else {
                LoggerUtility.error("Failed to load image: " + imagePath);
                return null;
            }
        } catch (Exception e) {
            LoggerUtility.error("Error loading image: " + e.getMessage(), e);
            return null;
        }
    }
}