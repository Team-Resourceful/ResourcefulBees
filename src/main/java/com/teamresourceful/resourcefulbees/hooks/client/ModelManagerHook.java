package com.teamresourceful.resourcefulbees.hooks.client;

import net.minecraft.resources.Identifier;

public interface ModelManagerHook {

    default boolean rbees$hasCustomModel(Identifier model) {
        throw new UnsupportedOperationException();
    }
}