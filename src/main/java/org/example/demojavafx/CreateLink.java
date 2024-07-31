package org.example.demojavafx;

import io.swagger.v3.oas.models.media.Schema;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.controlsfx.control.ToggleSwitch;
import org.example.graph.EventGraph;
import org.example.graph.Link;
import org.example.graph.Node;
import org.example.graph.NodeType;
import org.example.util.EventGraphService;

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


    public void initialize() {
        addElementsIntoComboBoxes();
    }
    public void addElementsIntoComboBoxes() {
        EventGraph eventGraph = eventGraphService.getEventGraph();
        eventGraph.getNodesByType(NodeType.TOPIC).forEach(topic -> topicComboBox.getItems().add(topic.getName()));
        eventGraph.getNodesByType(NodeType.SERVICE).forEach(service -> serviceComboBox.getItems().add(service.getName()));
        eventGraph.getEventNames().forEach(event -> existingEventComboBox.getItems().add(event));
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
            // get schema from eventGraph by link.what
            Schema schema = eventGraph.getLinks().
                    stream().
                    filter(link -> link.getWhat().equals(event)).
                    findFirst().get().getSchema();
            Link link = new Link(serviceNode, topicNode, event, service, schema, topicNode.getBroker(), group);
            eventGraph.addLink(link);
            eventGraphService.getEventGraph().print();
        }
    }
}
