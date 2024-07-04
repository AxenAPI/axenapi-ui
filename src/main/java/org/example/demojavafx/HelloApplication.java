package org.example.demojavafx;

import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartStylableNode;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.graph.EventGraph;
import org.example.graph.Link;
import org.example.graph.Node;
import org.example.graph.NodeType;
import org.example.util.OpenAPITranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;

public class HelloApplication extends Application {
    private static final Logger logger = LoggerFactory.getLogger(HelloApplication.class);
    @Override
    public void start(Stage stage) throws IOException {
        System.setProperty("logback.configurationFile", "/resources/logback.xml");

        logger.info("JavaFX application started.");

        FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("main_window.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);

        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }



    public static void main(String[] args) {
        launch();
    }
}