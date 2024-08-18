package cottontex.graphdep.controllers.info;

import cottontex.graphdep.constants.AppPathsCSS;
import cottontex.graphdep.constants.AppPathsIMG;
import cottontex.graphdep.controllers.common.EBaseDialogController;
import cottontex.graphdep.database.interfaces.admin.IAdminTimeTableHandler;
import cottontex.graphdep.models.UserStatus;
import cottontex.graphdep.services.info.UserStatusDialogService;
import cottontex.graphdep.utils.DialogUtils;
import cottontex.graphdep.utils.JavaFxScheduler;
import cottontex.graphdep.utils.LoggerUtility;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Objects;


public class EUserStatusDialogController extends EBaseDialogController {

    private UserStatusDialogService userStatusDialogService;
    private final ObservableList<UserStatus> userStatuses = FXCollections.observableArrayList();
    private final CompositeDisposable disposables = new CompositeDisposable();

    @FXML
    private VBox userStatusBox;
    @FXML
    private ScrollPane statusScrollPane;


    private static final double DIALOG_MIN_WIDTH = 300;
    private static final double DIALOG_MAX_WIDTH = 500;
    private static final double DIALOG_MIN_HEIGHT = 600;
    private static final double DIALOG_MAX_HEIGHT = 900;


    @FXML
    public void initialize() {
        statusScrollPane.setContent(userStatusBox);
        initializeDependencies();
        initializeComponents();
    }

    @Override
    protected void initializeDependencies() {
        super.initializeDependencies();
        IAdminTimeTableHandler adminTimeTableHandler = getDependency(IAdminTimeTableHandler.class);
        userStatusDialogService = new UserStatusDialogService(adminTimeTableHandler);
    }

    @Override
    protected void initializeComponents() {
        super.initializeComponents();
        LoggerUtility.info("Initializing UserStatusDialogController components");
    }

    @Override
    protected void performRoleSpecificLogout() {

    }

    @Override
    protected void redirectToLogin() {

    }

    @Override
    public void dispose() {
        super.dispose();
        disposables.clear();
    }

    @Override
    protected void refreshContent() {
        LoggerUtility.info("Refreshing user statuses");
        loadUserStatuses();
    }

    @Override
    protected void loadDialogImage() {
        setRefreshButtonImage(AppPathsIMG.REFRESH_ICON);
    }

    private void loadUserStatuses() {
        if (userStatusDialogService == null) {
            LoggerUtility.error("UserStatusDialogService is not initialized");
            return;
        }
        disposables.add(userStatusDialogService.fetchUserStatuses()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(
                        this::updateUserStatusList,
                        error -> {
                            LoggerUtility.error("Error fetching user statuses", error);
                            showAlert("Error", "Failed to load user statuses. Please try again.");
                        }
                ));
    }

//    private void loadUserStatuses() {
//        Platform.runLater(() -> {
//            try {
//                List<UserStatus> userStatuses = userStatusDialogService.fetchUserStatuses();
//                updateUserStatusList(userStatuses);
//            } catch (Exception e) {
//                LoggerUtility.error("Error fetching user statuses", e);
//                showAlert("Error", "Failed to load user statuses. Please try again.");
//            }
//        });
//    }

    @Override
    public void setDialogStage(Stage dialogStage) {
        super.setDialogStage(dialogStage);
        setupDialog(dialogStage);
        loadUserStatuses();
    }

    private void setupDialog(Stage dialogStage) {
        DialogUtils.setupDynamicSizeDialog(dialogStage, statusScrollPane, userStatusBox,
                DIALOG_MIN_WIDTH, DIALOG_MAX_WIDTH,
                DIALOG_MIN_HEIGHT, DIALOG_MAX_HEIGHT);

        dialogStage.setTitle("User Status");

        Scene scene = dialogStage.getScene();
        if (scene != null) {
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(AppPathsCSS.USER_STATUS_DIALOG)).toExternalForm());
        } else {
            LoggerUtility.error("Scene is null when setting dialog stage");
        }
    }

    private void updateUserStatusList(List<UserStatus> userStatusList) {
        userStatuses.setAll(userStatusList);
        userStatusBox.getChildren().clear();
        for (UserStatus status : userStatuses) {
            HBox userRow = UserStatusDialogService.createUserStatusRow(status);
            userStatusBox.getChildren().add(userRow);
        }
        LoggerUtility.info("Updated user status list with " + userStatusList.size() + " entries");
    }

//    @Override
//    public void setDialogStage(Stage dialogStage) {
//        super.setDialogStage(dialogStage);
//
//        DialogUtils.setupDynamicSizeDialog(dialogStage, statusScrollPane, userStatusBox,
//                DIALOG_MIN_WIDTH, DIALOG_MAX_WIDTH,
//                DIALOG_MIN_HEIGHT, DIALOG_MAX_HEIGHT);
//
//        dialogStage.setTitle("User Status");
//
//        Scene scene = dialogStage.getScene();
//        if (scene != null) {
//            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(AppPathsCSS.USER_STATUS_DIALOG)).toExternalForm());
//        } else {
//            LoggerUtility.error("Scene is null when setting dialog stage");
//        }
//
//        // Load user statuses after setting up the dialog
//        loadUserStatuses();
//    }
//    private double calculateContentWidth() {
//        return userStatusBox.getChildren().stream()
//                .mapToDouble(node -> node.getBoundsInParent().getWidth())
//                .max()
//                .orElse(DIALOG_MIN_WIDTH);
//    }
//
//    private double calculateContentHeight() {
//        return userStatusBox.getChildren().stream()
//                .mapToDouble(node -> node.getBoundsInParent().getHeight())
//                .sum();
//    }

//    private void updateUserStatusList(List<UserStatus> userStatuses) {
//        DialogUtils.updateContentAndResize(
//                dialogStage,
//                this::calculateContentWidth,
//                this::calculateContentHeight,
//                () -> {
//                    userStatusBox.getChildren().clear();
//                    for (UserStatus status : userStatuses) {
//                        HBox userRow = UserStatusDialogService.createUserStatusRow(status);
//                        userStatusBox.getChildren().add(userRow);
//                    }
//                    LoggerUtility.info("Updated user status list with " + userStatuses.size() + " entries");
//                },
//                DIALOG_MIN_WIDTH,
//                DIALOG_MAX_WIDTH,
//                DIALOG_MIN_HEIGHT,
//                DIALOG_MAX_HEIGHT,
//                CONTENT_PADDING
//        );
//    }
}