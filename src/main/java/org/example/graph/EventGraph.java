package org.example.graph;

import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.demojavafx.datamodel.Color;
import org.example.util.OpenAPITranslator;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
public class EventGraph {

    private String name;

    @Setter
    private Set<Node> nodes = new HashSet<>();

    @Getter
    @Setter
    private Map<String, Event> events = new HashMap<>();

    @Setter
    private Set<Link> links = new HashSet<>();

    public void addNode(Node node) {
        boolean add = nodes.add(node);
        if(!add) {
            // find node in nodes

        }
    }

    public void addLink(Link link) {
        if(link.getEvent() == null) {
            throw new IllegalStateException("Event in link is null");
        }

        if(events.get(link.getEvent().getName()) == null) {
            throw new IllegalStateException("Event " + link.getEvent().getName() + " not found");
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

    public Set<Link> getLinks() {
        return links;
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public static EventGraph merge(EventGraph g1, EventGraph g2) {
        EventGraph merged = new EventGraph();

        g1.getNodes().forEach(merged::addNode);
        merged.addEvents(g1.getEvents());
        merged.addEvents(g2.getEvents());
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
            Event event = l.getEvent();
            Event mergedEvent = merged.getEvent(event.getName());
            if(mergedEvent == null) {
                throw new IllegalStateException("Event " + event.getName() + " not found in merged graph");
            }
            l.setEvent(mergedEvent);
        });

        return merged;
    }

    private void addEvents(Map<String, Event> events) {
        events.values().forEach(e -> {
            if(!this.events.containsKey(e.getName())) {
                e.setColor(getEventColor());
                this.events.put(e.getName(), e);
            }
        });
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
            if(link.getServiceName().equals(graphName)) {
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
            if(link.getServiceName().equals(name)) {
                link.setVisible(true);
            }
        });
    }

    @JsonIgnore
    public Iterable<String> getEventNames() {
        return events.keySet();
    }

    public void deleteService(String graphName) {
        nodes.forEach(node -> node.removeBelongsToVisibleGraph(graphName));
        nodes.removeIf(node -> node.getBelongsToVisibleGraph().isEmpty());
        links.removeIf(link -> link.getServiceName().equals(graphName));
    }

    public void removeNodesWithoutLinks() {
        nodes.removeIf(node -> {
            boolean hasLink = links.stream()
                    .anyMatch(link -> link.getFrom().equals(node)
                            || link.getTo().equals(node));
            return !hasLink;
        });
    }

    @JsonIgnore
    public Event getEvent(String event) {
        return events.get(event);
    }

    @JsonIgnore
    public Color getEventColor() {
        if(events == null || events.isEmpty()) {
            return Color.values()[0];
        }
        return Color.values()[events.size() % Color.values().length];
    }

    @JsonIgnore
    public String getNextTopicName() {
        List<Node> topics = getNodesByType(NodeType.TOPIC);
        return "new_topic" + (topics.size() + 1);
    }

    public void addEvent(Event event) {
        if(!events.containsKey(event.getName())) {
            events.put(event.getName(), event);
        }
    }
}

