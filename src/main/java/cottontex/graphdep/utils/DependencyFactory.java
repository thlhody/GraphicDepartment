package cottontex.graphdep.utils;

import cottontex.graphdep.database.queries.UserLogin;
import cottontex.graphdep.database.queries.admin.AdminTimeTableHandler;
import cottontex.graphdep.database.queries.admin.UserManagementHandler;
import cottontex.graphdep.database.queries.user.ScheduleUserTable;
import cottontex.graphdep.database.queries.user.UserTimeOffHandler;
import cottontex.graphdep.database.queries.user.UserTimeTableHandler;

public class DependencyFactory {
    private static DependencyFactory instance;

    private DependencyFactory() {}

    public static DependencyFactory getInstance() {
        if (instance == null) {
            instance = new DependencyFactory();
        }
        return instance;
    }

    public ScheduleUserTable createScheduleUserTable() {
        // Instantiate and return a ScheduleUserTable instance
        return new ScheduleUserTable();
    }

    public UserTimeTableHandler createUserTimeTableHandler() {
        // Instantiate and return a UserTimeTableHandler instance
        return new UserTimeTableHandler();
    }

    public AdminTimeTableHandler createAdminTimeTableHandler() {
        // Instantiate and return an AdminTimeTableHandler instance
        return new AdminTimeTableHandler();
    }

    public UserTimeOffHandler createUserTimeOffHandler() {
        // Instantiate and return a UserTimeOffHandler instance
        return new UserTimeOffHandler();
    }

    public UserLogin createUserLogin() {
        // Instantiate and return a UserLogin instance
        return new UserLogin();
    }

    public UserManagementHandler createUserManagementHandler() {
        // Instantiate and return a UserManagementHandler instance
        return new UserManagementHandler();
    }
}
