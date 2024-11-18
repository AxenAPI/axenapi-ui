package org.example.demojavafx.datamodel;

import lombok.EqualsAndHashCode;
import org.example.graph.BrokerType;
import org.example.graph.Node;

@EqualsAndHashCode(of = "title")
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
