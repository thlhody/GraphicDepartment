package cottontex.graphdep.controllers.user;

import cottontex.graphdep.controllers.BaseController;
import cottontex.graphdep.database.queries.user.ScheduleUserTable;
import cottontex.graphdep.utils.DateTimeUtils;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;

public class UserController extends BaseController {

    @FXML private Label welcomeLabel;
    @FXML private Label displayTimeInfo;
    @FXML private Button startButton;
    @FXML private Button pauseButton;
    @FXML private Button endButton;
    @FXML private Button logoutButton;
    @FXML private Button myAccountButton;
    @FXML private ImageView logoImage;

    private ScheduleUserTable scheduleUserTable;

    @Override
    public void initializeUserData() {
        welcomeLabel.setText("Welcome, " + userSession.getUsername() + "!");
        checkActiveSession();
    }

    @FXML
    public void initialize() {
        try {
            scheduleUserTable = new ScheduleUserTable();
            updateTimeDisplay();
            startButton.setDisable(true);
            pauseButton.setDisable(true);
            endButton.setDisable(true);
            myAccountButton.setDisable(false);
            logoutButton.setDisable(false);
            setupLogo();
        } catch (Exception e) {
            LoggerUtility.error("Error in UserController initialize", e);
        }
    }

    private void updateTimeDisplay() {
        if (displayTimeInfo != null) {
            String currentTime = DateTimeUtils.getCurrentDateTimeForDisplay();
            displayTimeInfo.setText(currentTime);
        }
    }

    private void checkActiveSession() {
        if (userSession.getUserID() == null) {
            LoggerUtility.error("userID is null in checkActiveSession. Make sure to call initializeUserData before using the controller.");
            return;
        }

        Date currentDate = new Date(System.currentTimeMillis());
        boolean hasActiveSession = scheduleUserTable.hasActiveSession(userSession.getUserID(), currentDate);

        startButton.setDisable(hasActiveSession);
        pauseButton.setDisable(!hasActiveSession);
        endButton.setDisable(!hasActiveSession);
    }


    private void updateWelcomeMessage() {
        if (welcomeLabel != null && userSession.getUsername() != null) {
            welcomeLabel.setText("Welcome, " + userSession.getUsername() + "!");
        }
    }


    @FXML
    protected void onLogoutButtonClick() {
        if (logoutButton != null) {
            loadPage((Stage) logoutButton.getScene().getWindow(), "/cottontex/graphdep/fxml/LauncherLayout.fxml", "Login");
        }
    }
    @FXML
    protected void onMyAccountButtonClick() {
        SettingsUserController.openSettingsWindow(userSession.getUserID());
    }

    @FXML
    protected void onStartButtonClick() {
        if (userSession.getUserID() == null) {
            LoggerUtility.error("userID is null in onStartButtonClick");
            return;
        }

        Timestamp startTimestamp = new Timestamp(System.currentTimeMillis());

        boolean success = scheduleUserTable.saveStartHour(userSession.getUserID(), startTimestamp);
        if (success) {
            updateTimeDisplay();
            if (startButton != null) startButton.setDisable(true);
            if (pauseButton != null) pauseButton.setDisable(false);
            if (endButton != null) endButton.setDisable(false);
        } else {
            LoggerUtility.error("Failed to save start time");
        }
    }

    @FXML
    protected void onPauseButtonClick() {
        if (userSession.getUserID() == null) {
            LoggerUtility.error("userID is null in onPauseButtonClick");
            return;
        }

        Timestamp pauseTimestamp = new Timestamp(System.currentTimeMillis());

        scheduleUserTable.savePauseTime(userSession.getUserID(), pauseTimestamp);
        updateTimeDisplay();
        if (startButton != null) startButton.setDisable(false);
        if (pauseButton != null) pauseButton.setDisable(true);
        if (endButton != null) endButton.setDisable(true);
    }

    @FXML
    protected void onEndButtonClick() {
        if (userSession.getUserID() == null) {
            LoggerUtility.error("userID is null in onEndButtonClick");
            return;
        }

        Timestamp endTimestamp = new Timestamp(System.currentTimeMillis());

        scheduleUserTable.finalizeWorkDay(userSession.getUserID(), endTimestamp);
        updateTimeDisplay();
        if (startButton != null) startButton.setDisable(false);
        if (pauseButton != null) pauseButton.setDisable(true);
        if (endButton != null) endButton.setDisable(true);
    }

    @FXML
    protected void viewWorkTable() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cottontex/graphdep/fxml/user/UserMonthlyTimeLayout.fxml"));
            Parent root = loader.load();

            UserMonthlyTimeController controller = loader.getController();
            controller.setUserID(userSession.getUserID());
            controller.loadUserMonthlyData();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("My Work Hours");
            stage.show();
        } catch (IOException e) {
            LoggerUtility.error("Error loading User Monthly Time view", e);
            showAlert("Error", "Unable to load work hours view.");
        }
    }
}