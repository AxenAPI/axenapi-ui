package org.example.graph;

import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Graph;
import org.example.util.OpenAPITranslator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class EventGraph {
    private String name;
    private Set<Node> nodes = new HashSet<>();
    private Set<Link> links = new HashSet<>();

    public void addNode(Node node) {
        nodes.add(node);
    }

    public void addLink(Link link) {
        links.add(link);
    }

    public boolean containsNode(String name, NodeType type) {
        return nodes
                .stream()
                .anyMatch(node -> node.getName().equals(name) && node.getType() == type);
    }

    public Node getNode(String name, NodeType nodeType) {
        return nodes
                .stream()
                .filter(node -> node.getName().equals(name) && node.getType() == nodeType)
                .findFirst()
                .orElse(null);
    }

    public void print() {
        System.out.println("Nodes: " + Arrays.deepToString(nodes.toArray()));
        System.out.println("Links: " + Arrays.deepToString(links.toArray()));
    }

    public Set<Link> getLinks() {
        return links;
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public static EventGraph merge(EventGraph g1, EventGraph g2) {
        EventGraph merged = new EventGraph();
        g1.getNodes().forEach(merged::addNode);
        g1.getLinks().forEach(merged::addLink);
        g2.getNodes().forEach(merged::addNode);
        g2.getLinks().forEach(merged::addLink);

        merged.links.forEach(l -> {
            Node node = merged.getNode(l.getFrom().getName(), l.getFrom().getType());
            l.setFrom(node);
            node = merged.getNode(l.getTo().getName(), l.getTo().getType());
            l.setTo(node);
        });

        return merged;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Graph<Node, Link> eventGraphToUIGraph(EventGraph eventGraph) {
        Graph<Node, Link> g = new DigraphEdgeList<>();
        eventGraph.getNodes().forEach(g::insertVertex);
        eventGraph.getLinks().forEach(link -> {
            g.insertEdge(link.getFrom(), link.getTo(), link);
        });

        return g;
    }

    public String getTitle() {
        return name;
    }

    public void minus(String graphName) {
        nodes.forEach(node -> node.removeBelongsToGraph(graphName));
        nodes.removeIf(node -> node.getBelongsToGraph().isEmpty());
        links.removeIf(link -> link.getName().equals(graphName));
    }

    public EventGraph plus(String filePath) {
        EventGraph eventGraph = OpenAPITranslator.parseOPenAPI(filePath);
        EventGraph merge = merge(this, eventGraph);
        return merge;
    }
}

