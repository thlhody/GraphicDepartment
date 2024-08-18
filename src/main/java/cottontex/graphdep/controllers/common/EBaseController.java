package cottontex.graphdep.controllers.common;

import cottontex.graphdep.constants.AppPathsIMG;
import cottontex.graphdep.models.UserSession;
import cottontex.graphdep.models.managers.EUserSessionManager;
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

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

public abstract class EBaseController {
    protected final DependencyFactory factory = DependencyFactory.getInstance();
    protected Optional<UserSession> userSession = Optional.empty();

    @FXML protected ImageView logoImage;
    @FXML protected ImageView mainImage;
    @FXML protected Button logoutButton;
    @FXML protected Button backButton;


    @FXML
    public void initialize() {
        try {
            LoggerUtility.initialize(this.getClass(), null);
            setupLogo();
            initializeDependencies();
            initializeComponents();
        } catch (Exception e) {
            LoggerUtility.error("Error during controller initialization", e);
            showAlert("Initialization Error", "An error occurred while initializing the application. Please try again.");
        }
    }

    protected void initializeUserSession() {
        userSession = EUserSessionManager.getSession();
        userSession.ifPresentOrElse(
                session -> {
                    if (!initializeUserData(session)) {
                        LoggerUtility.error("Failed to initialize user data");
                        handleUserDataInitializationFailure();
                    }
                },
                () -> {
                    LoggerUtility.error("UserSession is null");
                    handleNullUserSession();
                }
        );
    }

    protected abstract void initializeDependencies();

    protected boolean initializeUserData(UserSession session) {
        // Default implementation, can be overridden
        return true;
    }

    protected abstract void initializeComponents();

    protected boolean requiresUserSession() {
        // Default to true, override in subclasses if not required
        return true;
    }

    public void setupLogo() {
        try {
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
                    LoggerUtility.warn("Logo image not found at path: " + AppPathsIMG.COTTONTEX_LOGO);
                }
            } else {
                LoggerUtility.warn("LogoImage is null after FXML loading.");
            }
        } catch (Exception e) {
            LoggerUtility.error("Error in setting up the logo: " + e.getMessage(), e);
        }
    }

    protected void setupMainImage() {
        try {
            if (mainImage != null) {
                Image mainLogo = new Image(Objects.requireNonNull(getClass().getResourceAsStream(AppPathsIMG.CREATIVE_TIME_TASK_TRACKER))); // Update path
                mainImage.setImage(mainLogo);
                mainImage.setFitWidth(400);
                mainImage.setFitHeight(300);
                mainImage.setPreserveRatio(true);
            }
        } catch (Exception e) {
            LoggerUtility.error("Error setting up main image", e);
        }
    }

    // Hook method for additional initialization steps
    protected void postInitialize() {
        // Default empty implementation, can be overridden in subclasses
    }

    // Error handling methods
    protected void handleInitializationError(Exception e) {
        LoggerUtility.error("Error during controller initialization", e);
        showAlert("Initialization Error", "An error occurred while initializing the application. Please try again.");
    }

    protected void handleUserDataInitializationFailure() {
        LoggerUtility.error("Failed to initialize user data");
        showAlert("User Data Error", "Failed to initialize user data. Please log out and log in again.");
    }

    protected void handleNullUserSession() {
        LoggerUtility.error("Null user session encountered");
        showAlert("Session Error", "Your session has expired. Please log in again.");
        redirectToLogin();
    }

    // Utility method to get dependencies
    protected <T> T getDependency(Class<T> type) {
        return factory.get(type);
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
            if (controller instanceof EBaseController) {
                ((EBaseController) controller).setUserSession(userSession);
            } else {
                LoggerUtility.error("Controller is not an instance of BaseController or is null");
            }
            WindowManager.updateMainStage(stage, scene);
        } catch (IOException e) {
            LoggerUtility.error("Error loading page: " + e.getMessage(), e);
            showAlert("Error", "Failed to load the requested page.");
        }
    }

    protected void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    protected <T> Stage createCustomDialog(Class<T> controllerClass) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(cottontex.graphdep.constants.AppPathsFXML.USER_STATUS_DIALOG));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.UTILITY);
            dialogStage.setTitle("Users");
            dialogStage.setResizable(false);

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            WindowManager.initializeDialogStage(dialogStage);

            T controller = loader.getController();
            if (controller != null) {
                scene.setUserData(controller);
                if (controller instanceof EBaseDialogController) {
                    ((EBaseDialogController) controller).setDialogStage(dialogStage);
                }
            } else {
                LoggerUtility.error("Failed to get controller for dialog: " + "Users");
            }

            return dialogStage;
        } catch (IOException e) {
            LoggerUtility.error("Error creating custom dialog: " + "Users", e);
            showAlert("Error", "Failed to create dialog: " + "Users");
            return null;
        }
    }

    public void setUserSession(UserSession session) {
        this.userSession = Optional.ofNullable(session);
        EUserSessionManager.setSession(session);
        if (!initializeUserData(session)) {
            LoggerUtility.error("Failed to initialize user data.");
            handleUserDataInitializationFailure();
        }
    }

    protected abstract void performRoleSpecificLogout();

    protected abstract void redirectToLogin();

    @FXML
    protected void onLogoutButtonClick() {
        LoggerUtility.buttonInfo("Logout", userSession.map(UserSession::getUsername).orElse("Unknown"));
        performRoleSpecificLogout();
        EUserSessionManager.clearSession();
        redirectToLogin();
    }

    @FXML
    protected void onBackButtonClick() {
        // Implement back button logic
    }

    protected Scene getCurrentScene() {
        if (logoutButton != null && logoutButton.getScene() != null) {
            return logoutButton.getScene();
        } else if (backButton != null && backButton.getScene() != null) {
            return backButton.getScene();
        } else {
            LoggerUtility.error("Cannot get scene as all known elements are null or don't have a scene");
            return null;
        }
    }
}