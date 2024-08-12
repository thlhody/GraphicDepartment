package cottontex.graphdep.services.admin;

import cottontex.graphdep.database.interfaces.admin.IAdminScheduleHandler;
import cottontex.graphdep.models.HolidaySaveResult;
import cottontex.graphdep.models.WorkScheduleEntry;
import cottontex.graphdep.utils.LoggerUtility;
import cottontex.graphdep.utils.TableUtils;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.Map;

public class AdminMonthlyService {

    private final IAdminScheduleHandler adminScheduleHandler;

    public AdminMonthlyService(IAdminScheduleHandler adminScheduleHandler) {
        this.adminScheduleHandler = adminScheduleHandler;
    }

    public ObservableList<WorkScheduleEntry> getMonthlyWorkDataForDisplay(Integer year, Integer month) {
        Map<String, Map<Integer, String>> data = adminScheduleHandler.getMonthlyWorkData(year, month);
        data.remove("Admin");
        return TableUtils.createWorkScheduleEntries(data);
    }

    public Map<String, Map<Integer, String>> getMonthlyWorkDataForExport(Integer year, Integer month) {
        Map<String, Map<Integer, String>> data = adminScheduleHandler.getMonthlyWorkData(year, month);
        data.remove("Admin");
        return data;
    }

    public HolidaySaveResult saveNationalHoliday(LocalDate selectedDate) {
        LoggerUtility.actionInfo("Add National Holiday", "Date: " + selectedDate, "Admin");
        return adminScheduleHandler.saveNationalHoliday(selectedDate);
    }
}