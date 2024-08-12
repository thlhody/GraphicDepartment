package cottontex.graphdep.database.interfaces.user;

import cottontex.graphdep.models.WorkHourEntry;
import java.util.List;

public interface IUserTimeTableHandler {
    List<WorkHourEntry> getUserMonthlyWorkHours(Integer userId);
}
