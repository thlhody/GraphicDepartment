package cottontex.graphdep.views;

public class UserPage extends BasePage {

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