package com.teamresourceful.resourcefulbees.platform.client.events;

import com.teamresourceful.resourcefulbees.platform.common.events.base.EventHelper;
import net.minecraft.client.gui.screens.Screen;

public class ScreenOpenEvent {

    public static final EventHelper<ScreenOpenEvent> EVENT = new EventHelper<>();

    private Screen screen;

    public ScreenOpenEvent(Screen screen) {
        this.screen = screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public Screen getScreen() {
        return screen;
    }

}
