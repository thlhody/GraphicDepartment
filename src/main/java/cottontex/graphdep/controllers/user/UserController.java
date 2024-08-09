package cottontex.graphdep.controllers.user;

import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.database.queries.user.ScheduleUserTable;
import cottontex.graphdep.models.UserSession;
import cottontex.graphdep.utils.DateTimeUtils;
import cottontex.graphdep.utils.DependencyFactory;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.sql.Timestamp;

public class UserController extends UserBaseController {

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
    private Button logoutButton;
    @FXML
    private Label displayTimeInfo;

    @FXML
    private ScheduleUserTable scheduleUserTable;

    private boolean isWorking = false;
    private boolean isPaused = false;

    @FXML
    public void initialize() {
        super.setupLogo();
        initializeDependencies();
        this.userSession = UserSession.getInstance();
        LoggerUtility.info("UserSession in UserController initialize: " + this.userSession);
        if (this.userSession != null) {
            boolean initialized = super.initializeUserData();
            LoggerUtility.info("User data initialization result in UserController: " + initialized);
            if (initialized) {
                updateButtonStates();
                updateDisplayTimeInfo();
            } else {
                LoggerUtility.error("Failed to initialize user data in UserController");
            }
        } else {
            LoggerUtility.error("UserSession is null in UserController initialize method");
        }
    }

    protected void initializeDependencies() {
        scheduleUserTable = DependencyFactory.getInstance().createScheduleUserTable();
    }

    @Override
    public void setUserSession(UserSession session) {
        super.setUserSession(session);
        LoggerUtility.info("UserSession set in UserController: " + session);
    }

    @Override
    protected boolean initializeUserData() {
        userSession = UserSession.getInstance();
        if (userSession == null) {
            LoggerUtility.error("UserSession is null in UserController.initializeUserData()");
            return false;
        }

        Integer userId = userSession.getUserId();
        if (userId == null) {
            LoggerUtility.error("UserID is null in UserController.initializeUserData()");
            return false;
        }

        LocalDate today = LocalDate.now();
        if (scheduleUserTable != null) {
            isWorking = scheduleUserTable.hasActiveSession(userId, java.sql.Date.valueOf(today));
            updateButtonStates();
            updateDisplayTimeInfo();
            return true;
        } else {
            LoggerUtility.error("scheduleUserTable is null in UserController.initializeUserData()");
            return false;
        }
    }

    @Override
    protected Scene getScene() {
        if (logoutButton != null) { // Assuming startButton is part of the scene
            return logoutButton.getScene();
        } else {
            LoggerUtility.error("logoutButton is null, cannot obtain Scene.");
            return null;
        }
    }

    @FXML
    protected void onStartButtonClick() {
        if (!isWorking && UserSession.getInstance() != null) {
            Integer userId = UserSession.getInstance().getUserId();
            Timestamp startTimestamp = new Timestamp(System.currentTimeMillis());
            if (userId != null && scheduleUserTable.saveStartHour(userId, startTimestamp)) {
                isWorking = true;
                updateButtonStates();
                updateDisplayTimeInfo();
            } else {
                LoggerUtility.error("Failed to start work session");
            }
        } else {
            LoggerUtility.error("UserSession is null or already working in UserController.onStartButtonClick()");
        }
    }
    @FXML
    protected void onPauseButtonClick() {
        if (isWorking && UserSession.getInstance() != null) {
            Integer userId = UserSession.getInstance().getUserId();
            Timestamp pauseTimestamp = new Timestamp(System.currentTimeMillis());
            scheduleUserTable.savePauseTime(userId, pauseTimestamp);
            isPaused = !isPaused;
            updateButtonStates();
            updateDisplayTimeInfo();
        } else {
            LoggerUtility.error("UserSession is null or not working in UserController.onPauseButtonClick()");
        }
    }
    @FXML
    protected void onEndButtonClick() {
        if (isWorking && UserSession.getInstance() != null) {
            Integer userId = UserSession.getInstance().getUserId();
            Timestamp endTimestamp = new Timestamp(System.currentTimeMillis());
            scheduleUserTable.finalizeWorkDay(userId, endTimestamp);
            isWorking = false;
            isPaused = false;
            updateButtonStates();
            updateDisplayTimeInfo();
        } else {
            LoggerUtility.error("UserSession is null or not working in UserController.onEndButtonClick()");
        }
    }

    @FXML
    protected void onMyAccountButtonClick() {
        if (UserSession.getInstance() != null) {
            Stage stage = (Stage) myAccountButton.getScene().getWindow();
            loadPage(stage, AppPathsFXML.MY_ACCOUNT, "My Account", UserSession.getInstance());
        } else {
            LoggerUtility.error("UserSession is null in UserController.onMyAccountButtonClick()");
        }
    }

    @FXML
    protected void onLogoutButtonClick() {
        UserSession.setInstance(userSession);
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        loadPage(stage, AppPathsFXML.LAUNCHER, "Login", new UserSession());
    }

    private void updateButtonStates() {
        startButton.setDisable(isWorking);
        pauseButton.setDisable(!isWorking);
        endButton.setDisable(!isWorking);

        if (isPaused) {
            pauseButton.setText("Resume");
        } else {
            pauseButton.setText("Pause");
        }
    }

    private void updateDisplayTimeInfo() {
        if (isWorking) {
            if (isPaused) {
                displayTimeInfo.setText("Work paused at "+ DateTimeUtils.getCurrentDateTimeForDisplay());
            } else {
                displayTimeInfo.setText("Currently working at "+ DateTimeUtils.getCurrentDateTimeForDisplay());
            }
        } else {
            displayTimeInfo.setText("Not working at "+ DateTimeUtils.getCurrentDateTimeForDisplay());
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
}
