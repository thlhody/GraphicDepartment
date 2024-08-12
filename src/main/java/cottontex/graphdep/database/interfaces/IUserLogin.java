package cottontex.graphdep.database.interfaces;

public interface IUserLogin {
    String authenticateUser(String username, String password);
    Integer getUserID(String username);
    Integer getEmployeeId(String username);
    String getName(Integer userId);
}
