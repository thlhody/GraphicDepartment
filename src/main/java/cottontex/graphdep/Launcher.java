package cottontex.graphdep;

import cottontex.graphdep.constants.AppPathsFXML;
import cottontex.graphdep.views.BasePage;

public class Launcher extends BasePage {

    @Override
    protected String getFxmlPath() {
        return AppPathsFXML.LAUNCHER;
    }

    @Override
    protected String getTitle() {
        return "Graphic Department Login";
    }

    public static void main(String[] args) {
        launch(args);
    }
}