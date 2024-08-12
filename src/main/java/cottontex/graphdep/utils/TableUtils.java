package cottontex.graphdep.utils;

import cottontex.graphdep.models.WorkHourEntry;
import cottontex.graphdep.models.WorkScheduleEntry;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class TableUtils {

    public static <T> void setupGenericTableColumns(TableView<T> table, Map<String, Function<T, ?>> columnMappings) {
        table.getColumns().clear();

        for (Map.Entry<String, Function<T, ?>> entry : columnMappings.entrySet()) {
            TableColumn<T, Object> column = new TableColumn<>(entry.getKey());
            column.setCellValueFactory(cellData -> {
                T value = cellData.getValue();
                Object cellValue = entry.getValue().apply(value);
                if (cellValue instanceof ObservableValue) {
                    return (ObservableValue<Object>) cellValue;
                } else {
                    return new SimpleObjectProperty<>(cellValue);
                }
            });
            table.getColumns().add(column);
        }
    }

    public static void setupWorkScheduleColumns(TableView<WorkScheduleEntry> scheduleTable) {
        scheduleTable.getColumns().clear();

        TableColumn<WorkScheduleEntry, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        nameColumn.setPrefWidth(150);
        scheduleTable.getColumns().add(nameColumn);

        for (int day = 1; day <= 31; day++) {
            TableColumn<WorkScheduleEntry, String> dayColumn = getWorkScheduleEntryStringTableColumn(day);
            scheduleTable.getColumns().add(dayColumn);
        }

        TableColumn<WorkScheduleEntry, String> totalColumn = new TableColumn<>("Total");
        totalColumn.setCellValueFactory(cellData -> cellData.getValue().totalProperty());
        totalColumn.setPrefWidth(80);
        scheduleTable.getColumns().add(totalColumn);
    }

    private static TableColumn<WorkScheduleEntry, String> getWorkScheduleEntryStringTableColumn(int day) {
        final int dayNumber = day;
        TableColumn<WorkScheduleEntry, String> dayColumn = new TableColumn<>(String.valueOf(day));
        dayColumn.setCellValueFactory(cellData -> cellData.getValue().getDayProperty(dayNumber));
        dayColumn.setPrefWidth(40);
        dayColumn.setCellFactory(column -> new TableCell<WorkScheduleEntry, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("CO") || item.equals("CM") || item.equals("NH")) {
                        setStyle("-fx-background-color: #F0E68C;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
        return dayColumn;
    }

    public static ObservableList<WorkScheduleEntry> createWorkScheduleEntries(Map<String, Map<Integer, String>> data) {
        ObservableList<WorkScheduleEntry> entries = FXCollections.observableArrayList();

        for (Map.Entry<String, Map<Integer, String>> entry : data.entrySet()) {
            WorkScheduleEntry workScheduleEntry = new WorkScheduleEntry(entry.getKey());

            int totalMinutes = 0;
            for (Map.Entry<Integer, String> dayEntry : entry.getValue().entrySet()) {
                String value = dayEntry.getValue() != null ? dayEntry.getValue() : "";
                workScheduleEntry.setDayValue(dayEntry.getKey(), value);

                // Assuming the value is in the format "HH:mm" or a special code
                if (!value.isEmpty()) {
                    if (value.equals("CO") || value.equals("CM") || value.equals("SN")) {
                        // For special codes, we don't add to total hours
                        continue;
                    }
                    totalMinutes += DateTimeUtils.calculateMinutes(value);
                }
            }
            workScheduleEntry.setTotal(DateTimeUtils.formatTotalTime(totalMinutes));
            entries.add(workScheduleEntry);
        }
        return entries;
    }

    public static void setupUserMonthlyTimeTable(TableView<WorkHourEntry> table) {
        Map<String, Function<WorkHourEntry, ?>> columnMappings = new LinkedHashMap<>();
        columnMappings.put("Date", WorkHourEntry::getDate);
        columnMappings.put("Start Time", WorkHourEntry::getStartTime);
        columnMappings.put("End Time", WorkHourEntry::getEndTime);
        columnMappings.put("Breaks", WorkHourEntry::getBreaks);
        columnMappings.put("Breaks Time", WorkHourEntry::getBreaksTime);
        columnMappings.put("Total Worked Time", WorkHourEntry::getTotalWorkedTimeForDisplay);

        setupGenericTableColumns(table, columnMappings);

        // Add specific column styles
        table.getColumns().forEach(column -> {
            if (column.getText().equals("Date")) {
                column.getStyleClass().add("date-column");
            } else if (column.getText().contains("Time")) {
                column.getStyleClass().add("time-column");
            }
        });
    }

    public static void applyUserTableStyles(TableView<?> table, String userTableCss, String defaultCss) {
        String cssPath = findValidCssPath(userTableCss, defaultCss);
        if (cssPath != null) {
            applyTableStyles(table, cssPath);
            LoggerUtility.info("Applied table styles: " + cssPath);
        } else {
            LoggerUtility.error("No valid CSS file found. Table will use JavaFX default styling.");
        }
    }

    private static String findValidCssPath(String... cssPaths) {
        for (String cssPath : cssPaths) {
            URL resource = TableUtils.class.getResource(cssPath);
            if (resource != null) {
                LoggerUtility.info("Found valid CSS file: " + cssPath);
                return cssPath;
            } else {
                LoggerUtility.warn("CSS file not found: " + cssPath);
            }
        }
        return null;
    }

    public static void applyTableStyles(TableView<?> table, String cssPath) {
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("alternating-row-colors");

        URL cssUrl = TableUtils.class.getResource(cssPath);
        if (cssUrl != null) {
            String externalForm = cssUrl.toExternalForm();
            table.getStylesheets().add(externalForm);
            LoggerUtility.info("Added stylesheet: " + externalForm);
        } else {
            LoggerUtility.error("Failed to load CSS file: " + cssPath);
        }
    }

}