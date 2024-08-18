package cottontex.graphdep.controllers.admin;

import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.constants.AppPathsCSS;
import cottontex.graphdep.database.interfaces.admin.IAdminScheduleHandler;
import cottontex.graphdep.models.HolidaySaveResult;
import cottontex.graphdep.models.WorkScheduleEntry;
import cottontex.graphdep.models.managers.EUserSessionManager;
import cottontex.graphdep.services.admin.AdminMonthlyService;
import cottontex.graphdep.utils.ExportExcelUtils;
import cottontex.graphdep.utils.LoggerUtility;
import cottontex.graphdep.utils.TableUtils;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

public class EAdminMonthlyController extends EAdminBaseController {

    private AdminMonthlyService adminMonthlyService;

    @FXML private ComboBox<Integer> yearComboBox;
    @FXML private ComboBox<Integer> monthComboBox;
    @FXML private Button exportToExcelButton;
    @FXML private TableView<WorkScheduleEntry> scheduleTable;
    @FXML private DatePicker holidayDatePicker;

    @Override
    protected void initializeDependencies() {
        super.initializeDependencies();
        this.adminMonthlyService = new AdminMonthlyService(getDependency(IAdminScheduleHandler.class));
    }

    @Override
    protected void initializeComponents() {
        super.initializeComponents();
        initializeComboBoxes();
        setupWorkScheduleTable(scheduleTable);
        holidayDatePicker.setValue(LocalDate.now());
    }

    private void initializeComboBoxes() {
        yearComboBox.getItems().addAll(LocalDate.now().getYear() - 1, LocalDate.now().getYear(), LocalDate.now().getYear() + 1);
        yearComboBox.setValue(LocalDate.now().getYear());
        for (int i = 1; i <= 12; i++) {
            monthComboBox.getItems().add(i);
        }
        monthComboBox.setValue(LocalDate.now().getMonthValue());
    }

    @FXML
    protected void onBackToAdminPageClick() {
        LoggerUtility.buttonInfo("Back to Admin Page", EUserSessionManager.getCurrentUsername());
        loadAdminPage((Stage) yearComboBox.getScene().getWindow(), AppPathsFXML.ADMIN_PAGE_LAYOUT, "Admin Page", EAdminController.class);
    }

    @FXML
    protected void onViewMonthlyWorkHoursClick() {
        LoggerUtility.buttonInfo("View Monthly Work Hours", EUserSessionManager.getCurrentUsername());
        Integer selectedYear = yearComboBox.getValue();
        Integer selectedMonth = monthComboBox.getValue();
        LoggerUtility.actionInfo("View Monthly Work Hours", "Year: " + selectedYear + ", Month: " + selectedMonth, EUserSessionManager.getCurrentUsername());

        try {
            ObservableList<WorkScheduleEntry> entries = adminMonthlyService.getMonthlyWorkDataForDisplay(selectedYear, selectedMonth);
            scheduleTable.setItems(entries);
            scheduleTable.refresh();
        } catch (Exception e) {
            LoggerUtility.error("Error fetching monthly work data", e);
            showAlert("Error", "Failed to fetch monthly work data. Please try again.");
        }
    }

    @FXML
    protected void onAddNationalHolidayClick() {
        LoggerUtility.buttonInfo("Add National Holiday", EUserSessionManager.getCurrentUsername());
        LocalDate selectedDate = holidayDatePicker.getValue();

        if (selectedDate == null) {
            LoggerUtility.warn("No date selected for national holiday");
            showAlert("Error", "Please select a date for the national holiday.");
            return;
        }

        try {
            HolidaySaveResult result = adminMonthlyService.saveNationalHoliday(selectedDate);
            handleHolidaySaveResult(result);
        } catch (Exception e) {
            LoggerUtility.error("Error saving national holiday", e);
            showAlert("Error", "An unexpected error occurred while saving the national holiday.");
        }
    }

    private void handleHolidaySaveResult(HolidaySaveResult result) {
        if (result.success()) {
            LoggerUtility.info("National holiday saved successfully: " + result.message());
            showAlert("Success", result.message());
            onViewMonthlyWorkHoursClick(); // Refresh the table
        } else if (result.duplicate()) {
            LoggerUtility.info("Duplicate national holiday: " + result.message());
            showAlert("Information", result.message());
        } else {
            LoggerUtility.error("Failed to save national holiday: " + result.message());
            showAlert("Error", result.message());
        }
    }

    @FXML
    protected void onExportToExcelButtonClick() {
        LoggerUtility.buttonInfo("Export to Excel", EUserSessionManager.getCurrentUsername());
        Integer selectedYear = yearComboBox.getValue();
        Integer selectedMonth = monthComboBox.getValue();

        Window window = exportToExcelButton.getScene().getWindow();
        File file = showSaveFileDialog("WorkSchedule_" + selectedYear + "_" + selectedMonth + ".xlsx", window);
        if (file != null) {
            LoggerUtility.actionInfo("Export to Excel", "File: " + file.getAbsolutePath(), EUserSessionManager.getCurrentUsername());
            exportToExcel(selectedYear, selectedMonth, file.getAbsolutePath());
        }
    }

    private void exportToExcel(Integer year, Integer month, String filePath) {
        try {
            LoggerUtility.info("Starting Excel export for year: " + year + ", month: " + month);
            Map<String, Map<Integer, String>> data = adminMonthlyService.getMonthlyWorkDataForExport(year, month);

            if (data.isEmpty()) {
                LoggerUtility.warn("No data available for export");
                showAlert("Warning", "No data available for the selected period.");
                return;
            }

            ExportExcelUtils.exportToExcel(data, year, month, filePath);
            LoggerUtility.info("Excel file exported successfully to: " + filePath);
            showAlert("Success", "Excel file exported successfully.");
        } catch (IOException e) {
            LoggerUtility.error("Error exporting to Excel: " + e.getMessage(), e);
            showAlert("Error", "Failed to export Excel file. Please try again.");
        } catch (Exception e) {
            LoggerUtility.error("Unexpected error during Excel export: " + e.getMessage(), e);
            showAlert("Error", "An unexpected error occurred during export. Please try again.");
        }
    }
}