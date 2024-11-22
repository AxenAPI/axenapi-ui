package org.example.demojavafx;

import io.swagger.v3.oas.models.media.Schema;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;
import org.example.codegen.info.BrokerServer;
import org.example.codegen.info.BrokerServers;
import org.example.demojavafx.markers.BrokerServerRequire;
import org.example.graph.*;
import org.example.util.EventGraphService;

import java.io.IOException;

public class CreateLink {
    private final EventGraphService eventGraphService  = EventGraphService.EVENT_GRAPH_SERVICE;;

    public ComboBox topicComboBox;

    public ComboBox serviceComboBox;

    public ToggleSwitch directionToggleSwitch;

    public ComboBox existingEventComboBox;

    public TextField groupTextField;

    private String topic;

    private String service;

    private boolean direction;

    private String event;

    private String group;

    private MainWindow controller;

    private BrokerType type;

    public void initialize() throws IOException {
        addElementsIntoComboBoxes();

    }
    public void addElementsIntoComboBoxes() {
        EventGraph eventGraph = eventGraphService.getEventGraph();
        eventGraph.getNodesByType(NodeType.TOPIC).forEach(topic -> topicComboBox.getItems().add(topic.getName()));
        eventGraph.getNodesByType(NodeType.SERVICE).forEach(service -> serviceComboBox.getItems().add(service.getName()));
        eventGraph.getEventNames().forEach(event -> existingEventComboBox.getItems().add(event));

        // set default service
        if(!serviceComboBox.getItems().isEmpty()) {
            Object s = serviceComboBox.getItems().get(0);
            serviceComboBox.setValue(s);
            // set default group
            groupTextField.setText((String) s);
        }
        directionToggleSwitch.setSelected(true);
    }

    public void handleNewEvent(ActionEvent actionEvent) {
        EventGraph eventGraph = eventGraphService.getEventGraph();
        topic = (String) topicComboBox.getValue();
        service = (String) serviceComboBox.getValue();
        direction = directionToggleSwitch.isSelected();
        event = (String) existingEventComboBox.getValue();
        group = groupTextField.getText();
        if (topic != null && service != null && event != null) {
            //get node service by name
            Node serviceNode = eventGraphService.getEventGraph().getNode(service, NodeType.SERVICE);
            //get node topic by name
            Node topicNode = eventGraphService.getEventGraph().getNode(topic, NodeType.TOPIC);
            if(topicNode.getBroker() == null) {
                topicNode.setBroker(type);
            }
            // get schema from eventGraph by link.what
            Event eventSchema = eventGraph.getEvent(event);
            if(eventSchema == null) {
                throw new IllegalStateException("Event " + event + " not found in eventGraphService");
            }
            Link link;
            if(direction) {
                link = new Link(topicNode, serviceNode, group, eventSchema);
            } else {
                link = new Link(serviceNode, topicNode, group, eventSchema);
            }
            controller.addLinkToList(link);
            eventGraph.addLink(link);
            eventGraphService.getEventGraph().print();
            controller.drawGraph();
        }
    }

    public void setParent(MainWindow mainWindow) {
        this.controller = mainWindow;
    }
    public void close(ActionEvent actionEvent) {
        Stage window = (Stage) ((javafx.scene.Node) (actionEvent.getTarget())).getScene().getWindow();
        window.close();
    }
}
