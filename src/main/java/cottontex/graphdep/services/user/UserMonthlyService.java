package cottontex.graphdep.services.user;

import cottontex.graphdep.database.interfaces.user.IUserTimeTableHandler;
import cottontex.graphdep.database.interfaces.user.IUserTimeOffHandler;
import cottontex.graphdep.models.WorkHourEntry;
import cottontex.graphdep.utils.LoggerUtility;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserMonthlyService {
    private final IUserTimeTableHandler userTimeTableHandler;
    private final IUserTimeOffHandler userTimeOffHandler;

    public UserMonthlyService(IUserTimeTableHandler userTimeTableHandler, IUserTimeOffHandler userTimeOffHandler) {
        this.userTimeTableHandler = userTimeTableHandler;
        this.userTimeOffHandler = userTimeOffHandler;
    }

    public List<WorkHourEntry> getUserMonthlyWorkHours(int userId) {
        LoggerUtility.initialize(this.getClass(), "Getting user monthly work hours");
        List<WorkHourEntry> workHours = userTimeTableHandler.getUserMonthlyWorkHours(userId);
        LoggerUtility.info("Retrieved " + workHours.size() + " work hour entries for user ID: " + userId);
        return workHours;
    }

    public boolean saveTimeOff(int userId, LocalDate startDate, LocalDate endDate, String type) {
        List<LocalDate> workDays = getWorkDays(startDate, endDate);

        if (workDays.isEmpty()) {
            LoggerUtility.warn("No workdays in selected date range");
            return false;
        }

        boolean success = userTimeOffHandler.saveTimeOff(userId, workDays, type);
        if (success) {
            LoggerUtility.actionInfo("Time Off Request", "Submitted successfully for user ID: " + userId, String.valueOf(userId));
        } else {
            LoggerUtility.error("Failed to submit time off request for user ID: " + userId);
        }
        return success;
    }

    public List<LocalDate> getWorkDays(LocalDate startDate, LocalDate endDate) {
        LoggerUtility.debug("Calculating work days between " + startDate + " and " + endDate);
        List<LocalDate> workDays = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                workDays.add(currentDate);
            }
            currentDate = currentDate.plusDays(1);
        }

        LoggerUtility.debug("Found " + workDays.size() + " work days in the given range");
        return workDays;
    }
}