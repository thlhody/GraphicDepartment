module cottontex.graphdep {
    requires org.slf4j;
    requires org.apache.logging.log4j;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires static lombok;
    requires jfxtras.controls;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires com.zaxxer.hikari;
    requires mysql.connector.j;


    opens cottontex.graphdep to javafx.fxml;
    exports cottontex.graphdep;

    opens cottontex.graphdep.controllers to javafx.fxml;
    exports cottontex.graphdep.controllers;

    opens cottontex.graphdep.controllers.admin to javafx.fxml;
    exports cottontex.graphdep.controllers.admin;

    opens cottontex.graphdep.controllers.info to javafx.fxml;
    exports cottontex.graphdep.controllers.info;

    opens cottontex.graphdep.controllers.common to javafx.fxml;
    exports cottontex.graphdep.controllers.common;

    opens cottontex.graphdep.controllers.user to javafx.fxml;
    exports cottontex.graphdep.controllers.user;

    opens cottontex.graphdep.database to javafx.fxml;
    exports cottontex.graphdep.database;

    opens cottontex.graphdep.database.queries.admin to javafx.fxml;
    exports cottontex.graphdep.database.queries.admin;

    opens cottontex.graphdep.database.queries.user to javafx.fxml;
    exports cottontex.graphdep.database.queries.user;

    opens cottontex.graphdep.models to javafx.base, javafx.fxml;
    exports cottontex.graphdep.models;

    opens cottontex.graphdep.utils to javafx.fxml;
    exports cottontex.graphdep.utils;

    exports cottontex.graphdep.views;
    opens cottontex.graphdep.views to javafx.fxml;

    exports cottontex.graphdep.database.queries;
    opens cottontex.graphdep.database.queries to javafx.fxml;


}