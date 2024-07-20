package cottontex.graphdep.views;

public class AdminPage extends BasePage {

    @Override
    protected String getFxmlPath() {
        return "AdminPageLayout.fxml";
    }

    @Override
    protected String getTitle() {
        return "Admin Page";
    }

    public static void main(String[] args) {
        launch(args);
    }
}