package cottontex.graphdep.controllers.admin;

import cottontex.graphdep.controllers.BaseController;
import cottontex.graphdep.database.queries.AdminScheduleHandler;
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

public class AdminMonthlyTimeController extends BaseController {

    @FXML private ComboBox<Integer> yearComboBox;
    @FXML private ComboBox<Integer> monthComboBox;
    @FXML private Button exportToExcelButton;
    @FXML private TableView<WorkScheduleEntry> scheduleTable;

    private AdminScheduleHandler adminScheduleHandler = new AdminScheduleHandler();

    @FXML
    public void initialize() {
        initializeComboBoxes();
        TableUtils.setupTableColumns(scheduleTable);
    }

    @FXML
    protected void onBackToAdminPageClick() {
        loadPage((Stage) yearComboBox.getScene().getWindow(), "/cottontex/graphdep/fxml/admin/AdminPageLayout.fxml", "Admin Page");
    }

    @FXML
    protected void onViewMonthlyWorkHoursClick() {
        int selectedYear = yearComboBox.getValue();
        int selectedMonth = monthComboBox.getValue();

        Map<String, Map<Integer, String>> data = adminScheduleHandler.getMonthlyWorkData(selectedYear, selectedMonth);
        ObservableList<WorkScheduleEntry> entries = TableUtils.createWorkScheduleEntries(data);
        scheduleTable.setItems(entries);
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

    private void exportToExcel(int year, int month, String filePath) {
        Map<String, Map<Integer, String>> data = adminScheduleHandler.getMonthlyWorkData(year, month);
        try {
            ExportExcelUtils.exportToExcel(data, year, month, filePath);
            showAlert("Success", "Excel file exported successfully.");
        } catch (IOException e) {
            LoggerUtility.error("Error exporting to Excel", e);
            showAlert("Error", "Failed to export Excel file. Please try again.");
        }
    }

    private void initializeComboBoxes() {
        yearComboBox.getItems().addAll(2023, 2024, 2025);
        yearComboBox.setValue(LocalDate.now().getYear());

        for (int i = 1; i <= 12; i++) {
            monthComboBox.getItems().add(i);
        }
        monthComboBox.setValue(LocalDate.now().getMonthValue());
    }
}