package cottontex.graphdep.controllers;

import cottontex.graphdep.database.queries.AdminScheduleHandler;
import cottontex.graphdep.database.queries.UserManagementHandler;
import cottontex.graphdep.models.WorkScheduleEntry;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.File;
import java.time.LocalDate;
import java.util.Map;

public class AdminController extends BaseController {

    @FXML private Label welcomeLabel;
    @FXML private Button viewWorkDataButton;
    @FXML private Button logoutButton;
    @FXML private TextField nameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button addUserButton;
    @FXML private Button exportToExcelButton;
    @FXML private ComboBox<Integer> yearComboBox;
    @FXML private ComboBox<Integer> monthComboBox;
    @FXML private TableView<WorkScheduleEntry> scheduleTable;

    @Setter private Integer userID;
    private String username;

    private AdminScheduleHandler adminScheduleHandler = new AdminScheduleHandler();
    private UserManagementHandler userManagementHandler = new UserManagementHandler();

    @FXML
    public void initialize() {
        initializeComboBoxes();
        setupTableColumns();
    }

    public void setUsername(String username) {
        this.username = username;
        welcomeLabel.setText("Welcome, " + username + "!");
    }

    @FXML
    protected void onViewWorkDataButtonClick() {
        int selectedYear = yearComboBox.getValue();
        int selectedMonth = monthComboBox.getValue();

        Map<String, Map<Integer, String>> data = adminScheduleHandler.getMonthlyWorkData(selectedYear, selectedMonth);
        ObservableList<WorkScheduleEntry> entries = createWorkScheduleEntries(data);
        scheduleTable.setItems(entries);
    }

    @FXML
    protected void onAddUserButtonClick() {
        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "All fields must be filled.");
            return;
        }

        boolean success = userManagementHandler.addUser(name, username, password);
        if (success) {
            showAlert("Success", "User added successfully.");
            clearFields();
        } else {
            showAlert("Error", "Failed to add user. Username already exist.");
        }
    }

    @FXML
    protected void onLogoutAdminButtonClick() {
        loadPage((Stage) logoutButton.getScene().getWindow(), "/cottontex/graphdep/fxml/launcher.fxml", "Graphics Department Login");
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

    private void clearFields() {
        nameField.clear();
        usernameField.clear();
        passwordField.clear();
    }
}