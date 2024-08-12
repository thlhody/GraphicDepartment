package cottontex.graphdep.controllers.info;

import cottontex.graphdep.constants.AppPathsIMG;
import cottontex.graphdep.controllers.common.BaseDialogController;
import cottontex.graphdep.database.interfaces.admin.IAdminTimeTableHandler;
import cottontex.graphdep.models.UserStatus;
import cottontex.graphdep.services.info.UserStatusDialogService;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;

import java.util.List;

public class UserStatusDialogController extends BaseDialogController {

    private UserStatusDialogService userStatusDialogService;

    @FXML
    private VBox userStatusBox;
    @FXML
    private ScrollPane statusScrollPane;

    @Override
    public void initialize() {
        super.initialize();
        LoggerUtility.info("Initializing UserStatusDialogController");
        refreshContent();
    }

    @Override
    protected void initializeDependencies() {
        IAdminTimeTableHandler adminTimeTableHandler = getHandler(IAdminTimeTableHandler.class);
        userStatusDialogService = new UserStatusDialogService(adminTimeTableHandler);
    }

    @Override
    protected boolean initializeRoleSpecificData() {
        return false;
    }

    @Override
    protected void performRoleSpecificLogout() {

    }

    @Override
    protected void redirectToLogin() {
        // LauncherController is already the login screen, so we don't need to redirect
        LoggerUtility.info("Already on login screen, no redirection needed");
    }

    @Override
    protected void refreshContent() {
        List<UserStatus> userStatuses = userStatusDialogService.fetchUserStatuses();
        updateUserStatusList(userStatuses);
    }

    @Override
    protected void loadDialogImage() {
        setRefreshButtonImage(AppPathsIMG.REFRESH_ICON);
    }

    private void updateUserStatusList(List<UserStatus> userStatuses) {
        userStatusBox.getChildren().clear();
        for (UserStatus status : userStatuses) {
            userStatusBox.getChildren().add(UserStatusDialogService.createUserStatusRow(status));
            LoggerUtility.info("Added user status: " + status.getUsername() + " - " + status.getRole());
        }
        LoggerUtility.info("Total user statuses: " + userStatuses.size());
    }
}