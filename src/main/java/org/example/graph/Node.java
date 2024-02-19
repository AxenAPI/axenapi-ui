package org.example.graph;

import com.brunomnsilva.smartgraph.graphview.SmartLabelSource;

import java.util.Objects;
import java.util.UUID;

public class Node {

    private UUID id;
    private String name;
    private NodeType type;

    public Node(String name, NodeType type) {
        this.name = name;
        this.type = type;
        this.id = UUID.randomUUID();
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
                '}';
    }

    @SmartLabelSource
    public String view() {
        return name;
    }
}
