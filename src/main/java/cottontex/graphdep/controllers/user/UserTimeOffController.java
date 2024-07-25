package cottontex.graphdep.controllers.user;

import cottontex.graphdep.database.queries.user.UserTimeOffHandler;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserTimeOffController {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private Button submitButton;

    @Setter
    private Integer userID;
    private UserTimeOffHandler userTimeOffHandler;

    @FXML
    public void initialize() {
        userTimeOffHandler = new UserTimeOffHandler();
        typeComboBox.getItems().addAll("CO - Holiday", "CM - Medical Leave");
    }

    @FXML
    protected void onSubmitButtonClick() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String type = typeComboBox.getValue();

        if (startDate == null || endDate == null || type == null) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        if (endDate.isBefore(startDate)) {
            showAlert("Error", "End date cannot be before start date.");
            return;
        }

        List<LocalDate> workDays = getWorkDays(startDate, endDate);

        if (workDays.isEmpty()) {
            showAlert("Error", "No workdays selected in the given range.");
            return;
        }

        boolean success = userTimeOffHandler.saveTimeOff(userID, workDays, type);
        if (success) {
            showAlert("Success", "Time off request submitted successfully.");
        } else {
            showAlert("Error", "Failed to submit time off request.");
        }
    }

    private List<LocalDate> getWorkDays(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> workDays = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                workDays.add(currentDate);
            }
            currentDate = currentDate.plusDays(1);
        }

        return workDays;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}