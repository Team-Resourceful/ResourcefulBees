package com.teamresourceful.resourcefulbees.api.data.trait;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

public final class AbilityDisplay {

    private AbilityDisplay() {
    }

    public static Component title(Identifier id) {
        return Component.translatable(
                Util.makeDescriptionId("trait_ability", id)
        );
    }

    public static Component description(Identifier id) {
        return Component.translatable(
                "trait_ability." + id.getNamespace()
                        + ".desc."
                        + id.getPath().replace('/', '.')
        );
    }
}