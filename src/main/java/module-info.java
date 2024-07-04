module org.example.demojavafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires smartgraph;
    requires com.fasterxml.jackson.core;
    requires org.slf4j;
    requires org.apache.commons.io;
    requires org.apache.commons.lang3;
    requires swagger.parser.core;
    requires io.swagger.v3.oas.models;
    requires swagger.parser.v3;
    requires ch.qos.logback.classic;
    requires io.swagger.v3.core;

    opens org.example.demojavafx to javafx.fxml;
    opens org.example.graph to javafx.fxml;
    exports org.example.demojavafx;
    exports org.example.util;
    exports org.example.graph;
    opens org.example.util to javafx.fxml;

}