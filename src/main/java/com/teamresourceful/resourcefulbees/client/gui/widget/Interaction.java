package com.teamresourceful.resourcefulbees.client.gui.widget;

import java.util.function.Supplier;

public class Interaction {
    private final int xPos;
    private final int yPos;
    private final int height;
    private final int width;
    final Supplier<Boolean> supplier;

    public Interaction(int xPos, int yPos, int width, int height, Supplier<Boolean> supplier) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
        this.supplier = supplier;
    }

    public boolean onMouseClick(int mouseX, int mouseY) {
        if (mouseX >= xPos && mouseY >= yPos && mouseX <= xPos + width && mouseY <= yPos + height) {
            return supplier.get();
        }
        return false;
    }
}