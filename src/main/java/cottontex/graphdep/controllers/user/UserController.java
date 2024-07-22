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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;

public class UserController extends BaseController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label displayTimeInfo;
    @FXML
    private Button startButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button endButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button myAccountButton;
    @FXML
    private ImageView logoImage;

    @Setter
    @Getter
    private Integer userID;
    private String username;

    private ScheduleUserTable scheduleUserTable;

    @FXML
    public void initialize() {
        Image logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/cottontex/graphdep/images/ct.png")));
        logoImage.setImage(logo);
        try {
            scheduleUserTable = new ScheduleUserTable();
            updateTimeDisplay();
            if (startButton != null) startButton.setDisable(true);
            if (pauseButton != null) pauseButton.setDisable(true);
            if (endButton != null) endButton.setDisable(true);
            if (myAccountButton != null) myAccountButton.setDisable(false);
            if (logoutButton != null) logoutButton.setDisable(false);

        } catch (Exception e) {
            LoggerUtility.error("Error in UserController initialize", e);
        }
    }

    public void setUserInfo(Integer userID, String username) {
        this.userID = userID;
        this.username = username;
        updateWelcomeMessage();
        checkActiveSession();
    }

    private void updateWelcomeMessage() {
        if (welcomeLabel != null && username != null) {
            welcomeLabel.setText("Welcome, " + username + "!");
        }
    }

    private void updateTimeDisplay() {
        if (displayTimeInfo != null) {
            String currentTime = DateTimeUtils.getCurrentDateTimeForDisplay();
            displayTimeInfo.setText(currentTime);
        }
    }

    private void checkActiveSession() {
        if (userID == null) {
            LoggerUtility.error("userID is null in checkActiveSession. Make sure to call setUserInfo before using the controller.");
            return;
        }

        Date currentDate = new Date(System.currentTimeMillis());
        boolean hasActiveSession = scheduleUserTable.hasActiveSession(userID, currentDate);

        if (startButton != null) startButton.setDisable(hasActiveSession);
        if (pauseButton != null) pauseButton.setDisable(!hasActiveSession);
        if (endButton != null) endButton.setDisable(!hasActiveSession);
    }

    @FXML
    protected void onLogoutButtonClick() {
        if (logoutButton != null) {
            loadPage((Stage) logoutButton.getScene().getWindow(), "/cottontex/graphdep/fxml/LauncherLayout.fxml", "Login");
        }
    }
    @FXML
    protected void onMyAccountButtonClick() {
        SettingsUserController.openSettingsWindow(userID);
    }

//    @FXML
//    protected void onMyAccountButtonClick() {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cottontex/graphdep/fxml/user/SettingsUserLayout.fxml"));
//            Parent root = loader.load();
//
//            SettingsUserController settingsController = loader.getController();
//            settingsController.setUserID(userID);
//
//            if (myAccountButton != null) {
//                Stage stage = (Stage) myAccountButton.getScene().getWindow();
//                stage.setScene(new Scene(root));
//                stage.setTitle("My Account");
//            }
//        } catch (IOException e) {
//            LoggerUtility.error("Error loading My Account page", e);
//        }
//    }

    @FXML
    protected void onStartButtonClick() {
        if (userID == null) {
            LoggerUtility.error("userID is null in onStartButtonClick");
            return;
        }

        Timestamp startTimestamp = new Timestamp(System.currentTimeMillis());

        boolean success = scheduleUserTable.saveStartHour(userID, startTimestamp);
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
        if (userID == null) {
            LoggerUtility.error("userID is null in onPauseButtonClick");
            return;
        }

        Timestamp pauseTimestamp = new Timestamp(System.currentTimeMillis());

        scheduleUserTable.savePauseTime(userID, pauseTimestamp);
        updateTimeDisplay();
        if (startButton != null) startButton.setDisable(false);
        if (pauseButton != null) pauseButton.setDisable(true);
        if (endButton != null) endButton.setDisable(true);
    }

    @FXML
    protected void onEndButtonClick() {
        if (userID == null) {
            LoggerUtility.error("userID is null in onEndButtonClick");
            return;
        }

        Timestamp endTimestamp = new Timestamp(System.currentTimeMillis());

        scheduleUserTable.finalizeWorkDay(userID, endTimestamp);
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
            controller.setUserID(userID);
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