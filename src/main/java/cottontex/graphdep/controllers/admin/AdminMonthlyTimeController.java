package cottontex.graphdep.controllers.admin;

import cottontex.graphdep.constants.AppPathsCSS;
import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.controllers.common.BaseController;
import cottontex.graphdep.database.queries.admin.AdminScheduleHandler;
import cottontex.graphdep.models.UserSession;
import cottontex.graphdep.models.WorkScheduleEntry;
import cottontex.graphdep.utils.TableUtils;
import cottontex.graphdep.utils.ExportExcelUtils;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

public class AdminMonthlyTimeController extends BaseController {

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

    private AdminScheduleHandler adminScheduleHandler = new AdminScheduleHandler();

    @FXML
    public void initialize() {
        super.setupLogo();
        initializeComboBoxes();
        TableUtils.setupTableColumns(scheduleTable);
        scheduleTable.setFixedCellSize(25);
        scheduleTable.setPrefHeight(scheduleTable.getFixedCellSize() * 22.5);
        scheduleTable.getStyleClass().add("alternating-row-colors");
        scheduleTable.getStylesheets().add(Objects.requireNonNull(getClass().getResource(AppPathsCSS.TABLE_STYLES_A)).toExternalForm());
        holidayDatePicker.setValue(LocalDate.now());

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
        loadPage((Stage) yearComboBox.getScene().getWindow(), AppPathsFXML.ADMIN_PAGE_LAYOUT, "Admin Page", UserSession.getInstance());
    }

    @FXML
    protected void onViewMonthlyWorkHoursClick() {
        int selectedYear = yearComboBox.getValue();
        int selectedMonth = monthComboBox.getValue();

        Map<String, Map<Integer, String>> data = adminScheduleHandler.getMonthlyWorkData(selectedYear, selectedMonth);

        // Remove admin user from the data
        data.remove("Admin");
        ObservableList<WorkScheduleEntry> entries = TableUtils.createWorkScheduleEntries(data);
        scheduleTable.setItems(entries);

        // Set the preferred height to show only the visible users
        double headerHeight = 500; // Approximate height of the header
        double rowHeight = scheduleTable.getFixedCellSize();
        double tableHeight = headerHeight + (rowHeight * entries.size());
        scheduleTable.setPrefHeight(tableHeight);
    }

    @FXML
    protected void onExportToExcelButtonClick() {
        int selectedYear = yearComboBox.getValue();
        int selectedMonth = monthComboBox.getValue();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Excel File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("WorkSchedule_" + selectedYear + "_" + selectedMonth + ".xlsx");

        File file = fileChooser.showSaveDialog(exportToExcelButton.getScene().getWindow());
        if (file != null) {
            exportToExcel(selectedYear, selectedMonth, file.getAbsolutePath());
        }
    }

    @FXML
    protected void onAddNationalHolidayClick() {
        LocalDate selectedDate = holidayDatePicker.getValue();

        if (selectedDate != null) {
            System.out.println("Calling saveNationalHoliday with date: " + selectedDate);
            // Assuming saveNationalHoliday returns an instance of HolidaySaveResult
            AdminScheduleHandler.HolidaySaveResult result = adminScheduleHandler.saveNationalHoliday(selectedDate);

            // Handling the result based on assumed fields or methods
            // Replace 'isSuccess()' with the actual method or field available in HolidaySaveResult
            if (result != null) {
                // Example checks - adapt based on actual API
                if (result.isSuccess()) {
                    showAlert("Success", "National holiday added successfully for all users on " + selectedDate.toString());
                    onViewMonthlyWorkHoursClick(); // Refresh the table
                } else if (result.isDuplicate()) {
                    showAlert("Information", "National holiday already exists for " + selectedDate.toString() + ". No changes were made.");
                } else {
                    showAlert("Error", "Failed to add national holiday. Please try again.");
                }
            } else {
                showAlert("Error", "Unknown error occurred. Please try again.");
            }
        } else {
            showAlert("Error", "Please select a date for the national holiday.");
        }
    }

    private void exportToExcel(int year, int month, String filePath) {
        Map<String, Map<Integer, String>> data = adminScheduleHandler.getMonthlyWorkData(year, month);
        data.remove("Admin");
        try {
            ExportExcelUtils.exportToExcel(data, year, month, filePath);
            showAlert("Success", "Excel file exported successfully.");
        } catch (IOException e) {
            LoggerUtility.error("Error exporting to Excel", e);
            showAlert("Error", "Failed to export Excel file. Please try again.");
        }
    }
}
