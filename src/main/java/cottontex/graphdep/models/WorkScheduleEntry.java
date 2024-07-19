package cottontex.graphdep.models;


import java.util.Map;

public class WorkScheduleEntry {
    private final String name;
    private final Map<Integer, String> dailyHours;
    private final String total;

    public WorkScheduleEntry(String name, Map<Integer, String> dailyHours, String total) {
        this.name = name;
        this.dailyHours = dailyHours;
        this.total = total;
    }

    public String getName() { return name; }
    public String getDay(int day) { return dailyHours.getOrDefault(day, ""); }
    public String getTotal() { return total; }
}
