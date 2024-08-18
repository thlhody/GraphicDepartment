package cottontex.graphdep.controllers.user;

import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.constants.AppPathsCSS;
import cottontex.graphdep.models.WorkHourEntry;
import cottontex.graphdep.models.managers.EUserSessionManager;
import cottontex.graphdep.services.user.UserMonthlyService;
import cottontex.graphdep.utils.LoggerUtility;
import cottontex.graphdep.utils.TableUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class EUserMonthlyController extends EUserBaseController {

    private UserMonthlyService userMonthlyService;

    @FXML private TableView<WorkHourEntry> workHoursTable;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private Button submitButton;

    @Override
    protected void initializeDependencies() {
        super.initializeDependencies();
        this.userMonthlyService = new UserMonthlyService(userTimeTableHandler, userTimeOffHandler);
        LoggerUtility.info("Initializing EUserMonthlyController dependencies");
    }

    @Override
    protected void initializeComponents() {
        super.initializeComponents();
        setupTable();
        setupTypeComboBox();
        loadUserMonthlyData();
        LoggerUtility.info("Initializing user components for EUserMonthlyController");
    }

    private void setupTypeComboBox() {
        typeComboBox.getItems().addAll("CO - Holiday", "CM - Medical Leave");
    }

    private void setupTable() {
        TableUtils.setupUserMonthlyTimeTable(workHoursTable);
        TableUtils.applyUserTableStyles(workHoursTable, AppPathsCSS.TABLE_STYLES_A, "/cottontex/graphdep/css/user-table-styles.css");
        LoggerUtility.info("Table setup completed for EUserMonthlyController");
    }

    @FXML
    protected void onBackToUserPageClick() {
        LoggerUtility.buttonInfo("Back to User Page", EUserSessionManager.getCurrentUsername());
        EUserSessionManager.getSession().ifPresent(session -> {
            Stage stage = (Stage) workHoursTable.getScene().getWindow();
            LoggerUtility.switchController(this.getClass(), EUserController.class, session.getUsername());
            loadPage(stage, AppPathsFXML.USER_PAGE_LAYOUT, "User Page", session);
        });
    }

    @FXML
    protected void onSubmitButtonClick() {
        LoggerUtility.buttonInfo("Submit Time Off", EUserSessionManager.getCurrentUsername());
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String type = typeComboBox.getValue();

        if (startDate == null || endDate == null || type == null) {
            LoggerUtility.warn("Incomplete form submission attempt");
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        if (endDate.isBefore(startDate)) {
            LoggerUtility.warn("Invalid date range submitted");
            showAlert("Error", "End date cannot be before start date.");
            return;
        }

        EUserSessionManager.getSession().ifPresentOrElse(
                session -> {
                    boolean success = userMonthlyService.saveTimeOff(session.getUserId(), startDate, endDate, type);
                    if (success) {
                        showAlert("Success", "Time off request submitted successfully.");
                        loadUserMonthlyData(); // Refresh the data after submission
                    } else {
                        showAlert("Error", "Failed to submit time off request.");
                    }
                },
                () -> {
                    LoggerUtility.error("Attempt to submit time off without active session");
                    showAlert("Error", "Session expired. Please log in again.");
                }
        );
    }

    private void loadUserMonthlyData() {
        EUserSessionManager.getSession().ifPresentOrElse(
                session -> {
                    List<WorkHourEntry> workHours = userMonthlyService.getUserMonthlyWorkHours(session.getUserId());
                    if (workHours.isEmpty()) {
                        LoggerUtility.warn("No work hour entries found for user: " + session.getUsername());
                    } else {
                        workHoursTable.setItems(FXCollections.observableArrayList(workHours));
                        LoggerUtility.info("Set " + workHours.size() + " items to the table for user: " + session.getUsername());
                    }
                },
                () -> {
                    LoggerUtility.error("Attempt to load user monthly data without active session");
                    showAlert("Error", "Session expired. Please log in again.");
                }
        );
    }

    @Override
    protected void handleUserDataInitializationFailure() {
        super.handleUserDataInitializationFailure();
        showAlert("Access Denied", "You do not have the required permissions to access this page.");
        redirectToLogin();
    }
}