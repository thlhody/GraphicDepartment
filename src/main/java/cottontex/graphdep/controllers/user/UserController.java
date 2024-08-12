package cottontex.graphdep.controllers.user;

import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.controllers.info.UserStatusDialogController;
import cottontex.graphdep.models.UserSession;
import cottontex.graphdep.models.managers.UserSessionManager;
import cottontex.graphdep.services.user.UserService;
import cottontex.graphdep.utils.DateTimeUtils;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class UserController extends UserBaseController {

    private UserService userService;

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
        this.userService = new UserService(scheduleUserTable);
        LoggerUtility.initialize(this.getClass(), "Initializing UserController dependencies");
    }

    @FXML
    public void initialize() {
        LoggerUtility.initialize(this.getClass(), null);
        super.initialize();
        super.setupLogo();
        initializeDependencies();
        this.userSession = UserSession.getInstance();
        LoggerUtility.info("UserSession in UserController initialize: " + this.userSession);
        if (this.userSession != null) {
            boolean initialized = super.initializeUserData();
            LoggerUtility.info("User data initialization result in UserController: " + initialized);
            if (initialized) {
                setWelcomeMessage();
                updateButtonStates();
                updateDisplayTimeInfo();
            } else {
                LoggerUtility.error("Failed to initialize user data in UserController");
            }
        } else {
            LoggerUtility.error("UserSession is null in UserController initialize method");
        }
    }

    @Override
    protected void initializeUserDependencies() {
        super.initializeUserDependencies();
        this.userService = new UserService(scheduleUserTable);
        LoggerUtility.initialize(this.getClass(), "Initializing UserController dependencies");
    }

    @Override
    protected void initializeUserComponents() {
        super.setWelcomeMessage();
        updateButtonStates();
        updateDisplayTimeInfo();
    }


    @FXML
    protected void onStartButtonClick() {
        LoggerUtility.buttonInfo("Start Work", UserSession.getInstance().getUsername());
        workSessionState = userService.startWork(UserSession.getInstance(), workSessionState);
        updateButtonStates();
        updateDisplayTimeInfo();
    }

    @FXML
    protected void onPauseButtonClick() {
        LoggerUtility.buttonInfo("Pause/Resume Work", UserSession.getInstance().getUsername());
        workSessionState = userService.togglePause(UserSession.getInstance(), workSessionState);
        updateButtonStates();
        updateDisplayTimeInfo();
    }

    @FXML
    protected void onEndButtonClick() {
        LoggerUtility.buttonInfo("End Work", UserSession.getInstance().getUsername());
        workSessionState = userService.endWork(UserSession.getInstance(), workSessionState);
        updateButtonStates();
        updateDisplayTimeInfo();
    }

    @FXML
    protected void onUserStatusButtonClick() {
        LoggerUtility.buttonInfo("User Status", UserSession.getInstance().getUsername());
        Stage dialogStage = createCustomDialog(AppPathsFXML.USER_STATUS_DIALOG, "Users", UserStatusDialogController.class);
        if (dialogStage != null) {
            dialogStage.showAndWait();
        }
    }

    @FXML
    protected void onMyAccountButtonClick() {
        LoggerUtility.buttonInfo("My Account", UserSession.getInstance().getUsername());
        if (UserSession.getInstance() != null) {
            Stage stage = (Stage) myAccountButton.getScene().getWindow();
            LoggerUtility.switchController(this.getClass(), UserSettingsController.class, UserSession.getInstance().getUsername());
            loadPage(stage, AppPathsFXML.MY_ACCOUNT, "My Account", UserSession.getInstance());
        } else {
            LoggerUtility.error("UserSession is null in UserController.onMyAccountButtonClick()");
        }
    }

    @FXML
    protected void onViewWorkTableButtonClick() {
        if (UserSession.getInstance() != null) {
            Stage stage = (Stage) workTableButton.getScene().getWindow();
            loadPage(stage, AppPathsFXML.USER_MONTHLY_TIME_LAYOUT, "My Work Hours", UserSession.getInstance());
        } else {
            LoggerUtility.error("UserSession is null in UserController.onViewWorkTableButtonClick()");
        }
    }

    private void updateButtonStates() {
        startButton.setDisable(workSessionState.isWorking());
        pauseButton.setDisable(!workSessionState.isWorking());
        endButton.setDisable(!workSessionState.isWorking());

        pauseButton.setText(workSessionState.isPaused() ? "Resume" : "Pause");
        LoggerUtility.info("Button states updated. isWorking: " + workSessionState.isWorking() + ", isPaused: " + workSessionState.isPaused());
    }

    private void updateDisplayTimeInfo() {
        String displayInfo = userService.getDisplayTimeInfo(workSessionState);
        displayTimeInfo.setText(displayInfo);
        LoggerUtility.info("Display time info updated: " + displayInfo);
    }


    @FXML
    protected void onLogoutButtonClick() {
        LoggerUtility.buttonInfo("Logout", userSession.getUsername());
        performRoleSpecificLogout();
        UserSessionManager.clearSession();
        redirectToLogin();
    }

}