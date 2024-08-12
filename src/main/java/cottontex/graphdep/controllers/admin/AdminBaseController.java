package cottontex.graphdep.controllers.admin;

import cottontex.graphdep.constants.AppPathsCSS;
import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.controllers.LauncherController;
import cottontex.graphdep.controllers.common.BaseController;
import cottontex.graphdep.database.interfaces.IUserLogin;
import cottontex.graphdep.models.WorkScheduleEntry;
import cottontex.graphdep.models.managers.UserSessionManager;
import cottontex.graphdep.services.admin.AdminBaseService;
import cottontex.graphdep.utils.LoggerUtility;
import cottontex.graphdep.utils.TableUtils;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;

import static cottontex.graphdep.utils.TableUtils.applyTableStyles;

public abstract class AdminBaseController extends BaseController {

    private BorderPane borderPane;
    protected AdminBaseService adminBaseService;

    @FXML
    protected void onBackButtonClick() {
        LoggerUtility.buttonInfo("Back to Admin Page", UserSessionManager.getSession().getUsername());
        Stage stage = (Stage) backButton.getScene().getWindow();
        loadAdminPage(stage, AppPathsFXML.ADMIN_PAGE_LAYOUT, "Admin Page", AdminController.class);
    }

    protected void loadAdminPage(Stage stage, String fxmlPath, String title, Class<?> controllerClass) {
        LoggerUtility.switchController(this.getClass(), controllerClass, UserSessionManager.getSession().getUsername());
        loadPage(stage, fxmlPath, title, UserSessionManager.getSession());
    }

    @Override
    protected boolean initializeRoleSpecificData() {
        if (userSession == null) {
            LoggerUtility.error("UserSession is null in AdminBaseController.initializeRoleSpecificData()");
            return false;
        }
        if (!userSession.isAdmin()) {
            LoggerUtility.error("Non-admin user in AdminBaseController.initializeRoleSpecificData()");
            return false;
        }
        LoggerUtility.info("Admin-specific data initialized successfully");
        return adminBaseService.initializeUserData(userSession);
    }

    @Override
    protected void performRoleSpecificLogout() {
        // Any admin-specific logout logic
    }

    @Override
    protected void initializeDependencies() {
        super.initializeDependencies();
        adminBaseService = new AdminBaseService(getHandler(IUserLogin.class));
    }

    protected void setupWorkScheduleTable(TableView<WorkScheduleEntry> table) {
        TableUtils.setupWorkScheduleColumns(table);
        applyTableStyles(table, AppPathsCSS.TABLE_STYLES_A);
    }

    protected File showSaveFileDialog(String initialFileName, Window window) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Excel File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName(initialFileName);
        return fileChooser.showSaveDialog(window);
    }

    protected Scene getCurrentScene() {
        if (borderPane != null) {
            return borderPane.getScene();
        } else {
            LoggerUtility.error("Cannot get scene as logoutButton is null");
            return null;
        }
    }

    private Stage getCurrentStage() {
        if (logoutButton != null && logoutButton.getScene() != null) {
            return (Stage) logoutButton.getScene().getWindow();
        }
        return null;
    }

    protected void redirectToLogin() {
        Stage currentStage = getCurrentStage();
        if (currentStage != null) {
            LoggerUtility.switchController(this.getClass(), LauncherController.class, "Logged out user");
            loadPage(currentStage, AppPathsFXML.LAUNCHER, "Login", null);
            LoggerUtility.info("User redirected to login");
        } else {
            LoggerUtility.error("Failed to get current stage for redirect to login");
            // Create a new stage for login
            Stage newLoginStage = new Stage();
            loadPage(newLoginStage, AppPathsFXML.LAUNCHER, "Login", null);
            newLoginStage.show();
        }
    }


}