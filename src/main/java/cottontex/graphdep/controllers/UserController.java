package cottontex.graphdep.controllers;

import cottontex.graphdep.database.queries.ScheduleUserTable;
import cottontex.graphdep.database.queries.UserLogin;
import cottontex.graphdep.database.queries.UserStatusHandler;
import cottontex.graphdep.models.UserStatus;
import cottontex.graphdep.utils.DateTimeUtils;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import jfxtras.scene.control.LocalTimePicker;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class UserController extends BaseController {

    @FXML private DatePicker datePicker;
    @FXML private LocalTimePicker timePicker;
    @FXML private Label welcomeLabel;
    @FXML private Label errorLabel;
    @FXML private Button startButton;
    @FXML private Button pauseButton;
    @FXML private Button endButton;
    @FXML private Button logoutButton;
    @FXML private Button myAccountButton;
    @FXML private UserStatus currentStatus;

    private UserStatusHandler userStatusHandler = new UserStatusHandler();

    @Getter
    private Integer userID;

    @FXML
    public void initialize() {
        datePicker.setValue(LocalDate.now());
        timePicker.setLocalTime(LocalTime.now());
    }

    public void setUsername(String username) {
        welcomeLabel.setText("Welcome " + username + "!");
        datePicker.setValue(LocalDate.now());
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
        loadUserStatus();
    }

    private String getUsernameFromDatabase() {
        String username = UserStatusHandler.getUsernameById(userID);
        if (username == null || username.isEmpty()) {
            LoggerUtility.warn("Could not retrieve username for user ID: " + userID);
            return "User"; // Default fallback if username can't be fetched
        }
        return username;
    }

    private void loadUserStatus() {
        // Always display the welcome message
        welcomeLabel.setText("Welcome, " + getUsernameFromDatabase() + "!");

        currentStatus = userStatusHandler.getUserStatus(userID);

        if (currentStatus != null) {
            updateButtonStates();

            if (currentStatus.getStartTime() != null && !currentStatus.getStartTime().isEmpty()) {
                try {
                    // Parse the full timestamp
                    LocalDateTime startDateTime = LocalDateTime.parse(currentStatus.getStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    datePicker.setValue(startDateTime.toLocalDate());
                    timePicker.setLocalTime(startDateTime.toLocalTime());
                } catch (DateTimeParseException e) {
                    LoggerUtility.error("Error parsing start time: " + currentStatus.getStartTime(), e);
                    datePicker.setValue(LocalDate.now());
                    timePicker.setLocalTime(LocalTime.now());
                }
            } else {
                datePicker.setValue(LocalDate.now());
                timePicker.setLocalTime(LocalTime.now());
            }
        } else {
            LoggerUtility.error("Failed to load user status for user ID: " + userID);
            updateButtonStates(false, true, true); // Enable only the start button
            datePicker.setValue(LocalDate.now());
            timePicker.setLocalTime(LocalTime.now());
        }
    }


    private void updateButtonStates() {
        if (currentStatus.isOnline()) {
            startButton.setDisable(true);
            pauseButton.setDisable(false);
            endButton.setDisable(false);
        } else {
            startButton.setDisable(false);
            pauseButton.setDisable(true);
            endButton.setDisable(true);
        }
    }

    @FXML
    protected void onStartButtonClick() {
        Timestamp startTimestamp = DateTimeUtils.getTimestampFromPickers(datePicker,timePicker);
        userStatusHandler.saveStartTime(userID, startTimestamp);
        loadUserStatus();
    }

    @FXML
    protected void onPauseButtonClick() {
        Timestamp pauseTimestamp = DateTimeUtils.getTimestampFromPickers(datePicker,timePicker);
        userStatusHandler.savePauseTime(userID, pauseTimestamp);
        loadUserStatus();
    }

    @FXML
    protected void onEndButtonClick() {
        Timestamp endTimestamp = DateTimeUtils.getTimestampFromPickers(datePicker,timePicker);
        userStatusHandler.saveEndTime(userID, endTimestamp);
        loadUserStatus();
    }

    @FXML
    protected void onLogoutButtonClick() {
        loadPage((Stage) logoutButton.getScene().getWindow(), "/cottontex/graphdep/fxml/LauncherLayout.fxml", "Login");
    }

    private void updateButtonStates(boolean startDisabled, boolean pauseDisabled, boolean endDisabled) {
        startButton.setDisable(startDisabled);
        pauseButton.setDisable(pauseDisabled);
        endButton.setDisable(endDisabled);
    }



    @FXML
    protected void onMyAccountButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cottontex/graphdep/fxml/SettingsUserLayout.fxml"));
            Parent root = loader.load();

            SettingsUserController settingsController = loader.getController();
            settingsController.setUserID(userID);

            Stage stage = (Stage) myAccountButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("My Account");
        } catch (IOException e) {
            LoggerUtility.error("Error loading My Account page", e);
            // Show an error message to the user
        }
    }

    private String formatTime(Timestamp time) {
        if (time == null) {
            return "N/A";
        }
        return time.toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}