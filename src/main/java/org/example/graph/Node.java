package org.example.graph;

import com.brunomnsilva.smartgraph.graphview.SmartLabelSource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
public class Node {

    @Getter
    @Setter
    private UUID id;

    @Getter
    @Setter
    private List<String> belongsToGraph;

    @Getter
    @Setter
    private Set<String> belongsToVisibleGraph;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private NodeType type;

    @Setter
    @Getter
    private BrokerType brokerType;

    @Setter
    @Getter
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

    public void removeBelongsToVisibleGraph(String graphName) {
        belongsToVisibleGraph.remove(graphName);
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
