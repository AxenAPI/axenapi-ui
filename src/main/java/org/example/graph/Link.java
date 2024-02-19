package org.example.graph;

import com.brunomnsilva.smartgraph.graphview.SmartLabelSource;

import java.util.Objects;

public class Link {
     private Node from;
     private Node to;
     private String what;

     public Link(Node from, Node to, String what) {
         this.from = from;
         this.to = to;
         this.what = what;
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
}
