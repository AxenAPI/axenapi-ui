package org.example.graph;

import com.brunomnsilva.smartgraph.graphview.SmartLabelSource;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Link {
    private Node from;
    private Node to;
    private boolean visible;
    private String group;
    private Event event;

    public Link(Node from, Node to, String group, Event event) {
        this.from = from;
        this.to = to;
        this.visible = true;
        this.group = group;
        this.event = event;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return Objects.equals(from, link.from)
                && Objects.equals(to, link.to)
                && Objects.equals(event, link.event);
    }

    public int hashCode() {
        return Objects.hash(from, to, event);
    }

    public String toString() {
        return "Link{" +
                "from=" + from +
                ", to=" + to +
                ", what='" + event + '\'' +
                '}';
    }

    @SmartLabelSource
    public String view() {
        return event.getName();
    }

    public Node getNode(NodeType nodeType) {
        if (from.getType() == nodeType) {
            return from;
        } else if (to.getType() == nodeType) {
            return to;
        }
        return null;
    }

    @JsonIgnore
    public String getServiceName() {
        return getNode(NodeType.SERVICE).getName();
    }

    @JsonIgnore
    public BrokerType getBrokerType() {
       return getNode(NodeType.TOPIC).getBroker();
    }
}
