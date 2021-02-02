package com.resourcefulbees.resourcefulbees.init;

import com.resourcefulbees.resourcefulbees.api.beedata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;

import java.util.Map;

public class HoneySetup {
    public static void setupHoneyEffects() {
        Map<String, HoneyBottleData> honey = BeeRegistry.getRegistry().getHoneyBottles();
        honey.forEach((s, h) -> {
            h.getHoneyBottleRegistryObject().get().getFood();
        });

    }
}
