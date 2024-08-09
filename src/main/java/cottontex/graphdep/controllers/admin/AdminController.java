package cottontex.graphdep.controllers.admin;

import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.controllers.info.UserStatusDialogController;
import cottontex.graphdep.models.UserSession;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AdminController extends AdminBaseController {

    @FXML
    private Button manageUsersButton;
    @FXML
    private Button viewReportsButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button logoutButton;



    @FXML
    public void initialize() {
        LoggerUtility.info("Initializing AdminController");
        super.setupLogo();
    }

    @Override
    public void setUserSession(UserSession session) {
        super.setUserSession(session);
        LoggerUtility.info("UserSession set in AdminController: " + session);
    }

    @Override
    protected Scene getScene() {
        if (logoutButton == null) {
            LoggerUtility.error("Cannot get scene, Logout Button is null");
            return null;
        }
        return logoutButton.getScene();
    }

    @FXML
    protected void onUserStatusButtonClick() {
        Stage dialogStage = createCustomDialog(AppPathsFXML.USER_STATUS_DIALOG, "Users", UserStatusDialogController.class);
        if (dialogStage != null) {
            dialogStage.showAndWait();
        }
    }

    @FXML
    protected void onManageUsersClick() {
        Stage stage = (Stage) manageUsersButton.getScene().getWindow();
        loadPage(stage, AppPathsFXML.USER_PAGE_LAYOUT, "Manage Users", UserSession.getInstance());
    }

    @FXML
    protected void viewReportsButton() {
        if (viewReportsButton == null) {
            LoggerUtility.error("viewReportsButton is null when trying to view monthly time");
            return;
        }
        Stage stage = (Stage) viewReportsButton.getScene().getWindow();
        loadPage(stage, AppPathsFXML.VIEW_MONTHLY_TIME_LAYOUT, "View Reports", UserSession.getInstance());
    }

    @FXML
    protected void onSettingsButtonClick() {
        Stage stage = (Stage) settingsButton.getScene().getWindow();
        loadPage(stage, AppPathsFXML.SETTINGS_ADMIN_LAYOUT, "Settings", UserSession.getInstance());
    }

    @FXML
    protected void onLogoutButtonClick() {
        UserSession.setInstance(null);
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        loadPage(stage, AppPathsFXML.LAUNCHER, "Login", new UserSession());
    }
//
//    private void refreshUserStatus() {
//        List<UserStatus> userStatuses = fetchUserStatuses();
//        updateUserStatusList(userStatuses);
//    }
//
//    private List<UserStatus> fetchUserStatuses() {
//        return adminTimeTableHandler.getUserStatuses();
//    }
//
//    private void updateUserStatusList(List<UserStatus> userStatuses) {
//        userStatusBox.getChildren().clear();
//        for (UserStatus status : userStatuses) {
//            userStatusBox.getChildren().add(createUserStatusRow(status));
//        }
//    }

//    private HBox createUserStatusRow(UserStatus status) {
//        HBox userRow = new HBox(10);
//        userRow.setAlignment(Pos.CENTER_LEFT);
//        userRow.getStyleClass().add("user-status-row");
//
//        Rectangle statusIndicator = new Rectangle(10, 10);
//        boolean isCurrentlyOnline = !status.getStartTime().equals("N/A") && status.getEndTime().equals("N/A");
//        statusIndicator.setFill(isCurrentlyOnline ? Color.GREEN : Color.RED);
//
//        Label nameLabel = new Label(status.getUsername());
//        nameLabel.getStyleClass().add("user-name");
//
//        Label timeLabel = getTimeLabel(status, isCurrentlyOnline);
//        timeLabel.getStyleClass().add("user-time");
//
//        userRow.getChildren().addAll(statusIndicator, nameLabel, timeLabel);
//        return userRow;
//    }
//
//    private static Label getTimeLabel(UserStatus status, boolean isCurrentlyOnline) {
//        String startTime = status.getStartTime();
//        String endTime = status.getEndTime();
//        String timeLabelText;
//        if (isCurrentlyOnline) {
//            timeLabelText = "Online since " + startTime;
//        } else if (!startTime.equals("N/A") && !endTime.equals("N/A")) {
//            timeLabelText = startTime + " - " + endTime;
//        } else if (!startTime.equals("N/A")) {
//            timeLabelText = "Last seen at " + startTime;
//        } else {
//            timeLabelText = "N/A - N/A";
//        }
//
//        return new Label(timeLabelText);
//    }

//    private void setRefreshButtonImage() {
//        if (refreshIcon != null) {
//            InputStream imageStream = getClass().getResourceAsStream(AppPathsIMG.REFRESH_ICON);
//            if (imageStream != null) {
//                Image refreshImage = new Image(imageStream);
//                refreshIcon.setImage(refreshImage);
//            } else {
//                LoggerUtility.error("Failed to load refresh icon image");
//            }
//        } else {
//            LoggerUtility.error("Refresh icon ImageView is null in setRefreshButtonImage method");
//        }
//    }
//
//    private void setupRefreshButton() {
//        if (refreshButton != null) {
//            refreshButton.setOnAction(e -> refreshUserStatus());
//            refreshButton.setTooltip(new Tooltip("Refresh"));
//        } else {
//            LoggerUtility.error("Refresh button is null in setupRefreshButton method");
//        }
//    }
}
