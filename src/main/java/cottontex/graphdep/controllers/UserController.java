package cottontex.graphdep.controllers;

import cottontex.graphdep.database.queries.ScheduleUserTable;
import cottontex.graphdep.loggerUtility.LoggerUtility;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import java.time.LocalTime;
import java.util.Optional;

public class UserController {

    @FXML
    private DatePicker datePicker;
    @FXML
    private LocalTimePicker timePicker;

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label errorLabel;

    @FXML
    private Button startButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button endButton;
    @FXML
    private Button logoutButton;


    ScheduleUserTable scheduleUserTable = new ScheduleUserTable();

    @Setter
    private Integer userID;


    public void setUsername(String username) {
        welcomeLabel.setText("Welcome " + username + "!");
        datePicker.setValue(LocalDate.now());
    }

    @FXML
    protected void onLogoutButtonClick() {
        try {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/cottontex/graphdep/launcher.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
            stage.setScene(scene);
            stage.setTitle("Graphics Department Login");
        } catch (IOException e) {
            LoggerUtility.errorInfo(e.getMessage());
        }
    }

    @FXML
    protected void onStartButtonClick() {
        LocalDate date = datePicker.getValue();
        LocalTime startHour = timePicker.getLocalTime();
        LocalDateTime startDateTime = LocalDateTime.of(date, startHour);
        Timestamp startTimestamp = Timestamp.valueOf(startDateTime);

        // Check if the start hour already exists
        if (scheduleUserTable.isStartHourExists(userID, startTimestamp)) {
            errorLabel.setText("You already started work at this hour.");
            LoggerUtility.errorInfo("You already started work at this hour.");
            return; // Exit the method without saving
        }
        // Save the start hour
        scheduleUserTable.saveStartHour(userID, startTimestamp);
        LoggerUtility.infoTest("Start hour saved successfully!");
        errorLabel.setText("Start hour saved successfully!");
        endButton.setDisable(false);
        pauseButton.setDisable(false);
        startButton.setDisable(true);


    }

    @FXML
    protected void onPauseButtonClick() {
        LocalDate date = datePicker.getValue();
        LocalTime pauseHour = timePicker.getLocalTime();
        LocalDateTime pauseDateTime = LocalDateTime.of(date, pauseHour);
        Timestamp pauseTimestamp = Timestamp.valueOf(pauseDateTime);

        if (!scheduleUserTable.hasStartHour(userID, Date.valueOf(date))) {
            errorLabel.setText("No active work period to pause.");
            LoggerUtility.errorInfo("No active work period to pause.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Pause Work Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to pause your current work?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            scheduleUserTable.savePauseTime(userID, pauseTimestamp);
            LoggerUtility.infoTest("Work paused successfully!");
            errorLabel.setText("Work paused successfully!");
            startButton.setDisable(false);
        }
    }

    @FXML
    protected void onEndButtonClick() {
        LocalDate date = datePicker.getValue();
        LocalTime endHour = timePicker.getLocalTime();
        LocalDateTime endDateTime = LocalDateTime.of(date, endHour);
        Timestamp endTimestamp = Timestamp.valueOf(endDateTime);

        if (!scheduleUserTable.hasStartHour(userID, Date.valueOf(date))) {
            errorLabel.setText("No active work period to pause.");
            LoggerUtility.errorInfo("No active work period to pause.");
            return;
        }

        try {
            scheduleUserTable.finalizeWorkDay(userID, endTimestamp, Date.valueOf(date));
            LoggerUtility.infoTest("End hour updated and total hours calculated successfully!");
            errorLabel.setText("Work day finalized successfully!");
            startButton.setDisable(false);
        } catch (Exception e) {
            LoggerUtility.errorInfo("Error finalizing work day: " + e.getMessage());
            errorLabel.setText("Error finalizing work day.");
        }
    }


}

