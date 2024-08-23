package org.example.graph;

import com.brunomnsilva.smartgraph.graphview.SmartLabelSource;

import java.util.*;

public class Node {

    private UUID id;
    private List<String> belongsToGraph;
    private Set<String> belongsToVisibleGraph;
    private String name;
    private NodeType type;
    private BrokerType brokerType;

    private boolean visible;

    public Node(String name, NodeType type, String belongsToGraph, BrokerType brokerType) {
        this.name = name;
        this.type = type;
        this.id = UUID.randomUUID();
        this.belongsToGraph = new ArrayList<>();
        this.belongsToVisibleGraph = new HashSet<>();
        if(belongsToGraph != null) {
            this.belongsToGraph.add(belongsToGraph);
            this.belongsToVisibleGraph.add(belongsToGraph);
        }
        this.visible = true;
        this.brokerType = brokerType;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(name, node.name)
                && Objects.equals(type, node.type);
    }

    public int hashCode() {
        return Objects.hash(name, type);
    }

    public String getName() {
        return name;
    }

    public NodeType getType() {
        return type;
    }

    public String toString() {
        return "Node{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", graphName='" + belongsToGraph + '\'' +
                '}';
    }

    @SmartLabelSource
    public String view() {
        return name;
    }

    public void removeBelongsToGraph(String graphName) {
        belongsToGraph.remove(graphName);
    }

    public List<String> getBelongsToGraph() {
        return belongsToGraph;
    }

    public void addBelongsToGraph(List<String> belongsToGraph) {
        if(this.belongsToGraph != null && this.belongsToVisibleGraph != null) {
            this.belongsToGraph.addAll(belongsToGraph);
            this.belongsToVisibleGraph.addAll(belongsToGraph);
        } else {
            this.belongsToGraph = new ArrayList<>();
            this.belongsToVisibleGraph = new HashSet<>();
            this.belongsToGraph.addAll(belongsToGraph);
            this.belongsToVisibleGraph.addAll(belongsToGraph);
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void removeBelongsToVisibleGraph(String graphName) {
        belongsToVisibleGraph.remove(graphName);
    }

    public Set<String> getBelongsToVisibleGraph() {
        return belongsToVisibleGraph;
    }

    public void addBelongsToVisibleGraph(Set<String> belongsToVisibleGraph) {
        if(this.belongsToVisibleGraph != null) {
            this.belongsToVisibleGraph.addAll(belongsToVisibleGraph);
        } else {
            this.belongsToVisibleGraph = new HashSet<>();
            this.belongsToVisibleGraph.addAll(belongsToVisibleGraph);
        }
    }

    public BrokerType getBroker() {
        return brokerType;
    }

    public void setBroker(BrokerType broker) {
        this.brokerType = broker;
    }
}
