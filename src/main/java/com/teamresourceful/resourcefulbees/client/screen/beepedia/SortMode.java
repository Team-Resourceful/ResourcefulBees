package com.teamresourceful.resourcefulbees.client.screen.beepedia;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;

enum SortMode {
    ALPHABETICAL("Alphabetical"),
    ID("Identifier"),
    DISCOVERED("Discovery Time");

    private final MutableComponent displayName;

    SortMode(String name) {
        this.displayName = Component.literal(name);
    }

    Component displayName() {
        return displayName.withColor(TextColor.WHITE);
    }
}
