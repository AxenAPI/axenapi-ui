package org.example.demojavafx;

import io.swagger.v3.oas.models.media.Schema;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.controlsfx.control.ToggleSwitch;
import org.example.codegen.info.BrokerServer;
import org.example.codegen.info.BrokerServers;
import org.example.graph.*;
import org.example.util.EventGraphService;

public class CreateLink {
    private final EventGraphService eventGraphService  = EventGraphService.EVENT_GRAPH_SERVICE;;
    private final BrokerServers brokerServers = BrokerServers.BROKER_SERVERS;

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

    private BrokerServer brokerServer;

    private MainWindow controller;

    public ComboBox<BrokerServer> brokerComboBox;
    private BrokerType type;

    public void initialize() {
        addElementsIntoComboBoxes();
    }
    public void addElementsIntoComboBoxes() {
        EventGraph eventGraph = eventGraphService.getEventGraph();
        eventGraph.getNodesByType(NodeType.TOPIC).forEach(topic -> topicComboBox.getItems().add(topic.getName()));
        eventGraph.getNodesByType(NodeType.SERVICE).forEach(service -> serviceComboBox.getItems().add(service.getName()));
        eventGraph.getEventNames().forEach(event -> existingEventComboBox.getItems().add(event));
        brokerServers.getBrokerServers().forEach(broker -> brokerComboBox.getItems().add(broker));
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
            Schema schema = eventGraph.getEdges().get(event);
            Link link;
            if(direction) {
                link = new Link(topicNode, serviceNode, event, service, schema, topicNode.getBroker(), group);
            } else {
                link = new Link(serviceNode, topicNode, event, service, schema, topicNode.getBroker(), group);
            }
            eventGraph.addLink(link);
            eventGraphService.getEventGraph().print();
        }

        controller.drawGraph();
    }

    public void setParent(MainWindow mainWindow) {
        this.controller = mainWindow;
    }

    public void handleBrokerSelection(ActionEvent actionEvent) {
        brokerServer = brokerComboBox.getValue();
        type = BrokerType.KAFKA;
        if(brokerServer.getType().equals("kafka")) {
            type = BrokerType.KAFKA;
        } else if(brokerServer.getType().equals("jms")) {
            type = BrokerType.JMS;
        } else if(brokerServer.getType().equals("rabbit")) {
            type = BrokerType.RABBITMQ;
        }
    }
}
