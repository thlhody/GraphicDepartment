package cottontex.graphdep.controllers;

import cottontex.graphdep.database.queries.ScheduleUserTable;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import jfxtras.scene.control.LocalTimePicker;
import lombok.Setter;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private ScheduleUserTable scheduleUserTable = new ScheduleUserTable();
    private String currentUsername;

    @Setter private Integer userID;

    public void setUsername(String username) {
        welcomeLabel.setText("Welcome " + username + "!");
        datePicker.setValue(LocalDate.now());
    }

    @FXML
    protected void onLogoutButtonClick() {
        loadPage((Stage) logoutButton.getScene().getWindow(), "/cottontex/graphdep/fxml/LauncherLayout.fxml", "Graphics Department Login");
    }

    @FXML
    protected void onStartButtonClick() {
        LocalDateTime startDateTime = LocalDateTime.of(datePicker.getValue(), timePicker.getLocalTime());
        Timestamp startTimestamp = Timestamp.valueOf(startDateTime);

        if (scheduleUserTable.isStartHourExists(userID, startTimestamp)) {
            showError(errorLabel, "You already started work at this hour.");
            return;
        }

        scheduleUserTable.saveStartHour(userID, startTimestamp);
        LoggerUtility.info("Start hour saved successfully!");
        errorLabel.setText("Start hour saved successfully!");
        updateButtonStates(true, false, false);
    }

    @FXML
    protected void onPauseButtonClick() {
        LocalDateTime pauseDateTime = LocalDateTime.of(datePicker.getValue(), timePicker.getLocalTime());
        Timestamp pauseTimestamp = Timestamp.valueOf(pauseDateTime);

        if (!scheduleUserTable.hasStartHour(userID, Date.valueOf(datePicker.getValue()))) {
            showError(errorLabel, "No active work period to pause.");
            return;
        }

        if (showConfirmationDialog()) {
            scheduleUserTable.savePauseTime(userID, pauseTimestamp);
            LoggerUtility.info("Work paused successfully!");
            errorLabel.setText("Work paused successfully!");
            updateButtonStates(false, true, true);
        }
    }

    @FXML
    protected void onEndButtonClick() {
        LocalDateTime endDateTime = LocalDateTime.of(datePicker.getValue(), timePicker.getLocalTime());
        Timestamp endTimestamp = Timestamp.valueOf(endDateTime);

        if (!scheduleUserTable.hasStartHour(userID, Date.valueOf(datePicker.getValue()))) {
            showError(errorLabel, "No active work period to end.");
            return;
        }

        try {
            scheduleUserTable.finalizeWorkDay(userID, endTimestamp, Date.valueOf(datePicker.getValue()));
            LoggerUtility.info("End hour updated and total hours calculated successfully!");
            errorLabel.setText("Work day finalized successfully!");
            updateButtonStates(false, true, true);
        } catch (Exception e) {
            showError(errorLabel, "Error finalizing work day: " + e.getMessage());
        }
    }

    private void updateButtonStates(boolean startDisabled, boolean pauseDisabled, boolean endDisabled) {
        startButton.setDisable(startDisabled);
        pauseButton.setDisable(pauseDisabled);
        endButton.setDisable(endDisabled);
    }

    private boolean showConfirmationDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Pause Work Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to pause your current work?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
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
}