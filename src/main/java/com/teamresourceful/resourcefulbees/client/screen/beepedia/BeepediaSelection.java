package com.teamresourceful.resourcefulbees.client.screen.beepedia;

import net.minecraft.resources.Identifier;

sealed interface BeepediaSelection {
    enum Home implements BeepediaSelection {INSTANCE}

    record Bee(Identifier id) implements BeepediaSelection {
    }

    record Trait(String id) implements BeepediaSelection {
    }

    record Honey(String id) implements BeepediaSelection {
    }
}
