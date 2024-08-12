package cottontex.graphdep.database.interfaces.admin;

import cottontex.graphdep.database.handlers.AddUserResult;
import java.util.List;

public interface IUserManagementHandler {
    AddUserResult addUser(String name, String username, String password, Integer employeeId, String role);
    List<String> getAllUsernames();
    boolean resetPassword(String username, String newPassword);
    boolean deleteUser(String username);
    boolean changePassword(String username, String currentPassword, String newPassword);
    boolean isUsernameTaken(String username);
    boolean isEmployeeIdTaken(int employeeId);
}