package cottontex.graphdep.controllers.admin;

import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.database.interfaces.admin.IAdminScheduleHandler;
import cottontex.graphdep.models.HolidaySaveResult;
import cottontex.graphdep.models.WorkScheduleEntry;
import cottontex.graphdep.models.managers.UserSessionManager;
import cottontex.graphdep.services.admin.AdminMonthlyService;
import cottontex.graphdep.utils.ExportExcelUtils;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

public class AdminMonthlyController extends AdminBaseController {

    private AdminMonthlyService adminMonthlyService;

    @FXML
    private ComboBox<Integer> yearComboBox;
    @FXML
    private ComboBox<Integer> monthComboBox;
    @FXML
    private Button exportToExcelButton;
    @FXML
    private TableView<WorkScheduleEntry> scheduleTable;
    @FXML
    private DatePicker holidayDatePicker;
    @FXML
    private VBox tableContainer;

    @FXML
    @Override
    public void initialize() {
        super.initialize();
        initializeComboBoxes();
        initializeDependencies();
        setupWorkScheduleTable(scheduleTable);
        setupTableContainer();
        holidayDatePicker.setValue(LocalDate.now());
    }

    @Override
    protected void initializeDependencies() {
        super.initializeDependencies();
        adminMonthlyService = new AdminMonthlyService(getHandler(IAdminScheduleHandler.class));
    }


    private void initializeComboBoxes() {
        yearComboBox.getItems().addAll(2023, 2024, 2025);
        yearComboBox.setValue(LocalDate.now().getYear());
        for (int i = 1; i <= 12; i++) {
            monthComboBox.getItems().add(i);
        }
        monthComboBox.setValue(LocalDate.now().getMonthValue());
    }

    @FXML
    protected void onBackToAdminPageClick() {
        LoggerUtility.buttonInfo("Back to Admin Page", UserSessionManager.getSession().getUsername());
        loadAdminPage((Stage) yearComboBox.getScene().getWindow(), AppPathsFXML.ADMIN_PAGE_LAYOUT, "Admin Page", AdminController.class);
    }

    @FXML
    protected void onViewMonthlyWorkHoursClick() {
        LoggerUtility.buttonInfo("View Monthly Work Hours", UserSessionManager.getSession().getUsername());
        Integer selectedYear = yearComboBox.getValue();
        Integer selectedMonth = monthComboBox.getValue();
        LoggerUtility.actionInfo("View Monthly Work Hours", "Year: " + selectedYear + ", Month: " + selectedMonth, UserSessionManager.getSession().getUsername());

        ObservableList<WorkScheduleEntry> entries = adminMonthlyService.getMonthlyWorkDataForDisplay(selectedYear, selectedMonth);
        scheduleTable.setItems(entries);

        scheduleTable.getColumns().forEach(column -> column.setPrefWidth(column.getWidth()));
    }

    @FXML
    protected void onAddNationalHolidayClick() {
        LoggerUtility.buttonInfo("Add National Holiday", UserSessionManager.getSession().getUsername());
        LocalDate selectedDate = holidayDatePicker.getValue();

        if (selectedDate != null) {
            HolidaySaveResult result = adminMonthlyService.saveNationalHoliday(selectedDate);

            if (result.success()) {
                LoggerUtility.info("Success: " + result.message());
                showAlert("Success", result.message());
                onViewMonthlyWorkHoursClick(); // Refresh the table
            } else if (result.duplicate()) {
                LoggerUtility.info("Duplicate: " + result.message());
                showAlert("Information", result.message());
            } else {
                LoggerUtility.error("Error " + result.message());
                showAlert("Error", result.message());
            }
        } else {
            LoggerUtility.error("No date selected for national holiday");
            showAlert("Error", "Please select a date for the national holiday.");
        }
    }

    @FXML
    protected void onExportToExcelButtonClick() {
        LoggerUtility.buttonInfo("Export to Excel", UserSessionManager.getSession().getUsername());
        Integer selectedYear = yearComboBox.getValue();
        Integer selectedMonth = monthComboBox.getValue();

        Window window = exportToExcelButton.getScene().getWindow();
        File file = showSaveFileDialog("WorkSchedule_" + selectedYear + "_" + selectedMonth + ".xlsx", window);
        if (file != null) {
            LoggerUtility.actionInfo("Export to Excel", "File: " + file.getAbsolutePath(), UserSessionManager.getSession().getUsername());
            exportToExcel(selectedYear, selectedMonth, file.getAbsolutePath());
        }
    }

    private void exportToExcel(Integer year, Integer month, String filePath) {
        Map<String, Map<Integer, String>> data = adminMonthlyService.getMonthlyWorkDataForExport(year, month);
        try {
            ExportExcelUtils.exportToExcel(data, year, month, filePath);
            showAlert("Success", "Excel file exported successfully.");
        } catch (IOException e) {
            LoggerUtility.error("Error exporting to Excel", e);
            showAlert("Error", "Failed to export Excel file. Please try again.");
        }
    }

    private void setupTableContainer() {
        // Bind the table's preferred width to its container's width
        scheduleTable.prefWidthProperty().bind(tableContainer.widthProperty());
        // Allow the table to grow vertically
        VBox.setVgrow(scheduleTable, javafx.scene.layout.Priority.ALWAYS);
        // Enable horizontal scrolling
        scheduleTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
    }

}