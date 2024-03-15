package org.example.graph;

import com.brunomnsilva.smartgraph.graphview.SmartLabelSource;

import java.util.*;

public class Node {

    private UUID id;
    private List<String> belongsToGraph;
    private String name;
    private NodeType type;

    public Node(String name, NodeType type, String belongsToGraph) {
        this.name = name;
        this.type = type;
        this.id = UUID.randomUUID();
        this.belongsToGraph = new ArrayList<>();
        this.belongsToGraph.add(belongsToGraph);
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
        if(this.belongsToGraph != null) {
            this.belongsToGraph.addAll(belongsToGraph);
        } else {
            this.belongsToGraph = new ArrayList<>();
            this.belongsToGraph.addAll(belongsToGraph);
        }
    }
}
