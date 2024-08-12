package cottontex.graphdep.controllers.common;

import cottontex.graphdep.constants.AppPathsCSS;
import cottontex.graphdep.constants.AppPathsIMG;
import cottontex.graphdep.database.interfaces.IUserLogin;
import cottontex.graphdep.models.UserSession;
import cottontex.graphdep.models.managers.UserSessionManager;
import cottontex.graphdep.utils.DependencyFactory;
import cottontex.graphdep.utils.LoggerUtility;
import cottontex.graphdep.windowmanagement.WindowManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Setter;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

@Setter
public abstract class BaseController {

    protected final DependencyFactory factory = DependencyFactory.getInstance();
    protected UserSession userSession;
    protected IUserLogin userLogin;

    @FXML
    protected ImageView logoImage;
    @FXML
    protected ImageView mainImage;
    @FXML
    protected Button logoutButton;
    @FXML
    protected Button backButton;

    @FXML
    public void initialize() {
        LoggerUtility.initialize(this.getClass(), null);
        setupLogo();
        initializeDependencies();
        if (requiresUserSession()) {
            if (userSession == null) {
                LoggerUtility.warn("UserSession is null in BaseController initialize method. Attempting to retrieve from UserSessionManager.");
                userSession = UserSessionManager.getSession();
            }
            if (userSession != null) {
                initializeUserData();
            } else {
                LoggerUtility.error("UserSession is still null after attempt to retrieve from UserSessionManager.");
            }
        } else {
            LoggerUtility.info("UserSession not required for this controller.");
        }
    }

    protected boolean requiresUserSession() {
        return true; // Override this in controllers that don't require a session
    }

    protected void initializeDependencies() {
        userLogin = factory.get(IUserLogin.class);  // Retrieve IUserLogin from DependencyFactory
        if (userLogin == null) {
            LoggerUtility.error("IUserLogin failed to initialize in BaseController");
        } else {
            LoggerUtility.info("IUserLogin initialized successfully in BaseController");
        }
    }

    public void setUserSession(UserSession session) {
        LoggerUtility.initialize(this.getClass(), "Setting UserSession: " + session);
        this.userSession = session;
        UserSessionManager.setSession(session);
        if (!initializeUserData()) {
            LoggerUtility.error("Failed to initialize user data.");
            Platform.runLater(() -> {
                showAlert("Access Denied", "You do not have the required permissions to access this page.");
            });
        }
    }

    protected boolean initializeUserData() {
        if (userSession == null) {
            LoggerUtility.error("Invalid user session in BaseController.initializeUserData(). UserSession is null.");
            return false;
        }
        LoggerUtility.info("Initializing user data with session: " + userSession);
        return initializeRoleSpecificData();
    }
    protected abstract boolean initializeRoleSpecificData();


    protected <T> T getHandler(Class<T> type) {
        try {
            LoggerUtility.info("Getting handler for " + type.getSimpleName());
            return factory.get(type);
        } catch (IllegalArgumentException e) {
            LoggerUtility.error("Failed to initialize " + type.getSimpleName(), e);
            showAlert("Error", "Failed to initialize application. Please restart.");
            return null;
        }
    }

    protected void loadPage(Stage stage, String fxmlPath, String title, UserSession userSession) {
        try {
            LoggerUtility.info("Loading page: " + fxmlPath + " with title: " + title);
            URL location = getClass().getResource(fxmlPath);
            if (location == null) {
                LoggerUtility.error("FXML file not found: " + fxmlPath);
                return;
            }
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            Object controller = fxmlLoader.getController();
            LoggerUtility.info("Controller loaded: " + (controller != null ? controller.getClass().getName() : "null"));
            if (controller instanceof BaseController) {
                ((BaseController) controller).setUserSession(userSession);
            } else {
                LoggerUtility.error("Controller is not an instance of BaseController or is null");
            }
        } catch (IOException e) {
            LoggerUtility.error("Error loading page: " + e.getMessage(), e);
            showAlert("Error", "Failed to load the requested page.");
        }
    }

    protected void showAlert(String title, String message) {
        LoggerUtility.info("Showing alert: " + title + " - " + message);
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public void setupLogo() {
        try {
            LoggerUtility.info("Setting up logo");
            if (logoImage != null) {
                URL logoUrl = getClass().getResource(AppPathsIMG.COTTONTEX_LOGO);
                if (logoUrl != null) {
                    Image logo = new Image(logoUrl.toString());
                    logoImage.setImage(logo);
                    logoImage.setFitWidth(80);
                    logoImage.setFitHeight(80);
                    logoImage.setPreserveRatio(true);
                    LoggerUtility.info("Logo set up successfully");
                } else {
                    LoggerUtility.error("Logo image not found at path: " + AppPathsIMG.COTTONTEX_LOGO);
                }
            } else {
                LoggerUtility.error("LogoImage is null after FXML loading.");
            }
        } catch (Exception e) {
            LoggerUtility.error("Error in setting up the logo: " + e.getMessage(), e);
        }
    }

    public void setupMainImage() {
        try {
            LoggerUtility.info("Setting up main image");
            Image mainLogo = new Image(Objects.requireNonNull(getClass().getResourceAsStream(AppPathsIMG.CREATIVE_TIME_TASK_TRACKER)));
            mainImage.setImage(mainLogo);
            mainImage.setFitWidth(400);
            mainImage.setFitHeight(300);
            mainImage.setPreserveRatio(true);
            LoggerUtility.info("Main image set up successfully");
        } catch (Exception e) {
            LoggerUtility.error("Error setting up main image: " + e.getMessage(), e);
        }
    }

    protected <T> Stage createCustomDialog(String fxmlPath, String title, Class<T> controllerClass) {
        try {
            LoggerUtility.info("Creating custom dialog: " + title + " with FXML: " + fxmlPath);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.UTILITY);
            dialogStage.setTitle(title);
            dialogStage.setResizable(false);

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            loadDialogSpecificCSS(scene, controllerClass);

            WindowManager.initializeDialogStage(dialogStage);

            T controller = loader.getController();
            if (controller != null) {
                scene.setUserData(controller);
                if (controller instanceof BaseDialogController) {
                    ((BaseDialogController) controller).setDialogStage(dialogStage);
                }
                LoggerUtility.info("Dialog controller set: " + controller.getClass().getSimpleName());
            } else {
                LoggerUtility.error("Failed to get controller for dialog: " + title);
            }

            return dialogStage;
        } catch (IOException e) {
            LoggerUtility.error("Error creating custom dialog: " + title, e);
            showAlert("Error", "Failed to create dialog: " + title);
            return null;
        }
    }

    private void loadDialogSpecificCSS(Scene scene, Class<?> controllerClass) {
        try {
            LoggerUtility.info("Loading dialog-specific CSS for " + controllerClass.getSimpleName());
            String cssPath = null;
            if (controllerClass.getSimpleName().equals("UserStatusDialogController")) {
                cssPath = AppPathsCSS.USER_STATUS_DIALOG;
            } else if (controllerClass.getSimpleName().equals("AboutDialogController")) {
                cssPath = AppPathsCSS.IMAGE_STYLE_A;
            }

            if (cssPath != null) {
                String css = Objects.requireNonNull(getClass().getResource(cssPath)).toExternalForm();
                scene.getStylesheets().add(css);
                LoggerUtility.info("CSS loaded: " + cssPath);
            } else {
                LoggerUtility.info("No specific CSS found for " + controllerClass.getSimpleName());
            }
        } catch (Exception e) {
            LoggerUtility.error("Error loading dialog-specific CSS: " + e.getMessage());
        }
    }

    protected abstract void performRoleSpecificLogout();

    protected abstract void redirectToLogin();

    protected Scene getCurrentScene() {
        if (logoutButton != null && logoutButton.getScene() != null) {
            return logoutButton.getScene();
        } else if (backButton != null && backButton.getScene() != null) {
            return backButton.getScene();
        } else {
            // Try to find any node that has a scene
            for (java.lang.reflect.Field field : this.getClass().getDeclaredFields()) {
                if (javafx.scene.Node.class.isAssignableFrom(field.getType())) {
                    try {
                        field.setAccessible(true);
                        javafx.scene.Node node = (javafx.scene.Node) field.get(this);
                        if (node != null && node.getScene() != null) {
                            return node.getScene();
                        }
                    } catch (IllegalAccessException e) {
                        LoggerUtility.error("Error accessing field: " + field.getName(), e);
                    }
                }
            }
            LoggerUtility.error("Cannot get scene as all known elements are null or don't have a scene");
            return null;
        }
    }
}