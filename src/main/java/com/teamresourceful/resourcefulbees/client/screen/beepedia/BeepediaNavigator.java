package com.teamresourceful.resourcefulbees.client.screen.beepedia;

import net.minecraft.resources.Identifier;

public interface BeepediaNavigator {

    void openBee(Identifier id);

    void openTrait(String id);

    void openHoney(String id);
}