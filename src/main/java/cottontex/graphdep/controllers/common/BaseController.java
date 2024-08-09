package cottontex.graphdep.controllers.common;

import cottontex.graphdep.constants.AppPathsCSS;
import cottontex.graphdep.constants.AppPathsIMG;
import cottontex.graphdep.models.UserSession;
import cottontex.graphdep.utils.LoggerUtility;
import cottontex.graphdep.views.BasePage;
import cottontex.graphdep.windowmanagement.WindowManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

@Setter
public abstract class BaseController {

    protected UserSession userSession;
    @FXML
    private ImageView logoImage;
    @FXML
    private ImageView mainImage;

    protected void loadPage(Stage stage, String fxmlPath, String title, UserSession userSession) {
        try {
            URL location = getClass().getResource(fxmlPath);
            if (location == null) {
                LoggerUtility.error("******Error-LoadPage****** FXML file not found: " + fxmlPath);
                return;
            }
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            Object controller = fxmlLoader.getController();
            LoggerUtility.info("Controller loaded: " + (controller != null ? controller.getClass().getName() : "null"));
            if (controller != null) {
                if (controller instanceof BaseController) {
                    ((BaseController) controller).setUserSession(userSession);
                } else {
                    LoggerUtility.error("******Error-LoadPage****** Controller is not an instance of BaseController: " + controller.getClass().getName());
                }
            } else {
                LoggerUtility.error("******Error-LoadPage****** Controller not found for FXML: " + fxmlPath);
            }
        } catch (IOException e) {
            LoggerUtility.error("******Error-LoadPage****** Error loading page: " + e.getMessage(), e);
            showAlert("Error", "Failed to load the requested page.");
        }
    }

    private BaseController getControllerFromStage(Stage stage) {
        Scene scene = stage.getScene();
        if (scene != null) {
            Parent root = scene.getRoot();
            if (root != null) {
                return (BaseController) root.getUserData();
            }
        }
        return null;
    }

    public void setUserSession(UserSession session) {
        this.userSession = session;
        LoggerUtility.info("Setting UserSession in " + this.getClass().getSimpleName() + ": " + session);
    }

    protected void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setupLogo() {
        try {
            if (logoImage == null) {
                LoggerUtility.error("******Error-setupLogo****** LogoImage is null after FXML loading.");
            } else {
                URL logoUrl = getClass().getResource(AppPathsIMG.COTTONTEX_LOGO);
                if (logoUrl != null) {
                    Image logo = new Image(logoUrl.toString());
                    logoImage.setImage(logo);
                    logoImage.setFitWidth(80);  // Set a specific width
                    logoImage.setFitHeight(80); // Set a specific height
                    logoImage.setPreserveRatio(true);
                } else {
                    LoggerUtility.error("******Error-setupLogo******Logo image not found at path: " + AppPathsIMG.COTTONTEX_LOGO);
                }
            }
        } catch (Exception e) {
            LoggerUtility.error("******Error-setupLogo****** Error in setting up the logo: " + e.getMessage(), e);
        }
    }

    public void setupMainImage() {
        try {
            Image mainLogo = new Image(Objects.requireNonNull(getClass().getResourceAsStream(AppPathsIMG.CREATIVE_TIME_TASK_TRACKER)));
            mainImage.setImage(mainLogo);
            mainImage.setFitWidth(400);  // Adjust as needed
            mainImage.setFitHeight(300); // Adjust as needed
            mainImage.setPreserveRatio(true);
        } catch (Exception e) {
            LoggerUtility.error("Error setting up main image: " + e.getMessage(), e);
        }
    }

    protected <T> Stage createCustomDialog(String fxmlPath, String title, Class<T> controllerClass) {
        try {

            // Try loading as a resource stream first
            InputStream fxmlStream = getClass().getResourceAsStream(fxmlPath);

            if (fxmlStream == null) {
                fxmlStream = getClass().getResourceAsStream(fxmlPath);
            }

            if (fxmlStream == null) {
                return null;
            }

            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(fxmlStream);

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.UTILITY);
            dialogStage.setTitle(title);
            dialogStage.setResizable(false);

            Scene scene = new Scene(root);
            WindowManager.updateStage(dialogStage,scene);
            loadCSS(scene);
            loadDialogSpecificCSS(scene, controllerClass);

            T controller = loader.getController();
            if (controller instanceof BaseDialogController baseController) {
                baseController.setDialogStage(dialogStage);

                // Set the dialog image if it exists in the FXML
                ImageView dialogImageView = (ImageView) root.lookup("#dialogImage");
                if (dialogImageView != null) {
                    baseController.setDialogImage(dialogImageView);
                }
            }

            scene.setUserData(controller);

            return dialogStage;
        } catch (IOException e) {
            LoggerUtility.error("Error creating custom dialog", e);
            showAlert("Error", "Failed to create custom dialog.");
            return null;
        }
    }

    private void loadCSS(Scene scene) {
        try {
            String css = Objects.requireNonNull(getClass().getResource(AppPathsCSS.IMAGE_STYLE_A)).toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            LoggerUtility.error(e.getMessage());
        }
    }
    private void loadDialogSpecificCSS(Scene scene, Class<?> controllerClass) {
        try {
            String cssPath = null;
            if (controllerClass.getSimpleName().equals("UserStatusDialogController")) {
                cssPath = AppPathsCSS.USER_STATUS_DIALOG;
            }
            // Add more conditions here for other dialog-specific CSS files

            if (cssPath != null) {
                String css = Objects.requireNonNull(getClass().getResource(cssPath)).toExternalForm();
                scene.getStylesheets().add(css);
            }
        } catch (Exception e) {
            LoggerUtility.error("Error loading dialog-specific CSS: " + e.getMessage());
        }
    }

}
