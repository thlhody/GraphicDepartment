package cottontex.graphdep;

import cottontex.graphdep.views.BasePage;

public class Launcher extends BasePage {

    @Override
    protected String getFxmlPath() {
        return "/cottontex/graphdep/fxml/launcher.fxml";
    }

    @Override
    protected String getTitle() {
        return "Graphic Department Login";
    }

    public static void main(String[] args) {
        launch(args);
    }
}