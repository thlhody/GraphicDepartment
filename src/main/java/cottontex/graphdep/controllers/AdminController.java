package cottontex.graphdep.controllers;

import cottontex.graphdep.database.queries.UserStatusHandler;
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
import lombok.Setter;

import java.util.List;
import java.util.Objects;

public class AdminController extends BaseController {

    @FXML private Label welcomeLabel;
    @FXML private Button viewWorkDataButton;
    @FXML private Button logoutButton;
    @FXML private TextField nameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button addUserButton;
    @FXML private VBox userStatusBox;
    @FXML private Button settingsButton;

    @Setter private Integer userID;
    private String username;

    private UserStatusHandler userStatusHandler = new UserStatusHandler();


    public void setUsername(String username) {
        this.username = username;
        welcomeLabel.setText("Welcome, " + username + "!");
    }
    @FXML
    public void initialize(){
        populateUserStatusBox();
    }

    @FXML
    protected void onViewMonthlyTimeClick() {
        loadPage((Stage) logoutButton.getScene().getWindow(), "/cottontex/graphdep/fxml/AdminMonthlyTimeLayout.fxml", "Monthly Time View");
    }
    @FXML
    protected void onSettingsButtonClick() {
        loadPage((Stage) settingsButton.getScene().getWindow(), "/cottontex/graphdep/fxml/SettingsAdminLayout.fxml", "Settings");
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

        List<UserStatus> userStatuses = userStatusHandler.getUserStatuses();
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
        userRow.setStyle("-fx-background-color: #e8f5e9; -fx-padding: 5; -fx-background-radius: 5;");

        Label nameLabel = new Label(status.getUsername());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        nameLabel.setTextFill(Color.web("#2e7d32"));

        Label timeLabel = new Label(status.getStartTime() + " - " + status.getEndTime());
        timeLabel.setFont(Font.font("System", 12));
        timeLabel.setTextFill(Color.web("#1b5e20"));

        userRow.getChildren().addAll(nameLabel, timeLabel);
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