package com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge.modules;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

//TODO The hierarchy of these classes needs to be changed if we want this to be useful beyond just the centrifuge
public abstract class AbstractDisplayModule<T extends AbstractContainerScreen<?>> extends AbstractTerminalModule<T> {

    protected final int x;
    protected final int y;
    protected final int width;
    protected final int height;
    protected boolean isActive;

    protected AbstractDisplayModule(int x, int y, int width, int height, T screen) {
        super(screen);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMaxX() {
        return x + width;
    }

    public int getMaxY() {
        return y + height;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
