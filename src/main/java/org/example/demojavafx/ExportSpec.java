package org.example.demojavafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.demojavafx.markers.Exporter;
import org.example.graph.EventGraph;
import org.example.graph.Node;
import org.example.graph.NodeType;
import org.example.util.EventGraphService;
import org.example.util.ExportDirUnit;
import org.example.util.OpenAPITranslator;

import java.util.List;

public class ExportSpec implements Exporter {
    @FXML
    public PathChooserController pathChooserController;

    private String directory;
    private EventGraphService eventGraphService = EventGraphService.EVENT_GRAPH_SERVICE;

    @FXML
    public TableView<MyDataModel> serviceTable;
    ObservableList<MyDataModel> tableData = FXCollections.observableArrayList();

    public void initialize() {
        pathChooserController.setExporter(this);
        TableColumn<MyDataModel, ?> titleColumn = serviceTable.getVisibleLeafColumn(0);
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        serviceTable.setItems(tableData);
        EventGraph eventGraph = eventGraphService.getEventGraph();
        List<Node> sevices = eventGraph.getNodesByType(NodeType.SERVICE);
        tableData.addAll(sevices.stream().map(node -> new MyDataModel(node.getName(), node.getName(), "")).toList());
        TableColumn<MyDataModel, Boolean> selectColumn = new TableColumn<>("Select");
        selectColumn.setCellFactory(column -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();
            {
                checkBox.setSelected(true);
                checkBox.setDisable(true);
                checkBox.setOnAction(event -> {
                    MyDataModel rowData = getTableView().getItems().get(getIndex());
                    rowData.setSelected(checkBox.isSelected());
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
        serviceTable.getColumns().add(selectColumn);
    }
    @Override
    public Type getType() {
        return Type.SPEC;
    }

    public void exportSpecification(ActionEvent actionEvent) {
        // TODO watch table checkbox
        directory = pathChooserController.getDirectory();
        OpenAPITranslator.saveOpenAPISpecification(eventGraphService.getEventGraph(), directory);
    }
}
