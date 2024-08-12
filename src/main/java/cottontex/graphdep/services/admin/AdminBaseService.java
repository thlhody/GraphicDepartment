package cottontex.graphdep.services.admin;

import cottontex.graphdep.database.interfaces.IUserLogin;
import cottontex.graphdep.models.UserSession;
import cottontex.graphdep.utils.LoggerUtility;

public class AdminBaseService {
    protected final IUserLogin userLogin;

    public AdminBaseService(IUserLogin userLogin) {
        this.userLogin = userLogin;
    }

    public boolean initializeUserData(UserSession userSession) {
        if (userSession == null) {
            LoggerUtility.error("Invalid user session in BaseService.initializeUserData(). UserSession is null.");
            return false;
        }
        LoggerUtility.info("Initializing user data with session: " + userSession);
        return true;
    }

    // Add more common service methods here
}