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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.demojavafx.datamodel.Color;
import org.example.demojavafx.datamodel.EventDataModel;
import org.example.demojavafx.datamodel.TopicDataModel;
import org.example.graph.EventGraph;
import org.example.graph.Link;
import org.example.graph.NodeType;
import org.example.util.EventGraphService;
import org.example.util.OpenAPITranslator;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MainWindow {

    public StackPane pane;
    public TableView<MyDataModel> fileInfoTable;
    ObservableList<MyDataModel> tableData = FXCollections.observableArrayList();

    @FXML
    public TableView<TopicDataModel> topicTable;
    ObservableList<TopicDataModel> topicList = FXCollections.observableArrayList();

    @FXML
    public TableView<EventDataModel> eventTable;
    ObservableList<EventDataModel> eventList = FXCollections.observableArrayList();

    private final EventGraphService eventGraphService = EventGraphService.EVENT_GRAPH_SERVICE;
    private int colorNum = 0;


    public void initialize() {
        fileInfoTable.setItems(tableData);
        eventTable.setItems(eventList);
        topicTable.setItems(topicList);

        eventTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("title"));
        topicTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("title"));
        addColorToEventTable();

        TableColumn<MyDataModel, ?> column2 = fileInfoTable.getVisibleLeafColumn(0);
        column2.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<MyDataModel, Boolean> selectColumn = new TableColumn<>("Select");
        TableColumn<MyDataModel, Boolean> deleteColumn = new TableColumn<>("Delete");

        deleteColumn.setCellFactory(column -> new TableCell<>() {
            private final Button deleteButton = new Button();
            {
                Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("delete.png")));
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(20);
                imageView.setFitWidth(20);
                deleteButton.setGraphic(imageView);
                // set button size as table cell
                deleteButton.setMaxSize(20,20);
            }

            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                    deleteButton.setOnAction(actionEvent -> {
                        // get service name
                        MyDataModel rowData = getTableView().getItems().get(getIndex());
                        String serviceName = rowData.getTitle();
                        System.out.println("serviceName =" + serviceName);
                        eventGraphService.deleteService(serviceName);
                        getTableView().getItems().remove(getIndex());
                        getTableView().refresh();
                        reloadTopicsAndEvent();
                        drawGraph();
                    });
                }
            }

        });

        selectColumn.setCellFactory(column -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(event -> {
                    MyDataModel rowData = getTableView().getItems().get(getIndex());
                    rowData.setSelected(checkBox.isSelected());
                    if (rowData.isSelected()) {
                        eventGraphService.makeVisibleEventGraph(rowData.getTitle());
                    } else {
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
        fileInfoTable.getColumns().add(deleteColumn);

    }

    private void addColorToEventTable() {
        TableColumn<EventDataModel, Color> colorColumn = new TableColumn<>("Color");

        colorColumn.setCellFactory(column -> new TableCell<>() {
                 @Override
                 public void updateItem(Color item, boolean empty) {
                     super.updateItem(item, empty);
                     if (empty) {
                         setGraphic(null);
                     } else {
                         EventDataModel rowData = getTableView().getItems().get(getIndex());
                         item = rowData.getColor();
                         // draw small circle
                         Circle circle = new Circle();
                         circle.setRadius(10);
                         if (item != null) {
                             circle.setFill(javafx.scene.paint.Color.rgb(
                                     item.r, item.g, item.b
                             ));
                         }
                         setGraphic(circle);
                     }
                 }
            }
        );
        eventTable.getColumns().add(colorColumn);
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

        //build window for selecting specification files
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
            reloadTopicsAndEvent();
            drawGraph();
        }
    }

    private void reloadTopicsAndEvent() {
        eventList.clear();
        topicList.clear();
        eventGraphService.getEventGraph().getNodesByType(NodeType.TOPIC).forEach(topic -> {
            TopicDataModel topicDataModel = new TopicDataModel(topic.getName(), topic);
            if(!topicList.contains(topicDataModel)) {
                topicList.add(topicDataModel);
            }
        });
        eventGraphService.getEventGraph().getEventNames().forEach(event -> {
            EventDataModel eventDataModel = new EventDataModel(event);
            if(!eventList.contains(eventDataModel)) {
                eventDataModel.setColor(Color.values()[colorNum]);
                eventList.add(eventDataModel);
                colorNum++;
                if(colorNum == Color.values().length) {
                    colorNum = 0;
                }
            }
        });
    }

    public void resetZoom(ActionEvent actionEvent) {

    }

    public void exportSpecification(ActionEvent actionEvent) {
        //open exportSpec.fxml
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("exportSpec.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        setColorsToEdges(graphView, g);

        // add subscene in pane
        pane.getChildren().add(eventGraphScene);
        graphView.init();
    }

    private void setColorsToEdges(SmartGraphPanel<org.example.graph.Node, Link> graphView, Graph<org.example.graph.Node, Link> g) {
        Collection<Edge<Link, org.example.graph.Node>> edges = g.edges();
        edges.forEach(e -> {
            SmartStylableNode stylableEdge = graphView.getStylableEdge(e.element());
            String eventName = e.element().getWhat();
            Optional<EventDataModel> eventDataModel = eventList.stream().findFirst().filter(
                    o -> o.getTitle().equals(eventName)
            );
            eventDataModel.ifPresent(
                    dataModel ->
                            stylableEdge.addStyleClass(dataModel.getCSSClass()));

        });

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
        org.example.graph.Node node = new org.example.graph.Node("New_Topic_" + number, NodeType.TOPIC, null, null);
        eventGraphService.addNode(node);
        topicList.add(new TopicDataModel(node.getName(), node));
//        reloadTopicsAndEvent();
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

    public void clearGraph(ActionEvent actionEvent) {
        eventGraphService.clear();
        tableData.clear();
        reloadTopicsAndEvent();
        drawGraph();
    }

    public void addBroker(ActionEvent actionEvent) {
        //open window create_broker.fxml
        try {
            Stage stage = new Stage();
            Parent root = null;

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("broker_list.fxml"));
            root = loader.load();

            stage.setTitle("Create Broker Form");
            stage.setScene(new Scene(root, 600, 400));

            BrokerListController children = loader.getController(); //getting controller of window find_win.fxml
            children.setParent(this);   //setting parent of the controller-child - this
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void exportCode(ActionEvent actionEvent) {
        //open window create_broker.fxml
        try {
            Stage stage = new Stage();
            Parent root = null;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("create_code.fxml"));
            root = loader.load();
            stage.setTitle("Create Code Form");
            stage.setScene(new Scene(root, 600, 400));
            CreateCodeController children = loader.getController(); //getting controller of window find_win.fxml
            children.setParent(this);   //setting parent of the controller-child - this
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void addEventToTable(String name) {
        Color c = Color.values()[colorNum];
        eventList.add(new EventDataModel(name, c));
        colorNum++;
        if(colorNum == Color.values().length) {
            colorNum = 0;
        }
    }
}
