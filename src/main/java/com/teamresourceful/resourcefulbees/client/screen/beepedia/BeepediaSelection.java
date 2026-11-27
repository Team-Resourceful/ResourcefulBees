package com.teamresourceful.resourcefulbees.client.screen.beepedia;

import net.minecraft.resources.Identifier;

sealed interface BeepediaSelection {
    enum Home implements BeepediaSelection {INSTANCE}

    record Bee(Identifier id) implements BeepediaSelection {
    }

    record Trait(Identifier id) implements BeepediaSelection {
    }

    record Honey(Identifier id) implements BeepediaSelection {
    }
}
