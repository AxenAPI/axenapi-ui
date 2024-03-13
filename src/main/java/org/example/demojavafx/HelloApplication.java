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
        // Create the graph

        EventGraph eventGraph1 = OpenAPITranslator.parseOPenAPI("C:/ideaProjects/demojavafx/api-docs.json");
        EventGraph eventGraph2 = OpenAPITranslator.parseOPenAPI("C:/ideaProjects/demojavafx/api-docs2.json");
        EventGraph eventGraph3 = OpenAPITranslator.parseOPenAPI("C:/ideaProjects/demojavafx/api-docs3.json");

        EventGraph eventGraph = EventGraph.merge(EventGraph.merge(eventGraph1, eventGraph2), eventGraph3);
        Graph<Node, Link> g = EventGraph.eventGraphToUIGraph(eventGraph);
// ... see Examples below

        SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
        SmartGraphPanel<Node, Link> graphView = new SmartGraphPanel<>(g, strategy);

        g.vertices().forEach(v -> {
            if (v.element().getType() == NodeType.SERVICE) {
                graphView.getStylableVertex(v.element())
                        .addStyleClass("service");
            } else if (v.element().getType() == NodeType.TOPIC) {
                graphView.getStylableVertex(v.element())
                        .addStyleClass("topic");
            }
        });

        graphView.setVertexDoubleClickAction(graphVertex -> {
            System.out.println("Vertex contains element: " + graphVertex.getUnderlyingVertex().element());
        });

        graphView.setEdgeDoubleClickAction(graphEdge -> {
            System.out.println("Edge contains element: " + graphEdge.getUnderlyingEdge().element());
            //dynamically change the style, can also be done for a vertex
            Collection<Edge<Link, Node>> edges = g.edges();
            edges.forEach(e -> {
               if(e.element().getWhat().equals(graphEdge.getUnderlyingEdge().element().getWhat())) {
                   SmartStylableNode stylableEdge = graphView.getStylableEdge(e.element());
                   if(!stylableEdge.removeStyleClass("selectedEvent")) {
                       stylableEdge.addStyleClass("selectedEvent");
                   }
               }
            });
        });

        Scene scene2 = new Scene(graphView, 1024, 768);

        Stage stage2 = new Stage(StageStyle.DECORATED);
        stage2.setTitle("JavaFXGraph Visualization");
        stage2.setScene(scene2);
        stage2.show();

//IMPORTANT! - Called after scene is displayed, so we can initialize the graph visualization
        graphView.init();
    }



    public static void main(String[] args) {
        launch();
    }
}