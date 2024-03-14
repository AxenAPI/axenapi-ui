package org.example.demojavafx;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartStylableNode;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.example.graph.EventGraph;
import org.example.graph.Link;
import org.example.graph.NodeType;
import org.example.util.OpenAPITranslator;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class MainWindow {

    public StackPane pane;
    public TableView<MyDataModel> fileInfoTable;
    ObservableList<MyDataModel> tableData = FXCollections.observableArrayList();
    EventGraph allFilesGraph = new EventGraph();


    public void initialize() {
        fileInfoTable.setItems(tableData);
        TableColumn<MyDataModel, ?> column1 = fileInfoTable.getVisibleLeafColumn(0);
        column1.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        TableColumn<MyDataModel, ?> column2 = fileInfoTable.getVisibleLeafColumn(1);
        column2.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<MyDataModel, Boolean> selectColumn = new TableColumn<>("Select");

        selectColumn.setCellFactory(column -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(event -> {
                    MyDataModel rowData = getTableView().getItems().get(getIndex());
                    rowData.setSelected(checkBox.isSelected());
                    System.out.println("Selected: " + rowData.getFileName() + rowData.getTitle() + " " +
                            String.valueOf(rowData.isSelected()));

                    if (rowData.isSelected()) {
                        System.out.println("Selected: " + rowData.getFileName() + " " + rowData.getTitle());
                        allFilesGraph = allFilesGraph.plus(rowData.getAbsolutePath());
                    } else {
                        System.out.println("Not Selected: " + rowData.getFileName() + " " + rowData.getTitle());
                        allFilesGraph.minus(rowData.getTitle());
                    }
                    drawGraph();
                });
            }

            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(item != null && item);
                    setGraphic(checkBox);
                }
            }
        });

        fileInfoTable.getColumns().add(selectColumn);

    }

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

        if (selectedFiles != null) {
            // Process the selected files
            for (File file : selectedFiles) {
                // read all content from file and put it in a one string
                EventGraph eventGraph = OpenAPITranslator.parseOPenAPI(file.getAbsolutePath());
                allFilesGraph = EventGraph.merge(allFilesGraph, eventGraph);
                // java code: add raw in table fileInfoTable
                tableData.add(new MyDataModel(file.getName(), eventGraph.getTitle(), file.getAbsolutePath()));
            }
            drawGraph();
        }
    }

    public void resetZoom(ActionEvent actionEvent) {

    }

    public void exportSpecification(ActionEvent actionEvent) {

    }

    public void drawGraph() {
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
