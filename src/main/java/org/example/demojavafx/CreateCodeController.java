package org.example.demojavafx;

import io.swagger.v3.oas.models.security.SecurityScheme;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.example.codegen.info.BrokerServer;
import org.example.codegen.info.BrokerServers;
import org.example.demojavafx.markers.BrokerServerRequire;
import org.example.graph.Node;
import org.example.graph.NodeType;
import org.example.util.*;

import java.io.IOException;
import java.util.*;

public class CreateCodeController {

    EventGraphService eventGraphService = EventGraphService.EVENT_GRAPH_SERVICE;
    BrokerServers brokerServers = BrokerServers.BROKER_SERVERS;
    CodeGenerator codeGenerator = CodeGeneratorImpl.INSTANCE;

    //table
    public TableView<Node> serviceTable;
    ObservableList<Node> serviceList = FXCollections.observableArrayList();
    public List<Node> selectedServiceList = new ArrayList<>();
    private MainWindow parent;
    Map<Node, BrokerServer> brokerServerMap = new HashMap<>();
    Map<Node, String> portMap = new HashMap<>();


    public void initialize() {
        // table: name, port, choose broker
        List<Node> servises = eventGraphService.getEventGraph()
                .getNodesByType(NodeType.SERVICE);
        selectedServiceList.addAll(servises);
        serviceTable.setItems(serviceList);
        serviceList.addAll(servises);
        TableColumn<Node, String> name = new TableColumn<>("Name");
        TableColumn<Node, Boolean> selectColumn = new TableColumn<>("Select");

        TableColumn<Node, BrokerServer> broker = new TableColumn<>("Broker");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        serviceTable.setEditable(true);
        TableColumn<Node, String> port = new TableColumn<>("Port");
//        port.setCellValueFactory(new PropertyValueFactory<>("port"));
        port.setEditable(true);
        port.setCellFactory(TextFieldTableCell.forTableColumn());
        port.setCellValueFactory(data -> {
            Callback<TableColumn<Object, String>, TableCell<Object, String>> tableColumnTableCellCallback = TextFieldTableCell.forTableColumn();
            if (data.getValue() != null) {
                // get index of data
                int index = serviceList.indexOf(data.getValue());
                String portString = "808" + index;
                portMap.put(data.getValue(), portString);
                return new SimpleStringProperty(portString);
            } else {
                return new SimpleStringProperty("");
            }
        });

        port.setOnEditCommit(
                (TableColumn.CellEditEvent<Node, String> event) -> {
                    Node node = event.getTableView().getItems().get(event.getTablePosition().getRow());
                    portMap.put(node, event.getNewValue());
        });
        selectColumn.setCellFactory(column -> new TableCell<>(){
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
        Set<BrokerServer> brokers = brokerServers.getBrokerServers();
        servises.forEach(node -> brokerServerMap.put(node, brokers.iterator().next()));
        // set select from list.
        broker.setCellFactory(column -> new TableCell<>() {
            private final BrokerServerRequire require;
            {
                require = brokerServer -> {
                    Node node = getTableView().getItems().get(getIndex());
                    brokerServerMap.put(node, brokerServer);
                };
            }

            @Override
            public void updateItem(BrokerServer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    try {
                        FXMLLoader childLoader = new FXMLLoader(getClass().getResource("chooseBrokerElement.fxml"));
                        VBox brokerChooser = childLoader.load();
                        ChooseBrokerElement chooseBroker = childLoader.getController();
                        chooseBroker.setRequire(require);
                        setGraphic(brokerChooser);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        serviceTable.getColumns().add(name);
        serviceTable.getColumns().add(selectColumn);
        serviceTable.getColumns().add(port);
        serviceTable.getColumns().add(broker);
        serviceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    public void generateCode(ActionEvent actionEvent) {
        List<ServiceInfo> serviceInfoList = new ArrayList<>();
        String specDir = ExportDirUnit.getExportDir();

        OpenAPITranslator
                .saveOpenAPISpecification
                        (eventGraphService.getEventGraph(), specDir);

        selectedServiceList.forEach(service -> {
            String port = portMap.get(service);
            String broker = brokerServerMap.get(service).getAddress();
            String name = service.getName();
            ServiceInfo.ServiceInfoBuilder builder = ServiceInfo.builder();
            String specPath = specDir + "\\" + name + ".json";
            builder.brokerAddress(broker);
            builder.name(name);
            builder.port(port);
            builder.specificationPath(specPath);
            serviceInfoList.add(builder.build());
        });
        codeGenerator.generateCode(serviceInfoList);
    }

    public void setParent(MainWindow mainWindow) {
        this.parent = mainWindow;
    }
}
