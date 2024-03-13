package org.example.demojavafx;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartStylableNode;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.example.graph.EventGraph;
import org.example.graph.Link;
import org.example.graph.NodeType;
import org.example.util.OpenAPITranslator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;

public class MainWindow {

    public StackPane pane;

    public void viewEventGraph(ActionEvent actionEvent) {

    }

    public void chooseFiles(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Files");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));


        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(((Node) actionEvent.getTarget()).getScene().getWindow());
        EventGraph allFilesGraph = new EventGraph();
        if (selectedFiles != null) {
            // Process the selected files
            for (File file : selectedFiles) {
                // read all content from file and put it in a one string
                EventGraph eventGraph = OpenAPITranslator.parseOPenAPI(file.getAbsolutePath());
                allFilesGraph = EventGraph.merge(allFilesGraph, eventGraph);
            }

            Graph<org.example.graph.Node, Link> g = EventGraph.eventGraphToUIGraph(allFilesGraph);

            SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
            SmartGraphPanel<org.example.graph.Node, Link> graphView = new SmartGraphPanel<>(g, strategy);

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
                Collection<Edge<Link, org.example.graph.Node>> edges = g.edges();
                edges.forEach(e -> {
                    if(e.element().getWhat().equals(graphEdge.getUnderlyingEdge().element().getWhat())) {
                        SmartStylableNode stylableEdge = graphView.getStylableEdge(e.element());
                        if(!stylableEdge.removeStyleClass("selectedEvent")) {
                            stylableEdge.addStyleClass("selectedEvent");
                        }
                    }
                });
            });

            SubScene eventGraphScene = new SubScene(graphView, 1024, 768);
            eventGraphScene.setRoot(graphView);

            // add subscene in pane
            pane.getChildren().add(eventGraphScene);
            graphView.init();
        }
    }

    public void resetZoom(ActionEvent actionEvent) {

    }

    public void exportSpecification(ActionEvent actionEvent) {

    }
}
