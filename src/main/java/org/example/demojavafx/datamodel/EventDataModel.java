package org.example.demojavafx.datamodel;

import org.example.graph.EventGraph;
import org.example.graph.Node;

public class EventDataModel {
    private Node node;
    private String title;
    private Color color;

    public EventDataModel(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

}
