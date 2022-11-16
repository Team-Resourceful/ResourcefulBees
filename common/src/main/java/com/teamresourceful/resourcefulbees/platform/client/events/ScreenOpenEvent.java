package com.teamresourceful.resourcefulbees.platform.client.events;

import net.minecraft.client.gui.screens.Screen;

public class ScreenOpenEvent {

    private Screen screen;

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public Screen getScreen() {
        return screen;
    }

}
