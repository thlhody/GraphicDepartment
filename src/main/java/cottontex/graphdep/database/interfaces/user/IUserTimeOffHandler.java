package cottontex.graphdep.database.interfaces.user;

import java.time.LocalDate;
import java.util.List;

public interface IUserTimeOffHandler {
    boolean saveTimeOff(Integer userID, List<LocalDate> workDays, String type);
}