package org.example.demojavafx.markers;

public interface Exporter {
    enum Type {
        CODE, SPEC
    }
    void setExportPath(String exportPath);
    Exporter.Type getType();
}
