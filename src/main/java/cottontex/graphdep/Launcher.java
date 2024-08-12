package cottontex.graphdep;

import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.views.BasePage;
import cottontex.graphdep.database.DatabaseConnection;
import cottontex.graphdep.database.BaseDatabase;
import cottontex.graphdep.utils.LoggerUtility;
import javafx.application.Platform;

public class Launcher extends BasePage {

    @Override
    protected String getFxmlPath() {
        return AppPathsFXML.LAUNCHER;
    }

    @Override
    protected String getTitle() {
        return "Graphic Department Login";
    }

    @Override
    public void stop() {
        try {
            shutdownApplication();
            super.stop();
            LoggerUtility.info("Application stopped successfully.");
        } catch (Exception e) {
            LoggerUtility.error("Error occurred during application stop", e);
        } finally {
            // Ensure that any remaining cleanup is performed
            try {
                DatabaseConnection.closePool();
            } catch (Exception e) {
                LoggerUtility.error("Error closing database connection pool during final cleanup", e);
            }
        }
    }

    private static void shutdownApplication() {
        try {
            // Clear any statement caches
            BaseDatabase.clearStatementCaches();

            // Close the database connection pool
            DatabaseConnection.closePool();

            LoggerUtility.info("Application shutdown completed successfully.");
        } catch (Exception e) {
            LoggerUtility.error("Error during application shutdown", e);
        }
    }

    public static void main(String[] args) {
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Platform.runLater(() -> {
                try {
                    shutdownApplication();
                } catch (Exception e) {
                    LoggerUtility.error("Error in shutdown hook", e);
                }
            });
        }));

        launch(args);
    }
}