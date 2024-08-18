package cottontex.graphdep.controllers.admin;

import cottontex.graphdep.constants.AppPathsCSS;
import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.controllers.ELauncherController;
import cottontex.graphdep.controllers.common.EBaseController;
import cottontex.graphdep.database.interfaces.IUserLogin;
import cottontex.graphdep.database.interfaces.admin.IAdminScheduleHandler;
import cottontex.graphdep.models.UserSession;
import cottontex.graphdep.models.WorkScheduleEntry;
import cottontex.graphdep.models.managers.EUserSessionManager;
import cottontex.graphdep.services.admin.AdminBaseService;
import cottontex.graphdep.utils.LoggerUtility;
import cottontex.graphdep.utils.TableUtils;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;

public abstract class EAdminBaseController extends EBaseController {

    protected BorderPane borderPane;
    protected AdminBaseService adminBaseService;
    protected IAdminScheduleHandler adminScheduleHandler;

    @Override
    protected void initializeDependencies() {
        IUserLogin userLogin = getDependency(IUserLogin.class);
        this.adminScheduleHandler = getDependency(IAdminScheduleHandler.class);
        this.adminBaseService = new AdminBaseService(userLogin);
    }

    @Override
    protected boolean initializeUserData(UserSession session) {
        if (!session.isAdmin()) {
            LoggerUtility.error("Non-admin user in AdminBaseController.initializeUserData()");
            return false;
        }
        return adminBaseService.initializeUserData(session);
    }

    @Override
    protected void initializeComponents() {
        // Initialize any common admin components here
    }

    @Override
    protected boolean requiresUserSession() {
        return true;
    }

    @Override
    protected void performRoleSpecificLogout() {
        // Perform any admin-specific logout actions here
    }

    @Override
    protected void redirectToLogin() {
        Stage currentStage = (Stage) getCurrentScene().getWindow();
        LoggerUtility.switchController(this.getClass(), ELauncherController.class, "Logged out admin");
        loadPage(currentStage, AppPathsFXML.LAUNCHER, "Login", null);
    }

    protected void loadAdminPage(Stage stage, String fxmlPath, String title, Class<?> controllerClass) {
        LoggerUtility.switchController(this.getClass(), controllerClass, EUserSessionManager.getCurrentUsername());
        userSession.ifPresentOrElse(
                session -> loadPage(stage, fxmlPath, title, session),
                () -> {
                    LoggerUtility.error("Attempted to load admin page without valid user session");
                    showAlert("Session Error", "Your session has expired. Please log in again.");
                    redirectToLogin();
                }
        );
    }


    protected void setupWorkScheduleTable(TableView<WorkScheduleEntry> table) {
        TableUtils.setupWorkScheduleColumns(table);
        TableUtils.applyTableStyles(table, AppPathsCSS.TABLE_STYLES_A); // Update path as needed
    }

    protected File showSaveFileDialog(String initialFileName, Window window) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Excel File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName(initialFileName);
        return fileChooser.showSaveDialog(window);
    }

    @Override
    protected void handleUserDataInitializationFailure() {
        super.handleUserDataInitializationFailure();
        showAlert("Access Denied", "You do not have the required permissions to access this page.");
        redirectToLogin();
    }

    @Override
    protected void handleNullUserSession() {
        super.handleNullUserSession();
        redirectToLogin();
    }
}