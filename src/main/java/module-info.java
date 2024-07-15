module cottontex.graphdep {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.logging.log4j;

    opens cottontex.graphdep to javafx.fxml;
    exports cottontex.graphdep;

}