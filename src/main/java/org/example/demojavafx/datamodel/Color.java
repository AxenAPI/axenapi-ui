package org.example.demojavafx.datamodel;

public enum Color {
    RED(255, 0, 0, "edgeRed"), GREEN(0, 255, 0, "edgeGreen"), BLUE(0, 0, 255, "edgeBlue"),
    YELLOW(255, 255, 0, "edgeYellow"), ORANGE(255, 165, 0, "edgeOrange"),
    PURPLE(128, 0, 128, "edgePurple"), PINK(255, 192, 203, "edgePink"),
    BROWN(165, 42, 42, "edgeBrown"), WHITE(255, 255, 255, "edgeWhite"),
    GRAY(128, 128, 128, "edgeGray"), AQUA(0, 255, 255, "edgeAqua"),
    SILVER(192, 192, 192, "edgeSilver"), GOLD(255, 215, 0, "edgeGold"),
    LIME(0, 255, 0, "edgeLime"), TEAL(0, 128, 128, "edgeTeal"), NAVY(0, 0, 128, "edgeNavy"),
    MAROON(128, 0, 0, "edgeMaroon"), OLIVE(128, 128, 0, "edgeOlive"), INDIGO(75, 0, 130, "edgeIndigo");

    public final int r, g, b;
    public final String cssClass;
    Color(int r, int g, int b, String cssClass) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.cssClass = cssClass;
    }
}
