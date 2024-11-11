package org.example.demojavafx.datamodel;

public enum Color {
    RED(255, 0, 0), GREEN(0, 255, 0), BLUE(0, 0, 255),
    YELLOW(255, 255, 0), ORANGE(255, 165, 0),
    PURPLE(128, 0, 128), PINK(255, 192, 203),
    BROWN(165, 42, 42), BLACK(0, 0, 0), WHITE(255, 255, 255),
    GRAY(128, 128, 128), AQUA(0, 255, 255),
    SILVER(192, 192, 192), GOLD(255, 215, 0),
    LIME(0, 255, 0), TEAL(0, 128, 128), NAVY(0, 0, 128),
    MAROON(128, 0, 0), OLIVE(128, 128, 0), INDIGO(75, 0, 130);

    final int r, g, b;
    Color(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }
}
