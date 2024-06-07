package org.example.util;

import org.example.graph.EventGraph;
import org.example.graph.Node;

public enum EventGraphService {
    EVENT_GRAPH_SERVICE;

    private EventGraph eventGraph = new EventGraph();

    public EventGraph getEventGraph() {
        return eventGraph;
    }

    public void mergeEventGraph(EventGraph eventGraph) {
        this.eventGraph = EventGraph.merge(this.eventGraph, eventGraph);
    }

    public void removeEventGraph(String graphName) {
        this.eventGraph.minus(graphName);
        eventGraph.print();
    }


    public void addNode(Node node) {
        this.eventGraph.addNode(node);
    }
}
