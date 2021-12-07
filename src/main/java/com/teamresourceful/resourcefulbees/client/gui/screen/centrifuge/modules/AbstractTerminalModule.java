package com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge.modules;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;

public abstract class AbstractTerminalModule<T extends ContainerScreen<?>> extends AbstractGui implements IDisplayModule {

    protected final T screen;

    protected AbstractTerminalModule(T screen) {
        this.screen = screen;
    }
}
