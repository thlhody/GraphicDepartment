module cottontex.graphdep {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.logging.log4j;
    requires static lombok;
    requires jfxtras.controls;

    opens cottontex.graphdep to javafx.fxml;
    exports cottontex.graphdep;
    opens cottontex.graphdep.controllers to javafx.fxml;
    exports cottontex.graphdep.controllers;
    exports cottontex.graphdep.database;
    opens cottontex.graphdep.database to javafx.fxml;
    exports cottontex.graphdep.loggerUtility;
    opens cottontex.graphdep.loggerUtility to javafx.fxml;

}