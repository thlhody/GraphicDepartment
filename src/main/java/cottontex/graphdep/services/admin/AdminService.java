package cottontex.graphdep.services.admin;

import cottontex.graphdep.database.interfaces.admin.IAdminTimeTableHandler;
import cottontex.graphdep.database.interfaces.admin.IUserManagementHandler;

public class AdminService {
    private final IAdminTimeTableHandler adminTimeTableHandler;
    private final IUserManagementHandler userManagementHandler;

    public AdminService(IAdminTimeTableHandler adminTimeTableHandler, IUserManagementHandler userManagementHandler) {
        this.adminTimeTableHandler = adminTimeTableHandler;
        this.userManagementHandler = userManagementHandler;
    }

    // Implement methods using adminTimeTableHandler and userManagementHandler
}