package cottontex.graphdep.utils;

import cottontex.graphdep.models.WorkScheduleEntry;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Map;

public class TableUtils {

    public static void setupTableColumns(TableView<WorkScheduleEntry> scheduleTable) {
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

    public static ObservableList<WorkScheduleEntry> createWorkScheduleEntries(Map<String, Map<Integer, String>> data) {
        ObservableList<WorkScheduleEntry> entries = FXCollections.observableArrayList();
        for (Map.Entry<String, Map<Integer, String>> entry : data.entrySet()) {
            String name = entry.getKey();
            Map<Integer, String> dailyData = entry.getValue();

            int totalMinutes = DateTimeUtils.calculateTotalMinutes(dailyData);
            String total = DateTimeUtils.formatTotalTime(totalMinutes);

            entries.add(new WorkScheduleEntry(name, dailyData, total));
        }
        return entries;
    }
}