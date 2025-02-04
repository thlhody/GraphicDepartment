module cottontex.graphdep {
    requires org.slf4j;
    requires org.apache.logging.log4j;
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires jfxtras.controls;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires com.zaxxer.hikari;
    requires mysql.connector.j;
    requires io.reactivex.rxjava3;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.databind;
    requires java.sql;


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

    exports cottontex.graphdep.database.handlers;
    opens cottontex.graphdep.database.handlers to javafx.base, javafx.fxml;

    opens cottontex.graphdep.database.handlers.admin to javafx.fxml;
    exports cottontex.graphdep.database.handlers.admin;

    exports cottontex.graphdep.database.interfaces;
    opens cottontex.graphdep.database.interfaces to javafx.base, javafx.fxml;

    opens cottontex.graphdep.database.handlers.user to javafx.fxml;
    exports cottontex.graphdep.database.handlers.user;

    opens cottontex.graphdep.models to javafx.base, javafx.fxml;
    exports cottontex.graphdep.models;

    opens cottontex.graphdep.utils to javafx.fxml;
    exports cottontex.graphdep.utils;

    exports cottontex.graphdep.views;
    opens cottontex.graphdep.views to javafx.fxml;
    exports cottontex.graphdep.database.interfaces.admin;
    opens cottontex.graphdep.database.interfaces.admin to javafx.base, javafx.fxml;
    exports cottontex.graphdep.database.interfaces.user;
    opens cottontex.graphdep.database.interfaces.user to javafx.base, javafx.fxml;
    exports cottontex.graphdep.services.admin;
    opens cottontex.graphdep.services.admin to javafx.fxml;
    exports cottontex.graphdep.services.user;
    opens cottontex.graphdep.services.user to javafx.fxml;
    exports cottontex.graphdep.services.info;
    opens cottontex.graphdep.services.info to javafx.fxml;


}