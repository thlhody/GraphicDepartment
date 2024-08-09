package cottontex.graphdep.controllers.user;

import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.database.queries.user.UserTimeOffHandler;
import cottontex.graphdep.database.queries.user.UserTimeTableHandler;
import cottontex.graphdep.models.UserSession;
import cottontex.graphdep.models.WorkHourEntry;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class UserMonthlyTimeController extends UserBaseController {

    @FXML
    private TableView<WorkHourEntry> workHoursTable;
    @FXML
    private TableColumn<WorkHourEntry, String> dateColumn;
    @FXML
    private TableColumn<WorkHourEntry, String> startTimeColumn;
    @FXML
    private TableColumn<WorkHourEntry, Integer> breaksColumn;
    @FXML
    private TableColumn<WorkHourEntry, String> breaksTimeColumn;
    @FXML
    private TableColumn<WorkHourEntry, String> endTimeColumn;
    @FXML
    private TableColumn<WorkHourEntry, String> totalWorkedTimeColumn;
    @FXML
    private Button backButton;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private Button submitButton;


    @FXML
    public void initialize() {
        super.setupLogo();
        initializeDependencies();
        setupTableColumns();
        typeComboBox.getItems().addAll("CO - Holiday", "CM - Medical Leave");
    }

    @Override
    protected void initializeControllerDependencies() {
        // Add any UserMonthlyTimeController-specific dependencies here
        // For example:
        // someSpecificHandler = DependencyFactory.getInstance().createSomeSpecificHandler();
    }

    @Override
    protected boolean initializeUserData() {
        if (!super.initializeUserData()) {
            return false;
        }
        loadUserMonthlyData();
        return true;
    }

    public void loadUserMonthlyData() {
        UserSession userSession = UserSession.getInstance();
        if (userSession == null || userSession.getUserId() == null) {
            LoggerUtility.error("UserSession or UserID is null in UserMonthlyTimeController.loadUserMonthlyData()");
            return;
        }

        List<WorkHourEntry> workHours = timeTableHandler.getUserMonthlyWorkHours(userSession.getUserId());
        LoggerUtility.info("Retrieved " + workHours.size() + " work hour entries");

        if (workHours.isEmpty()) {
            LoggerUtility.warn("No work hour entries found for user " + userSession.getUserId());
        } else {
            workHoursTable.setItems(FXCollections.observableArrayList(workHours));
            LoggerUtility.info("Set " + workHours.size() + " items to the table");
        }
    }

    @FXML
    protected void onBackToUserPageClick() {
        Stage stage = (Stage) workHoursTable.getScene().getWindow();
        loadPage(stage, AppPathsFXML.USER_PAGE_LAYOUT, "User Page", UserSession.getInstance());
    }

    private void setupTableColumns() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        breaksColumn.setCellValueFactory(new PropertyValueFactory<>("breaks"));
        breaksTimeColumn.setCellValueFactory(new PropertyValueFactory<>("breaksTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        totalWorkedTimeColumn.setCellValueFactory(new PropertyValueFactory<>("totalWorkedTimeForDisplay"));
    }

    @Override
    protected Scene getScene() {
        return null;
    }
    @FXML
    protected void onSubmitButtonClick() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String type = typeComboBox.getValue();
        LoggerUtility.info("Submit button clicked.");

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

        UserSession userSession = UserSession.getInstance();
        if (userSession == null) {
            LoggerUtility.error("UserSession is null in UserTimeOffController.onSubmitButtonClick()");
            showAlert("Error", "User session is not available. Please log in again.");
            return;
        }

        boolean success = userTimeOffHandler.saveTimeOff(userSession.getUserId(), workDays, type);
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

}
