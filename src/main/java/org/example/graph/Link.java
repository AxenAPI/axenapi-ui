package org.example.graph;

import com.brunomnsilva.smartgraph.graphview.SmartLabelSource;
import io.swagger.v3.oas.models.media.Schema;

import java.util.Objects;

public class Link {
    private String name;
     private Node from;
     private Node to;
     private Schema schema;
     private String what;
     private BrokerType brokerType;

     private boolean visible;
     private String group;

    public Link(Node from, Node to, String what, String name, Schema schema, BrokerType brokerType, String group) {
         this.from = from;
         this.to = to;
         this.what = what;
         this.name = name;
         this.visible = true;
         this.schema = schema;
         this.brokerType = brokerType;
         this.group = group;
     }

     public boolean equals(Object o) {
         if (this == o) return true;
         if (o == null || getClass() != o.getClass()) return false;
         Link link = (Link) o;
         return Objects.equals(from, link.from)
                 && Objects.equals(to, link.to)
                 && Objects.equals(what, link.what);
     }

     public int hashCode() {
         return Objects.hash(from, to, what);
     }

     public String toString() {
         return "Link{" +
                 "from=" + from +
                 ", to=" + to +
                 ", what='" + what + '\'' +
                 '}';
     }

    public Node getFrom() {
        return from;
    }

    public Node getTo() {
        return to;
    }

    public String getWhat() {
        return what;
    }

    @SmartLabelSource
    public String view() {
        return what;
    }

    public void setFrom(Node node) {
        from = node;
    }

    public void setTo(Node node) {
        to = node;
    }

    public String getName() {
        return name;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public Schema getSchema() {
        return schema;
    }

    public BrokerType getBrokerType() {
         // todo: set broker into link
        return brokerType;
    }

    public String getGroup() {
         // todo: set group into link
        return group;
    }

    public Node getNode(NodeType nodeType) {
        if(from.getType() == nodeType) {
            return from;
        } else if (to.getType() == nodeType) {
            return to;
        }
        return null;
    }
}
