package cottontex.graphdep.controllers.admin;

import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.constants.AppPathsIMG;
import cottontex.graphdep.controllers.BaseController;
import cottontex.graphdep.database.queries.user.UserTimeTableHandler;
import cottontex.graphdep.models.UserStatus;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class AdminController extends BaseController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private Button viewWorkDataButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button viewMonthlyButton;
    @FXML
    private TextField nameField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button addUserButton;
    @FXML
    private VBox userStatusBox;
    @FXML
    private Button settingsButton;
    @FXML
    private ImageView logoImage;

    private final UserTimeTableHandler userTimeTableHandler = new UserTimeTableHandler();

    @Override
    public void initializeUserData() {
        welcomeLabel.setText("Welcome, " + userSession.getUsername() + "!");
        populateUserStatusBox();
    }

    @FXML
    public void initialize() {
        populateUserStatusBox();
        setupLogo();
    }

    @FXML
    protected void onViewMonthlyTimeClick() {
        loadPage((Stage) viewMonthlyButton.getScene().getWindow(), AppPathsFXML.VIEW_MONTHLY_TIME_LAYOUT, "Monthly Time View");
    }

    @FXML
    protected void onSettingsButtonClick() {
        loadPage((Stage) settingsButton.getScene().getWindow(), AppPathsFXML.SETTINGS_ADMIN_LAYOUT, "Settings");
    }

    @FXML
    protected void onLogoutAdminButtonClick() {
        loadPage((Stage) logoutButton.getScene().getWindow(), AppPathsFXML.LAUNCHER_LAYOUT, "Graphics Department Login");
    }


    private void refreshUserStatus() {
        populateUserStatusBox();
    }

    private void populateUserStatusBox() {
        if (userStatusBox == null) {
            LoggerUtility.error("userStatusBox is null in populateUserStatusBox method");
            return;
        }

        List<UserStatus> userStatuses = userTimeTableHandler.getMostRecentUserStatuses();
        userStatusBox.getChildren().clear();

        setupUserStatusHeader(); // This will add the header to the userStatusBox

        for (UserStatus status : userStatuses) {
            HBox userRow = createUserStatusRow(status);
            userStatusBox.getChildren().add(userRow);
        }
    }

    private HBox createUserStatusRow(UserStatus status) {

        HBox userRow = new HBox(10);
        userRow.setAlignment(Pos.CENTER_LEFT);
        userRow.setStyle("-fx-padding: 5; -fx-background-radius: 5;");

        Rectangle statusIndicator = new Rectangle(10, 10);
        boolean isCurrentlyOnline = !status.getStartTime().equals("N/A") && status.getEndTime().equals("N/A");
        statusIndicator.setFill(isCurrentlyOnline ? Color.GREEN : Color.RED);

        Label nameLabel = new Label(status.getUsername());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        String startTime = status.getStartTime();
        String endTime = status.getEndTime();

        String timeLabelText;
        if (isCurrentlyOnline) {
            timeLabelText = "Online since " + startTime;
        } else if (!startTime.equals("N/A") && !endTime.equals("N/A")) {
            timeLabelText = startTime + " - " + endTime;
        } else if (!startTime.equals("N/A")) {
            timeLabelText = "Last seen at " + startTime;
        } else {
            timeLabelText = "N/A - N/A";
        }

        Label timeLabel = new Label(timeLabelText);
        timeLabel.setFont(Font.font("System", 12));

        userRow.getChildren().addAll(statusIndicator, nameLabel, timeLabel);
        return userRow;
    }

    private void setupUserStatusHeader() {
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label header = new Label("User Status");
        header.setFont(Font.font("System", FontWeight.BOLD, 16));

        Button refreshButton = new Button();
        refreshButton.setStyle("-fx-background-color: transparent;"); // Make button background transparent

        // Load the refresh icon
        try {
            InputStream iconStream = getClass().getResourceAsStream(AppPathsIMG.REFRESH_ICON);
            if (iconStream != null) {
                Image refreshIcon = new Image(iconStream);
                ImageView refreshIconView = new ImageView(refreshIcon);
                refreshIconView.setFitHeight(20); // Adjust size as needed
                refreshIconView.setFitWidth(20);  // Adjust size as needed
                refreshButton.setGraphic(refreshIconView);
                refreshButton.setTooltip(new Tooltip("Refresh")); // Add tooltip for accessibility
            } else {
                LoggerUtility.error("Refresh icon not found: " + AppPathsIMG.REFRESH_ICON);
                refreshButton.setText("Refresh"); // Fallback to text if icon not found
            }
        } catch (Exception e) {
            LoggerUtility.error("Failed to load refresh icon", e);
            refreshButton.setText("Refresh"); // Fallback to text if icon loading fails
        }

        refreshButton.setOnAction(e -> refreshUserStatus());

        headerBox.getChildren().addAll(header, refreshButton);

        // Check if userStatusBox is not null before adding the header
        if (userStatusBox != null) {
            userStatusBox.getChildren().add(0, headerBox);
        } else {
            LoggerUtility.error("userStatusBox is null in setupUserStatusHeader method");
        }
    }
}