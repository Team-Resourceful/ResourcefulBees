package com.teamresourceful.resourcefulbees.client;

import com.teamresourceful.resourcefulbees.client.screen.MissingRegistryScreen;
import com.teamresourceful.resourcefulbees.platform.client.events.ScreenOpenEvent;

public class ResourcefulBeesClient {

    public static void init() {
        ScreenOpenEvent.EVENT.addListener(MissingRegistryScreen::onScreenChange);
    }
}
