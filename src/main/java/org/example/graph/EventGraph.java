package org.example.graph;

import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.models.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.util.OpenAPITranslator;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
public class EventGraph {

    private String name;

    @Setter
    private Set<Node> nodes = new HashSet<>();

    @Setter
    @JsonIgnore
    private Map<String, Schema> edges = new HashMap<>();

    @Setter
    private Set<Link> links = new HashSet<>();

    public void addNode(Node node) {
        boolean add = nodes.add(node);
        if(!add) {
            // find node in nodes

        }
    }

    public void addLink(Link link) {
        if(link.getWhat() != null && link.getSchema() != null) {
            edges.put(link.getWhat(), link.getSchema());
        }
        links.add(link);
        // get service from link
        Node service = link.getNode(NodeType.SERVICE);
        if(service != null) {
            link.getNode(NodeType.TOPIC).addBelongsToGraph(service.getBelongsToGraph());
        }
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

    public Map<String, Schema> getEdges() {
        return edges;
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
        g2.getNodes().forEach(n -> {
            Node node = merged.getNode(n.getName(), n.getType());
            if (node != null) {
                node.addBelongsToGraph(n.getBelongsToGraph());
                node.addBelongsToVisibleGraph(n.getBelongsToVisibleGraph());
                if(n.getBroker() != null) {
                    if(n.getBroker() != node.getBroker()) {
                        // TODO: throw exception
                        // print warning
                        System.out.println("Warning: different brokers in node " + n.getName() + ": "
                                + n.getBroker() + " and "
                                + node.getBroker());
                    }
                    node.setBroker(n.getBroker());
                }
            } else {
                merged.addNode(n);
            }
        });
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
        eventGraph.getNodes().stream()
                .filter(Node::isVisible)
                .forEach(g::insertVertex);
        eventGraph.getLinks().forEach(link -> {
            if(link.getFrom().isVisible() && link.getTo().isVisible()) {
                g.insertEdge(link.getFrom(), link.getTo(), link);
            }
        });

        return g;
    }

    @JsonIgnore
    public String getTitle() {
        return name;
    }

    public void makeInvisible(String graphName) {
        nodes.forEach(node -> node.removeBelongsToVisibleGraph(graphName));
        nodes.forEach(node -> {
            if(node.getBelongsToVisibleGraph().isEmpty()) {
                node.setVisible(false);
            }
        });

        links.forEach(link -> {
            if(link.getName().equals(graphName)) {
                link.setVisible(false);
            }
        });
    }

    public EventGraph plus(String filePath) {
        EventGraph eventGraph = OpenAPITranslator.parseOPenAPI(filePath);
        EventGraph merge = merge(this, eventGraph);
        return merge;
    }

    public List<Node> getNodesByType(NodeType nodeType) {
        return nodes.stream()
                .filter(node -> node.getType() == nodeType)
                .collect(Collectors.toList());
    }

    public void makeVisible(String name) {
        nodes.forEach(node -> {
            if(node.getBelongsToGraph().contains(name)) {
                node.setVisible(true);
                node.addBelongsToVisibleGraph(Collections.singleton(name));
            }
        });

        links.forEach(link -> {
            if(link.getName().equals(name)) {
                link.setVisible(true);
            }
        });
    }

    @JsonIgnore
    public Iterable<String> getEventNames() {
        return edges.keySet();
    }

    public void addEdge(String name, Schema schema) {
        if(edges == null) {
            edges = new HashMap<>();
        }
        edges.put(name, schema);
    }

    public void deleteService(String graphName) {
        nodes.forEach(node -> node.removeBelongsToVisibleGraph(graphName));
        nodes.removeIf(node -> node.getBelongsToVisibleGraph().isEmpty());
        links.removeIf(link -> link.getName().equals(graphName));
    }

    public void removeNodesWithoutLinks() {
        nodes.removeIf(node -> {
            boolean hasLink = links.stream()
                    .anyMatch(link -> link.getFrom().equals(node)
                            || link.getTo().equals(node));
            return !hasLink;
        });
    }
}

