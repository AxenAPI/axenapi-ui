package org.example.demojavafx.markers;

public interface Exporter {
    enum Type {
        CODE, SPEC
    }
    Exporter.Type getType();
}
