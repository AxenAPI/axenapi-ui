package org.example.demojavafx.datamodel;

import org.example.graph.BrokerType;
import org.example.graph.Node;

public class TopicDataModel {
    private Node node;
    private String title;
    private BrokerType type;

    public TopicDataModel(String title, Node node) {
        this.title = title;
        this.node = node;
    }

    public String getTitle() {
        return title;
    }
}
