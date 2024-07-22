package cottontex.graphdep.controllers.admin;

import cottontex.graphdep.controllers.BaseController;
import cottontex.graphdep.database.queries.user.UserTimeTableHandler;
import cottontex.graphdep.models.UserStatus;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import lombok.Setter;

import java.net.URL;
import java.util.List;


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

    @Setter
    private Integer userID;
    private String username;

    private UserTimeTableHandler userTimeTableHandler = new UserTimeTableHandler();

    public void setUsername(String username) {
        this.username = username;
        welcomeLabel.setText("Welcome, " + username + "!");
        URL resource = getClass().getResource("/cottontex/graphdep/images/ct.png");
    }

    @FXML
    public void initialize() {
        populateUserStatusBox();
        setupLogo();

    }


    @FXML
    protected void onViewMonthlyTimeClick() {
        loadPage((Stage) viewMonthlyButton.getScene().getWindow(), "/cottontex/graphdep/fxml/admin/AdminMonthlyTimeLayout.fxml", "Monthly Time View");
    }

    @FXML
    protected void onSettingsButtonClick() {
        loadPage((Stage) settingsButton.getScene().getWindow(), "/cottontex/graphdep/fxml/admin/SettingsAdminLayout.fxml", "Settings");
    }

    @FXML
    protected void onLogoutAdminButtonClick() {
        loadPage((Stage) logoutButton.getScene().getWindow(), "/cottontex/graphdep/fxml/LauncherLayout.fxml", "Graphics Department Login");
    }

    private void refreshUserStatus() {
        populateUserStatusBox();
    }

    private void clearFields() {
        nameField.clear();
        usernameField.clear();
        passwordField.clear();
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
        System.out.println("Creating row for user: " + status.getUsername()); // Debug log

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

        System.out.println("Start time: " + startTime + ", End time: " + endTime); // Debug log

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

        Button refreshButton = new Button("Refresh");
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