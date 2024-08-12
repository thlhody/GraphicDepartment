package cottontex.graphdep.database.interfaces.admin;

import cottontex.graphdep.models.UserStatus;
import java.util.List;

public interface IAdminTimeTableHandler {
    List<UserStatus> getUserStatuses();
}