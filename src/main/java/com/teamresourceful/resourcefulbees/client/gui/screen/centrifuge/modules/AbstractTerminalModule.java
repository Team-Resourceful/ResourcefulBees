package com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge.modules;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public abstract class AbstractTerminalModule<T extends AbstractContainerScreen<?>> extends GuiComponent implements IDisplayModule {

    protected final T screen;

    protected AbstractTerminalModule(T screen) {
        this.screen = screen;
    }
}
