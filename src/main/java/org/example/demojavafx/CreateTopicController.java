package org.example.demojavafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.Setter;
import org.example.graph.BrokerType;
import org.example.graph.EventGraph;
import org.example.graph.Node;
import org.example.graph.NodeType;
import org.example.util.EventGraphService;


public class CreateTopicController {
    @FXML
    public TextField topicNameTextField;
    @FXML
    public ComboBox<BrokerType> topicTypeComboBox;

    private final EventGraphService eventGraphService = EventGraphService.EVENT_GRAPH_SERVICE;

    @Setter
    private MainWindow parent;
    public void initialize() {
        topicTypeComboBox.getItems().addAll(BrokerType.values());
        topicTypeComboBox.setValue(BrokerType.KAFKA);
        EventGraph eventGraph = eventGraphService.getEventGraph();
        String nextTopicName = eventGraph.getNextTopicName();
        topicNameTextField.setText(nextTopicName);
    }
    public void createTopic(ActionEvent actionEvent) {
        String topicName = topicNameTextField.getText();
        BrokerType brokerType = topicTypeComboBox.getValue();
        Node node = new org.example.graph.Node(topicName, NodeType.TOPIC, null, brokerType);
        eventGraphService.getEventGraph().addNode(node);
        parent.reloadTopicsAndEvent();
        parent.drawGraph();
        EventGraph eventGraph = eventGraphService.getEventGraph();
        String nextTopicName = eventGraph.getNextTopicName();
        topicNameTextField.setText(nextTopicName);
    }

    public void close(ActionEvent actionEvent) {
        Stage window = (Stage) ((javafx.scene.Node) (actionEvent.getTarget())).getScene().getWindow();
        window.close();
    }
}
