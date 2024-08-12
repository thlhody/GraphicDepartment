package cottontex.graphdep.controllers.user;

import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.constants.AppPathsCSS;
import cottontex.graphdep.models.WorkHourEntry;
import cottontex.graphdep.models.managers.UserSessionManager;
import cottontex.graphdep.services.user.UserMonthlyService;
import cottontex.graphdep.utils.LoggerUtility;
import cottontex.graphdep.utils.TableUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class UserMonthlyController extends UserBaseController {

    @FXML
    private TableView<WorkHourEntry> workHoursTable;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private Button submitButton;

    private UserMonthlyService userMonthlyService;

    @FXML
    public void initialize() {
        super.initialize();
        initializeUserDependencies();
        initializeUserComponents();
        loadUserMonthlyData();
    }

    @Override
    protected void initializeUserDependencies() {
        super.initializeUserDependencies();
        this.userMonthlyService = new UserMonthlyService(userTimeTableHandler, userTimeOffHandler);
    }

    @Override
    protected void initializeUserComponents() {
        LoggerUtility.info("Initializing user components for UserMonthlyTimeController");
        setupTable();
        setupTypeComboBox();
    }

    @Override
    protected void initializeWorkState() {
        LoggerUtility.info("Initializing work state for UserMonthlyTimeController");
        loadUserMonthlyData();
    }

    private void setupTypeComboBox() {
        typeComboBox.getItems().addAll("CO - Holiday", "CM - Medical Leave");
    }

    private void setupTable() {
        TableUtils.setupUserMonthlyTimeTable(workHoursTable);
        TableUtils.applyUserTableStyles(workHoursTable, AppPathsCSS.TABLE_STYLES_A, "/cottontex/graphdep/css/user-table-styles.css");
        LoggerUtility.info("Table setup completed for UserMonthlyTimeController");
    }

    @FXML
    protected void onBackToUserPageClick() {
        LoggerUtility.buttonInfo("Back to User Page", UserSessionManager.getSession().getUsername());
        Stage stage = (Stage) workHoursTable.getScene().getWindow();
        LoggerUtility.switchController(this.getClass(), UserBaseController.class, UserSessionManager.getSession().getUsername());
        loadPage(stage, AppPathsFXML.USER_PAGE_LAYOUT, "User Page", UserSessionManager.getSession());
    }

    @FXML
    protected void onSubmitButtonClick() {
        LoggerUtility.buttonInfo("Submit Time Off", UserSessionManager.getSession().getUsername());
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

        boolean success = userMonthlyService.saveTimeOff(UserSessionManager.getSession().getUserId(), startDate, endDate, type);
        if (success) {
            showAlert("Success", "Time off request submitted successfully.");
        } else {
            showAlert("Error", "Failed to submit time off request.");
        }
    }

    private void loadUserMonthlyData() {
        List<WorkHourEntry> workHours = userMonthlyService.getUserMonthlyWorkHours(UserSessionManager.getSession().getUserId());
        if (workHours.isEmpty()) {
            LoggerUtility.warn("No work hour entries found for user: " + UserSessionManager.getSession().getUsername());
        } else {
            workHoursTable.setItems(FXCollections.observableArrayList(workHours));
            LoggerUtility.info("Set " + workHours.size() + " items to the table for user: " + UserSessionManager.getSession().getUsername());
        }
    }
}