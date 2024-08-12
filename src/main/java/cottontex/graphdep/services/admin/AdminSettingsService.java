package cottontex.graphdep.services.admin;

import cottontex.graphdep.database.interfaces.admin.IUserManagementHandler;
import cottontex.graphdep.database.handlers.AddUserResult;
import cottontex.graphdep.utils.LoggerUtility;

import java.util.List;

public class AdminSettingsService {
    private final IUserManagementHandler userManagementHandler;

    public AdminSettingsService(IUserManagementHandler userManagementHandler) {
        this.userManagementHandler = userManagementHandler;
    }

    public AddUserResult addUser(String name, String username, String password, int employeeId, String role) {
        LoggerUtility.actionInfo("Add User", "Name: " + name + ", Username: " + username + ", Role: " + role, "Admin");
        return userManagementHandler.addUser(name, username, password, employeeId, role);
    }

    public boolean resetPassword(String username, String newPassword) {
        LoggerUtility.actionInfo("Reset Password", "User: " + username, "Admin");
        return userManagementHandler.resetPassword(username, newPassword);
    }

    public boolean deleteUser(String username) {
        LoggerUtility.actionInfo("Delete User", "User: " + username, "Admin");
        return userManagementHandler.deleteUser(username);
    }

    public List<String> getAllUsernames() {
        return userManagementHandler.getAllUsernames();
    }
}