package com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge.modules;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;

public abstract class AbstractTerminalModule<T extends ContainerScreen<?>> extends Gui implements IDisplayModule {

    protected final T screen;

    protected AbstractTerminalModule(T screen) {
        this.screen = screen;
    }
}
