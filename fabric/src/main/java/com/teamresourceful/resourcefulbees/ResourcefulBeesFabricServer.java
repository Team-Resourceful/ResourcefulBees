package com.teamresourceful.resourcefulbees;

import com.teamresourceful.resourcefulbees.platform.common.events.lifecycle.LoadingCompletedEvent;
import net.fabricmc.api.DedicatedServerModInitializer;

public class ResourcefulBeesFabricServer implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        LoadingCompletedEvent.fire();
    }
}
