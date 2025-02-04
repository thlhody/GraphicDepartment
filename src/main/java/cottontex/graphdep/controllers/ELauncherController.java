package cottontex.graphdep.controllers;

import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.controllers.common.EBaseController;
import cottontex.graphdep.controllers.info.EAboutDialogController;
import cottontex.graphdep.database.interfaces.IUserLogin;
import cottontex.graphdep.models.UserSession;
import cottontex.graphdep.models.managers.EUserSessionManager;
import cottontex.graphdep.services.SyncService;
import cottontex.graphdep.utils.LoggerUtility;
import cottontex.graphdep.utils.WindowManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class ELauncherController extends EBaseController {

    private IUserLogin userLogin;
    private SyncService syncService;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button aboutButton;
    @FXML private Button loginButton;
    @FXML private Label onlineStatusLabel;
    @FXML private ProgressIndicator syncProgressIndicator;

    @Override
    protected void initializeDependencies() {
        try {
            this.userLogin = getDependency(IUserLogin.class);
            this.syncService = getDependency(SyncService.class);
            LoggerUtility.info("UserLogin and SyncService dependencies initialized successfully");
        } catch (IllegalArgumentException e) {
            LoggerUtility.error("Failed to initialize dependencies", e);
            showAlert("Initialization Error", "Failed to initialize application dependencies. Please restart the application.");
        }
    }

    @Override
    protected boolean initializeUserData(UserSession session) {
        // LauncherController doesn't need to initialize user data
        return true;
    }

    @Override
    protected void initializeComponents() {
        setupLogo();
        setupMainImage();
        initializeSync();
    }

    private void initializeSync() {
        if (syncService == null) {
            LoggerUtility.info("SyncService is not available. Application will operate in offline mode.");
            updateOnlineStatus(false);
            showAlert("Sync Error", "Unable to start synchronization. Some features may be unavailable.");
            return;
        }

        loginButton.setDisable(true);
        syncProgressIndicator.setVisible(true);

        CompletableFuture.supplyAsync(() -> {
            LoggerUtility.info("Starting synchronization");
            syncService.startAutoSync();
            return syncService.isOnline();
        }).thenAccept(isOnline -> {
            Platform.runLater(() -> {
                loginButton.setDisable(false);
                syncProgressIndicator.setVisible(false);
                updateOnlineStatus(isOnline);
                if (!isOnline) {
                    LoggerUtility.info("Offline Mode: The application is currently offline. Local data will be used.");
                    //showAlert("Offline Mode", "The application is currently offline. Local data will be used.");
                }
            });
        }).exceptionally(e -> {
            LoggerUtility.info("Unexpected error during synchronization. Operating in offline mode.");
            Platform.runLater(() -> {
                loginButton.setDisable(false);
                syncProgressIndicator.setVisible(false);
                updateOnlineStatus(false);
                LoggerUtility.info("Offline Mode: An error occurred. The application will operate in offline mode.");
                //showAlert("Offline Mode", "An error occurred. The application will operate in offline mode.");
            });
            return null;
        });
    }
    @Override
    protected boolean requiresUserSession() {
        return false; // LauncherController doesn't require a user session initially
    }

    @Override
    protected void performRoleSpecificLogout() {
        // No specific logout actions needed for LauncherController
    }

    @Override
    protected void redirectToLogin() {
        // LauncherController is already the login screen, so we don't need to redirect
        LoggerUtility.info("Already on login screen, no redirection needed");
    }

    private boolean handleSuccessfulLogin(String username, String role) {
        LoggerUtility.info("User authenticated successfully: " + username + ", Role: " + role);
        Integer userId = userLogin.getUserID(username);
        Integer employeeId = userLogin.getEmployeeId(username);
        String name = userLogin.getName(username);

        if (userId == null) {
            LoggerUtility.error("Failed to retrieve user ID for: " + username);
            showAlert("Login Error", "Failed to retrieve user information.");
            return false;
        }

        EUserSessionManager.initializeSession(userId, username, name, role, employeeId);
        return EUserSessionManager.getSession().isPresent();
    }

    private void navigateToAppropriateScreen(UserSession userSession) {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        String fxmlPath = "ADMIN".equals(userSession.getRole()) ? AppPathsFXML.ADMIN_PAGE_LAYOUT : AppPathsFXML.USER_PAGE_LAYOUT;
        String title = "ADMIN".equals(userSession.getRole()) ? "Admin Page" : "User Page";

        LoggerUtility.info("Loading FXML: " + fxmlPath + " for role: " + userSession.getRole());
        loadPage(stage, fxmlPath, title, userSession);
        LoggerUtility.switchController(this.getClass(), ("ADMIN".equals(userSession.getRole()) ? "AdminBaseController" : "UserBaseController").getClass(), userSession.getUsername());
    }

    @FXML
    protected void onLoginButtonClick() {
        LoggerUtility.buttonInfo("Login", "Attempt");
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Login Error", "Please enter both username and password.");
            return;
        }

        LoggerUtility.info("Attempting to authenticate user: " + username);
        String role = userLogin.authenticateUser(username, password);

        if (role != null) {
            if (handleSuccessfulLogin(username, role)) {
                EUserSessionManager.getSession().ifPresentOrElse(
                        this::navigateToAppropriateScreen,
                        () -> {
                            LoggerUtility.error("Failed to retrieve UserSession after initialization");
                            showAlert("Login Error", "An error occurred during login. Please try again.");
                        }
                );
            }
        } else {
            LoggerUtility.warn("Failed login attempt for user: " + username);
            showAlert("Login Error", "Invalid username or password.");
        }
    }

    @FXML
    protected void onAboutButtonClick() {
        LoggerUtility.buttonInfo("About", "Click");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppPathsFXML.ABOUT_DIALOG));
            Parent root = loader.load();

            EAboutDialogController controller = loader.getController();
            if (controller != null) {
                Stage aboutStage = new Stage();
                aboutStage.initModality(Modality.APPLICATION_MODAL);
                aboutStage.setTitle("About");

                Scene scene = new Scene(root);
                aboutStage.setScene(scene);

                controller.setDialogStage(aboutStage);
                controller.setContent("Creative Time And Task Tracker", "Version 1.0", "© 2024 thlhody");

                WindowManager.initializeDialogStage(aboutStage);
                aboutStage.setResizable(false);
                aboutStage.sizeToScene();

                // Center the dialog on the parent window
                Stage parentStage = (Stage) aboutButton.getScene().getWindow();
                aboutStage.initOwner(parentStage);

                aboutStage.setOnShown(event -> {
                    aboutStage.setX(parentStage.getX() + (parentStage.getWidth() - aboutStage.getWidth()) / 2);
                    aboutStage.setY(parentStage.getY() + (parentStage.getHeight() - aboutStage.getHeight()) / 2);
                });

                aboutStage.showAndWait();
            } else {
                LoggerUtility.error("EAboutDialogController is null");
                showAlert("Error", "Failed to initialize About dialog controller.");
            }
        } catch (IOException e) {
            LoggerUtility.error("Error loading About dialog", e);
            showAlert("Error", "Failed to load About dialog.");
        }
    }
    private void updateOnlineStatus(boolean isOnline) {
        if (onlineStatusLabel != null) {
            onlineStatusLabel.setText(isOnline ? "Online" : "Offline");
            onlineStatusLabel.setStyle(isOnline ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
        }
    }
}