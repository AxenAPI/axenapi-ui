package org.example.demojavafx;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartStylableNode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.graph.EventGraph;
import org.example.graph.Link;
import org.example.graph.NodeType;
import org.example.util.EventGraphService;
import org.example.util.OpenAPITranslator;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class MainWindow {

    public StackPane pane;
    public TableView<MyDataModel> fileInfoTable;
    ObservableList<MyDataModel> tableData = FXCollections.observableArrayList();

    private final EventGraphService eventGraphService = EventGraphService.EVENT_GRAPH_SERVICE;


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
                        eventGraphService.makeVisibleEventGraph(rowData.getTitle());
                    } else {
                        System.out.println("Not Selected: " + rowData.getFileName() + " " + rowData.getTitle());
                        eventGraphService.makeInvisible(rowData.getTitle());
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
                    checkBox.setSelected(item == null || item);
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
                eventGraphService.mergeEventGraph(eventGraph);
                // java code: add raw in table fileInfoTable
                tableData.add(new MyDataModel(file.getName(), eventGraph.getTitle(), file.getAbsolutePath()));
            }
            drawGraph();
        }
    }

    public void resetZoom(ActionEvent actionEvent) {

    }

    public void exportSpecification(ActionEvent actionEvent) {
        OpenAPITranslator.saveOpenAPISpecification(eventGraphService.getEventGraph(), "C:\\ideaprojects\\axenapi\\axenapiui\\export");
    }

    public void drawGraph() {
        Graph<org.example.graph.Node, Link> g = EventGraph.eventGraphToUIGraph(eventGraphService.getEventGraph());
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

    public void addService(ActionEvent actionEvent) {
        int number = tableData.size() + 1;
        eventGraphService.addNode(new org.example.graph.Node("New_Service_" + number , NodeType.SERVICE, "New_Service_" + number, null));
        drawGraph();
        tableData.add(new MyDataModel("New_Service_" + number, "New_Service_" + number, ""));
    }

    public void createLink(ActionEvent actionEvent) {
        //open window create_link.fxml

        try {
            Stage stage = new Stage();
            Parent root = null;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("create_link.fxml"));
            root = loader.load();
            stage.setTitle("Create Link Form");
            stage.setScene(new Scene(root, 600, 400));
            CreateLink children = loader.getController(); //getting controller of window find_win.fxml
            children.setParent(this);   //setting parent of the controller-child - this
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void addTopic(ActionEvent actionEvent) {
        int number = eventGraphService.getEventGraph().getNodesByType(NodeType.TOPIC).size() + 1;
        eventGraphService.addNode(new org.example.graph.Node("New_Topic_" + number, NodeType.TOPIC, null, null));
        drawGraph();
    }

    public void addEvent(ActionEvent actionEvent) {
        //open window create_event.fxml
        try {
            Stage stage = new Stage();
            Parent root = null;

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("create_event.fxml"));
            root = loader.load();

            stage.setTitle("Create Event Form");
            stage.setScene(new Scene(root, 600, 400));

            CreateEvent children = loader.getController(); //getting controller of window find_win.fxml
            children.setParent(this);   //setting parent of the controller-child - this
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
