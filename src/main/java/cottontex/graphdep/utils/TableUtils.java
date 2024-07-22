package cottontex.graphdep.utils;

import cottontex.graphdep.models.WorkScheduleEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.Map;

public class TableUtils {

    public static void setupTableColumns(TableView<WorkScheduleEntry> scheduleTable) {
        scheduleTable.getColumns().clear();

        TableColumn<WorkScheduleEntry, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        nameColumn.setPrefWidth(150);
        scheduleTable.getColumns().add(nameColumn);

        for (int day = 1; day <= 31; day++) {
            final int dayNumber = day;
            TableColumn<WorkScheduleEntry, String> dayColumn = new TableColumn<>(String.valueOf(day));
            dayColumn.setCellValueFactory(cellData -> cellData.getValue().getDayProperty(dayNumber));
            dayColumn.setPrefWidth(40);
            scheduleTable.getColumns().add(dayColumn);
        }

        TableColumn<WorkScheduleEntry, String> totalColumn = new TableColumn<>("Total");
        totalColumn.setCellValueFactory(cellData -> cellData.getValue().totalProperty());
        totalColumn.setPrefWidth(80);
        scheduleTable.getColumns().add(totalColumn);
    }

    public static ObservableList<WorkScheduleEntry> createWorkScheduleEntries(Map<String, Map<Integer, String>> data) {
        ObservableList<WorkScheduleEntry> entries = FXCollections.observableArrayList();

        for (Map.Entry<String, Map<Integer, String>> entry : data.entrySet()) {
            WorkScheduleEntry workScheduleEntry = new WorkScheduleEntry(entry.getKey());

            double totalHours = 0;
            for (Map.Entry<Integer, String> dayEntry : entry.getValue().entrySet()) {
                String value = dayEntry.getValue() != null ? dayEntry.getValue() : "";
                workScheduleEntry.setDayValue(dayEntry.getKey(), value);

                // Assuming the value is in the format "HH:mm"
                if (!value.isEmpty()) {
                    String[] parts = value.split(":");
                    if (parts.length == 2) {
                        totalHours += Integer.parseInt(parts[0]) + (Integer.parseInt(parts[1]) / 60.0);
                    }
                }
            }

            workScheduleEntry.setTotal(String.format("%.2f", totalHours));
            entries.add(workScheduleEntry);
        }

        return entries;
    }
}