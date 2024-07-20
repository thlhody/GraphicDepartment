package cottontex.graphdep.controllers;

import cottontex.graphdep.database.queries.AdminScheduleHandler;
import cottontex.graphdep.models.WorkScheduleEntry;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.collections.FXCollections;

import java.io.File;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.IntStream;

public class AdminMonthlyTimeController extends BaseController {

    @FXML private ComboBox<Integer> yearComboBox;
    @FXML private ComboBox<Integer> monthComboBox;
    @FXML private TableView<WorkScheduleEntry> monthlyTimeTable;
    @FXML private Button exportToExcelButton;
    @FXML private TableView<WorkScheduleEntry> scheduleTable;

    private AdminScheduleHandler adminScheduleHandler = new AdminScheduleHandler();

    @FXML
    public void initialize() {
        initializeComboBoxes();
        setupTableColumns();
    }
    @FXML
    protected void onBackToAdminPageClick() {
        loadPage((Stage) yearComboBox.getScene().getWindow(), "/cottontex/graphdep/fxml/AdminPageLayout.fxml", "Admin Page");
    }

    @FXML
    protected void onViewMonthlyWorkHoursClick() {
        int selectedYear = yearComboBox.getValue();
        int selectedMonth = monthComboBox.getValue();

        Map<String, Map<Integer, String>> data = adminScheduleHandler.getMonthlyWorkData(selectedYear, selectedMonth);
        ObservableList<WorkScheduleEntry> entries = createWorkScheduleEntries(data);
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
            adminScheduleHandler.exportToExcel(selectedYear, selectedMonth, file.getAbsolutePath());
            showAlert("Success", "Excel file exported successfully.");
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

    private void setupTableColumns() {
        TableColumn<WorkScheduleEntry, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        scheduleTable.getColumns().add(nameColumn);

        for (int day = 1; day <= 31; day++) {
            final int currentDay = day;
            TableColumn<WorkScheduleEntry, String> dayColumn = new TableColumn<>(String.valueOf(day));
            dayColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getDay(currentDay)));
            scheduleTable.getColumns().add(dayColumn);
        }

        TableColumn<WorkScheduleEntry, String> totalColumn = new TableColumn<>("Total");
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        scheduleTable.getColumns().add(totalColumn);
    }

    private ObservableList<WorkScheduleEntry> createWorkScheduleEntries(Map<String, Map<Integer, String>> data) {
        ObservableList<WorkScheduleEntry> entries = FXCollections.observableArrayList();
        for (Map.Entry<String, Map<Integer, String>> entry : data.entrySet()) {
            String name = entry.getKey();
            Map<Integer, String> dailyData = entry.getValue();

            int totalMinutes = calculateTotalMinutes(dailyData);
            String total = String.format("%d:%02d", totalMinutes / 60, totalMinutes % 60);

            entries.add(new WorkScheduleEntry(name, dailyData, total));
        }
        return entries;
    }

    private int calculateTotalMinutes(Map<Integer, String> dailyData) {
        return dailyData.values().stream()
                .mapToInt(s -> {
                    String[] parts = s.split(":");
                    return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
                }).sum();
    }
}