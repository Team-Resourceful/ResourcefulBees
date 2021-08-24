package com.teamresourceful.resourcefulbees.client.gui.widget;

import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import javafx.geometry.BoundingBox;

public class BeepediaScreenArea {

    private final int xPos;
    private final int yPos;
    public final int height;
    public final int width;

    public BeepediaScreenArea(int xPos, int yPos, int width, int height) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.height = height;
        this.width = width;
    }

    public int getX(BeepediaScreen beepedia) {
        return beepedia.guiLeft + xPos;
    }

    public int getY(BeepediaScreen beepedia) {
        return beepedia.guiTop + yPos;
    }

    public BoundingBox getBoundingBox(BeepediaScreen beepedia) {
        return new BoundingBox(getX(beepedia), getY(beepedia), width, height);
    }

    public boolean isHovered(BeepediaScreen beepedia, double mouseX, double mouseY) {
        return getBoundingBox(beepedia).contains(mouseX, mouseY);
    }
}
