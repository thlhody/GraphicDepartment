package cottontex.graphdep.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
@Getter
@Setter
@AllArgsConstructor
public class WorkScheduleEntry {
    private final String name;
    private final Map<Integer, String> dailyHours;
    private final String total;
    public String getDay(int day) {
        return dailyHours.getOrDefault(day, "");
    }
}
