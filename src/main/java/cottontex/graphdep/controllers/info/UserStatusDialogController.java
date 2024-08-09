package cottontex.graphdep.controllers.info;

import cottontex.graphdep.constants.AppPathsIMG;
import cottontex.graphdep.controllers.common.BaseDialogController;
import cottontex.graphdep.models.UserStatus;
import cottontex.graphdep.utils.LoggerUtility;
import cottontex.graphdep.database.queries.admin.AdminTimeTableHandler;
import cottontex.graphdep.utils.DependencyFactory;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.InputStream;
import java.util.List;

public class UserStatusDialogController extends BaseDialogController {

    @FXML
    private Button refreshButton;
    @FXML
    private ImageView refreshIcon;
    @FXML
    private VBox userStatusBox;

    private AdminTimeTableHandler adminTimeTableHandler;

    @FXML
    public void initialize() {
        LoggerUtility.info("Initializing UserStatusDialogController");
        initializeDependencies();
        setRefreshButtonImage();
        setupRefreshButton();
        refreshUserStatus();
    }

    private void initializeDependencies() {
        adminTimeTableHandler = DependencyFactory.getInstance().createAdminTimeTableHandler();
    }

    private void refreshUserStatus() {
        List<UserStatus> userStatuses = fetchUserStatuses();
        updateUserStatusList(userStatuses);
    }

    private List<UserStatus> fetchUserStatuses() {
        return adminTimeTableHandler.getUserStatuses();
    }

    private void updateUserStatusList(List<UserStatus> userStatuses) {
        userStatusBox.getChildren().clear();
        for (UserStatus status : userStatuses) {
            userStatusBox.getChildren().add(createUserStatusRow(status));
            LoggerUtility.info("Added user status: " + status);
        }
        LoggerUtility.info("Total user statuses: " + userStatuses.size());
    }

    private HBox createUserStatusRow(UserStatus status) {
        HBox userRow = new HBox(10);
        userRow.setAlignment(Pos.CENTER_LEFT);
        userRow.getStyleClass().add("user-status-row");

        Rectangle statusIndicator = new Rectangle(10, 10);
        boolean isCurrentlyOnline = !status.getStartTime().equals("N/A") && status.getEndTime().equals("N/A");
        statusIndicator.setFill(isCurrentlyOnline ? Color.GREEN : Color.RED);

        Label nameLabel = new Label(status.getUsername());
        nameLabel.getStyleClass().add("user-name");

        Label timeLabel = new Label(getTimeLabel(status, isCurrentlyOnline));
        timeLabel.getStyleClass().add("user-time");

        userRow.getChildren().addAll(statusIndicator, nameLabel, timeLabel);
        return userRow;
    }

    private String getTimeLabel(UserStatus status, boolean isCurrentlyOnline) {
        String startTime = status.getStartTime();
        String endTime = status.getEndTime();
        if (isCurrentlyOnline) {
            return "Online since " + startTime;
        } else if (!startTime.equals("N/A") && !endTime.equals("N/A")) {
            return startTime + " - " + endTime;
        } else if (!startTime.equals("N/A")) {
            return "Last seen at " + startTime;
        } else {
            return "N/A - N/A";
        }
    }

    private void setRefreshButtonImage() {
        if (refreshIcon != null) {
            InputStream imageStream = getClass().getResourceAsStream(AppPathsIMG.REFRESH_ICON);
            if (imageStream != null) {
                Image refreshImage = new Image(imageStream);
                refreshIcon.setImage(refreshImage);
            } else {
                LoggerUtility.error("Failed to load refresh icon image");
            }
        } else {
            LoggerUtility.error("Refresh icon ImageView is null in setRefreshButtonImage method");
        }
    }

    private void setupRefreshButton() {
        if (refreshButton != null) {
            refreshButton.setOnAction(e -> refreshUserStatus());
            refreshButton.setTooltip(new Tooltip("Refresh"));
        } else {
            LoggerUtility.error("Refresh button is null in setupRefreshButton method");
        }
    }
}