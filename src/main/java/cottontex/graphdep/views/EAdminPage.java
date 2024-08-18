package cottontex.graphdep.views;

public class EAdminPage extends EBasePage {

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