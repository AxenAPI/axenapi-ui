package org.example.demojavafx;

import io.swagger.v3.oas.models.security.SecurityScheme;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.example.codegen.info.BrokerServer;
import org.example.codegen.info.BrokerServers;
import org.example.graph.Node;
import org.example.graph.NodeType;
import org.example.util.EventGraphService;

import java.util.*;

public class CreateCodeController {

    EventGraphService eventGraphService = EventGraphService.EVENT_GRAPH_SERVICE;
    BrokerServers brokerServers = BrokerServers.BROKER_SERVERS;

    //table
    public TableView<Node> serviceTable;
    ObservableList<Node> serviceList = FXCollections.observableArrayList();
    public List<Node> selectedServiceList = new ArrayList<>();
    private MainWindow parent;
    Map<Node, BrokerServer> brokerServerMap = new HashMap<>();
    Map<Node, Integer> portMap = new HashMap<>();

    public void initialize() {
        // table: name, port, choose broker
        List<Node> servises = eventGraphService.getEventGraph()
                .getNodesByType(NodeType.SERVICE);
        serviceTable.setItems(serviceList);
        serviceList.addAll(servises);
        TableColumn<Node, String> name = new TableColumn<>("Name");
        TableColumn<Node, Boolean> selectColumn = new TableColumn<>("Select");

        TableColumn<Node, BrokerServer> broker = new TableColumn<>("Broker");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        serviceTable.setEditable(true);
        TableColumn<Node, Integer> port = new TableColumn<>("Port");
        port.setEditable(true);
        port.setOnEditCommit(
                (TableColumn.CellEditEvent<Node, Integer> event) -> {
                    Node node = event.getTableView().getItems().get(event.getTablePosition().getRow());
                    portMap.put(node, event.getNewValue());
        });
        selectColumn.setCellFactory(column -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(event -> {
                    Node node = getTableView().getItems().get(getIndex());
                    if (checkBox.isSelected()) {
                        selectedServiceList.add(node);
                    } else {
                        selectedServiceList.remove(node);
                    }
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
        // set select from list.
        broker.setCellFactory(column -> new TableCell<>() {
            private final ComboBox<BrokerServer> comboBox = new ComboBox<>();
            {
                Set<BrokerServer> brokers = brokerServers.getBrokerServers();
                comboBox.getItems().addAll(brokers);
                comboBox.setOnAction(event -> {
                    Node node = getTableView().getItems().get(getIndex());
                    brokerServerMap.put(node, comboBox.getValue());
                });
            }

            @Override
            public void updateItem(BrokerServer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    comboBox.getSelectionModel().select(item);
                    setGraphic(comboBox);
                }
            }
        });
        serviceTable.getColumns().add(name);
        serviceTable.getColumns().add(selectColumn);
        serviceTable.getColumns().add(port);
        serviceTable.getColumns().add(broker);
    }

    public void generateCode(ActionEvent actionEvent) {
return;
    }

    public void setParent(MainWindow mainWindow) {
        this.parent = mainWindow;
    }
}
