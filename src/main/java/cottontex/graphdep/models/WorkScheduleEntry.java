package cottontex.graphdep.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


import java.util.HashMap;
import java.util.Map;

public class WorkScheduleEntry {
    private final StringProperty name;
    private final Map<Integer, StringProperty> days;
    private final StringProperty total;

    public WorkScheduleEntry(String name) {
        this.name = new SimpleStringProperty(name);
        this.days = new HashMap<>();
        for (int i = 1; i <= 31; i++) {
            days.put(i, new SimpleStringProperty(""));
        }
        this.total = new SimpleStringProperty("");
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty getDayProperty(int day) {
        return days.getOrDefault(day, new SimpleStringProperty(""));
    }

    public void setDayValue(int day, String value) {
        days.computeIfAbsent(day, k -> new SimpleStringProperty()).set(value);
    }

    public StringProperty totalProperty() {
        return total;
    }

    public void setTotal(String value) {
        total.set(value);
    }

}