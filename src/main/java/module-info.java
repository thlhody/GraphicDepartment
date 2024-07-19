module cottontex.graphdep {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.logging.log4j;
    requires static lombok;
    requires jfxtras.controls;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    opens cottontex.graphdep to javafx.fxml;
    exports cottontex.graphdep;
    opens cottontex.graphdep.controllers to javafx.fxml;
    exports cottontex.graphdep.controllers;
    exports cottontex.graphdep.database;
    opens cottontex.graphdep.database to javafx.fxml;
    exports cottontex.graphdep.utils;
    opens cottontex.graphdep.utils to javafx.fxml;
    opens cottontex.graphdep.models to javafx.base;
    exports cottontex.graphdep.views;
    opens cottontex.graphdep.views to javafx.fxml;

}