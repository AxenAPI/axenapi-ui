package org.example.graph;

import com.brunomnsilva.smartgraph.graphview.SmartLabelSource;
import io.swagger.v3.oas.models.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Link {
    private String name;
     private Node from;
     private Node to;
     private Schema schema;
     private String what;
    // todo: set broker into link
    private BrokerType brokerType;

     private boolean visible;
    // todo: set group into link
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

    @SmartLabelSource
    public String view() {
        return what;
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
