package cottontex.graphdep.services.user;

import cottontex.graphdep.database.interfaces.admin.IUserManagementHandler;
import cottontex.graphdep.utils.LoggerUtility;

public class UserSettingsService {
    private final IUserManagementHandler userManagementHandler;

    public UserSettingsService(IUserManagementHandler userManagementHandler) {
        this.userManagementHandler = userManagementHandler;
    }

    public boolean changePassword(String username, String currentPassword, String newPassword) {
        LoggerUtility.info("Attempting to change password for user: " + username);
        boolean success = userManagementHandler.changePassword(username, currentPassword, newPassword);
        if (success) {
            LoggerUtility.info("Password changed successfully for user: " + username);
        } else {
            LoggerUtility.error("Failed to change password for user: " + username);
        }
        return success;
    }
}