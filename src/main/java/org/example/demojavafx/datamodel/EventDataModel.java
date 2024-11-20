package org.example.demojavafx.datamodel;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(of = "title")
public class EventDataModel {
    private String title;
    private Color color;

    public EventDataModel(String title, Color c) {
        this.title = title;
        this.color = c;
    }

    public EventDataModel(String event) {
        this.title = event;
    }

    public String getTitle() {
        return title;
    }


    public void setColor(Color value) {
        this.color = value;
    }

    public String getCSSClass() {
        return color.cssClass;
    }

    public Color getColor() {
        return color;
    }
}
