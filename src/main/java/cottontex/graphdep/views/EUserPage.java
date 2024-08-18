package cottontex.graphdep.views;

public class EUserPage extends EBasePage {

    @Override
    protected String getFxmlPath() {
        return "UserPageLayout.fxml";
    }

    @Override
    protected String getTitle() {
        return "User Page";
    }

    public static void main(String[] args) {
        launch(args);
    }
}