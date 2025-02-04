package cottontex.graphdep.database.interfaces;

import cottontex.graphdep.models.UserOffline;

import java.util.List;

public interface IUserLogin {
    String authenticateUser(String username, String password);
    Integer getUserID(String username);
    Integer getEmployeeId(String username);
    String getName(String username);

    List<UserOffline> getAllUsers();
    void saveUser(UserOffline user);
}

