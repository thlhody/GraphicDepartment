package cottontex.graphdep.services.info;

import cottontex.graphdep.database.interfaces.admin.IAdminTimeTableHandler;
import cottontex.graphdep.models.UserStatus;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserStatusDialogService {
    private final IAdminTimeTableHandler adminTimeTableHandler;

    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public UserStatusDialogService(IAdminTimeTableHandler adminTimeTableHandler) {
        this.adminTimeTableHandler = adminTimeTableHandler;
    }

    public List<UserStatus> fetchUserStatuses() {
        return adminTimeTableHandler.getUserStatuses();
    }

    public static boolean isCurrentlyOnline(UserStatus status) {
        return status.getStartTime() != null &&
                !status.getStartTime().equals("N/A") &&
                (status.getEndTime() == null || status.getEndTime().equals("N/A"));
    }

    public static String formatUserStatus(UserStatus status) {
        String startTime = status.getStartTime();
        String endTime = status.getEndTime();

        if (startTime == null || startTime.equals("N/A")) {
            return "N/A";
        }

        LocalDateTime start = LocalDateTime.parse(startTime, INPUT_FORMATTER);
        String formattedDate = start.format(DATE_FORMATTER);
        String formattedStartTime = start.format(TIME_FORMATTER);

        if (endTime == null || endTime.equals("N/A")) {
            return String.format("%s :: %s - Online", formattedDate, formattedStartTime);
        } else {
            LocalDateTime end = LocalDateTime.parse(endTime, INPUT_FORMATTER);
            String formattedEndTime = end.format(TIME_FORMATTER);
            return String.format("%s :: %s - %s", formattedDate, formattedStartTime, formattedEndTime);
        }
    }

    public static HBox createUserStatusRow(UserStatus status) {
        HBox userRow = new HBox(10);
        userRow.setAlignment(Pos.CENTER_LEFT);
        userRow.getStyleClass().add("user-status-row");

        boolean isCurrentlyOnline = isCurrentlyOnline(status);
        Circle statusIndicator = new Circle(5, isCurrentlyOnline ? Color.GREEN : Color.RED);

        Label nameLabel = new Label(status.getUsername());
        nameLabel.getStyleClass().add("user-name");

        Label timeLabel = new Label(formatUserStatus(status));
        timeLabel.getStyleClass().add("user-time");

        userRow.getChildren().addAll(statusIndicator, nameLabel, timeLabel);
        return userRow;
    }
}