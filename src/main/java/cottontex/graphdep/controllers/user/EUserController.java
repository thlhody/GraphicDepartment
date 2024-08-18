package cottontex.graphdep.controllers.user;

import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.controllers.info.EUserStatusDialogController;
import cottontex.graphdep.models.WorkSessionState;
import cottontex.graphdep.models.managers.EUserSessionManager;
import cottontex.graphdep.services.user.UserService;
import cottontex.graphdep.utils.DependencyFactory;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

public class EUserController extends EUserBaseController {

    private UserService userService;
    private WorkSessionState currentState;
    private Timeline updateTimeline;

    @FXML
    private Label welcomeLabel;
    @FXML
    private Button startButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button endButton;
    @FXML
    private Button myAccountButton;
    @FXML
    private Button workTableButton;
    @FXML
    private Label displayTimeInfo;
    @FXML
    private Button userStatusButton;

    @Override
    protected void initializeDependencies() {
        super.initializeDependencies();
        this.userService = DependencyFactory.getInstance().get(UserService.class);
        LoggerUtility.info("EUserController dependencies initialized");
    }
    public EUserController() {
        Platform.runLater(this::initializeWorkSessionState);
    }

    @Override
    protected void initializeComponents() {
        super.initializeComponents();
        setWelcomeMessage();
        initializeWorkSessionState();
        setupDisplayTimeInfoUpdater();
        updateUI();
        LoggerUtility.info("EUserController components initialized");
    }

    private void setWelcomeMessage() {
        String welcomeMessage = EUserSessionManager.getSession()
                .map(session -> "Welcome, " + session.getName() + "!")
                .orElse("Welcome!");
        welcomeLabel.setText(welcomeMessage);
        LoggerUtility.info("Welcome message set: " + welcomeMessage);
    }

    private void initializeWorkSessionState() {
        userSession.ifPresent(session -> {
            currentState = userService.getCurrentWorkSessionState(session.getUserId());
            EUserSessionManager.updateWorkSessionState(currentState);
            Platform.runLater(this::updateUI);  // Ensure UI update happens on JavaFX Application Thread
            LoggerUtility.info("WorkSessionState initialized for user: " + session.getUsername() +
                    "sessionState=" + currentState.getSessionState() +
                    ", isWorking: " + currentState.isWorking() +
                    ", isPaused: " + currentState.isPaused());
        });
    }

    private void updateUI() {
        if (currentState == null) {
            currentState = new WorkSessionState(); // Initialize with default state if null
            LoggerUtility.warn("CurrentState was null, initialized with default state");
        }
        updateButtonStates();
        updateDisplayTimeInfo();
        LoggerUtility.info("UI updated based on current state: " + currentState.getSessionState() +
                ", isWorking: " + currentState.isWorking() +
                ", isPaused: " + currentState.isPaused());
    }

    private void updateButtonStates() {
        if (currentState != null) {
            boolean isWorking = currentState.isWorking();
            boolean isPaused = currentState.isPaused();

            startButton.setDisable(isWorking);
            pauseButton.setDisable(!isWorking);
            endButton.setDisable(!isWorking);

            if (isPaused) {
                pauseButton.setText("Resume");
            } else {
                pauseButton.setText("Pause");
            }

            LoggerUtility.info("Button states updated. isWorking: " + isWorking + ", isPaused: " + isPaused);
        } else {
            LoggerUtility.warn("Unable to update button states: currentState is null");
        }
    }

    private void setupDisplayTimeInfoUpdater() {
        if (updateTimeline != null) {
            updateTimeline.stop();
        }
        updateTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateDisplayTimeInfo()));
        updateTimeline.setCycleCount(Animation.INDEFINITE);
        updateTimeline.play();
    }

    private void updateDisplayTimeInfo() {
        WorkSessionState state = getCurrentWorkSessionState();
        String displayInfo = userService.getDisplayTimeInfo(state);
        displayTimeInfo.setText(displayInfo);
    }


    @Override
    protected void performRoleSpecificLogout() {
        super.performRoleSpecificLogout();
        EUserSessionManager.clearSession();
    }

    @Override
    public void onLogoutButtonClick() {
        if (updateTimeline != null) {
            updateTimeline.stop();
        }
        super.onLogoutButtonClick();
    }

    //Buttons-FXML

    @FXML
    protected void onStartButtonClick() {
        LoggerUtility.buttonInfo("Start Work", EUserSessionManager.getCurrentUsername());
        EUserSessionManager.getSession().ifPresent(session -> {
            currentState = userService.startWork(session);
            EUserSessionManager.updateWorkSessionState(currentState);
            updateUI();
        });
    }

    @FXML
    protected void onPauseButtonClick() {
        LoggerUtility.buttonInfo("Pause/Resume Work", EUserSessionManager.getCurrentUsername());
        EUserSessionManager.getSession().ifPresent(session -> {
            currentState = userService.togglePause(session);
            EUserSessionManager.updateWorkSessionState(currentState);
            updateUI();
        });
    }

    @FXML
    protected void onEndButtonClick() {
        LoggerUtility.buttonInfo("End Work", EUserSessionManager.getCurrentUsername());
        EUserSessionManager.getSession().ifPresent(session -> {
            currentState = userService.endWork(session);
            EUserSessionManager.updateWorkSessionState(currentState);
            updateUI();
        });
    }

    @FXML
    protected void onUserStatusButtonClick() {
        LoggerUtility.buttonInfo("User Status", EUserSessionManager.getCurrentUsername());
        Stage dialogStage = createCustomDialog(EUserStatusDialogController.class);
        if (dialogStage != null) {
            dialogStage.showAndWait();
        }
    }

    @FXML
    protected void onMyAccountButtonClick() {
        LoggerUtility.buttonInfo("My Account", EUserSessionManager.getCurrentUsername());
        EUserSessionManager.getSession().ifPresent(session -> {
            Stage stage = (Stage) myAccountButton.getScene().getWindow();
            LoggerUtility.switchController(this.getClass(), EUserSettingsController.class, session.getUsername());
            loadPage(stage, AppPathsFXML.MY_ACCOUNT, "My Account", session);
        });
    }

    @FXML
    protected void onViewWorkTableButtonClick() {
        EUserSessionManager.getSession().ifPresent(session -> {
            Stage stage = (Stage) workTableButton.getScene().getWindow();
            loadPage(stage, AppPathsFXML.USER_MONTHLY_TIME_LAYOUT, "My Work Hours", session);
        });
    }
}