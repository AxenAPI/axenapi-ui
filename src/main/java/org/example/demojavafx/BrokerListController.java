package org.example.demojavafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.codegen.info.BrokerServer;
import org.example.codegen.info.BrokerServers;

import java.io.IOException;
import java.util.Objects;

public class BrokerListController {
    public TableView<BrokerServer> brokerTable;
    ObservableList<BrokerServer> tableData = FXCollections.observableArrayList();
    BrokerServers brokerServers = BrokerServers.KAFKA_SERVERS;
    private MainWindow parent;

    public void initialize() {
        tableData.addAll(brokerServers.getBrokerServers());
        brokerTable.setEditable(true);
        TableColumn<BrokerServer, ?> column1 = brokerTable.getVisibleLeafColumn(0);
        TableColumn<BrokerServer, ?> column2 = brokerTable.getVisibleLeafColumn(1);
        TableColumn<BrokerServer, ?> column3 = brokerTable.getVisibleLeafColumn(2);

        column1.setCellValueFactory(new PropertyValueFactory<>("name"));
        column2.setCellValueFactory(new PropertyValueFactory<>("type"));
        column3.setCellValueFactory(new PropertyValueFactory<>("address"));


        column1.setEditable(true);
        column2.setEditable(true);
        column3.setEditable(true);


        brokerTable.setItems(tableData);
        TableColumn<BrokerServer, Boolean> deleteColumn = new TableColumn<>("Delete");
        TableColumn<BrokerServer, Boolean> saveColumn = new TableColumn<>("Save");
        saveColumn.setCellFactory(column -> new TableCell<>() {
            private final Button saveButton = new Button();

            {
                Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("save.png")));
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(20);
                imageView.setFitWidth(20);
                saveButton.setGraphic(imageView);
                // set button size as table cell
                saveButton.setMaxSize(20, 20);
            }

            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(saveButton);
                    saveButton.setOnAction(actionEvent -> {
                                // get server address
                                BrokerServer rowData = getTableView().getItems().get(getIndex());
                                try {
                                    brokerServers.saveOrUpdate(rowData);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    );
                }
            }
        });
        deleteColumn.setCellFactory(column -> new TableCell<>() {
            private final Button deleteButton = new Button();

            {
                Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("delete.png")));
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(20);
                imageView.setFitWidth(20);
                deleteButton.setGraphic(imageView);
                // set button size as table cell
                deleteButton.setMaxSize(20, 20);
            }

            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                    deleteButton.setOnAction(actionEvent -> {
                        // get server address
                        BrokerServer rowData = getTableView().getItems().get(getIndex());
                        String address = rowData.getAddress();
                        try {
                            brokerServers.delete(address);
                            getTableView().getItems().remove(getIndex());
                            getTableView().refresh();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        });
        brokerTable.getColumns().add(saveColumn);
        brokerTable.getColumns().add(deleteColumn);
    }

    public void setParent(MainWindow mainWindow) {
        this.parent = mainWindow;
    }

    public void addBroker(ActionEvent actionEvent) {
        int size = tableData.size();
        tableData.add(new BrokerServer("New_Broker", "localhost:9092","kafka", size));
        brokerTable.refresh();
    }

    public void clearAll(ActionEvent actionEvent) throws IOException {
        tableData.clear();
        brokerTable.refresh();
        brokerServers.clear();
    }
}
