package com.teamresourceful.resourcefulbees.client.gui.widget;

public class SubScreenArea {

    public final int xPos;
    public final int yPos;
    public final int height;
    public final int width;

    public SubScreenArea(int xPos, int yPos, int height, int width) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.height = height;
        this.width = width;
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return false;
    }
}
