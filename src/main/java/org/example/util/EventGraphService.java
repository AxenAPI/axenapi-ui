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

    public void makeInvisible(String graphName) {
        this.eventGraph.makeInvisible(graphName);
        eventGraph.print();
    }


    public void addNode(Node node) {
        this.eventGraph.addNode(node);
    }

    public void mergeOrMakeVisibleEventGraph(EventGraph eventGraph) {
        boolean isInGraph = this.eventGraph.getNodes().stream().anyMatch(node -> node.getBelongsToGraph().contains(eventGraph.getName()));

        if (isInGraph) {
            this.eventGraph.makeVisible(eventGraph.getName());
        } else {
            this.mergeEventGraph(eventGraph);
        }

    }

    public void saveOpenAPISpecification(String filePath) {
        OpenAPITranslator.saveOpenAPISpecification(this.eventGraph, filePath);
    }
}
