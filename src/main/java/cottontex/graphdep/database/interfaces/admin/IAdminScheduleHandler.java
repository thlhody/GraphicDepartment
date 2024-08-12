package cottontex.graphdep.database.interfaces.admin;

import cottontex.graphdep.models.HolidaySaveResult;

import java.time.LocalDate;
import java.util.Map;

public interface IAdminScheduleHandler {
    Map<String, Map<Integer, String>> getMonthlyWorkData(int year, int month);
    HolidaySaveResult saveNationalHoliday(LocalDate date);
}
