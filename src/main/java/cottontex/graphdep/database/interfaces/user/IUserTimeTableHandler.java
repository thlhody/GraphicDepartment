package cottontex.graphdep.database.interfaces.user;

import cottontex.graphdep.models.TimeProcessingOffline;
import cottontex.graphdep.models.WorkHourEntry;
import cottontex.graphdep.models.WorkSessionStateOffline;

import java.util.List;

public interface IUserTimeTableHandler {
    List<WorkHourEntry> getUserMonthlyWorkHours(Integer userId);

    List<TimeProcessingOffline> getAllTimeProcessing();
    void saveTimeProcessing(TimeProcessingOffline timeProcessing);
    List<WorkSessionStateOffline> getAllWorkSessionStates();
    void saveWorkSessionState(WorkSessionStateOffline workSessionState);
}
