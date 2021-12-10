package com.teamresourceful.resourcefulbees.common.lib.enums;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import net.minecraft.network.chat.TranslatableComponent;

public enum LightLevel {
    DAY(TranslationConstants.LightLevel.DAY),
    NIGHT(TranslationConstants.LightLevel.NIGHT),
    ANY(TranslationConstants.LightLevel.ANY);

    TranslatableComponent component;

    LightLevel(TranslatableComponent component) {
        this.component = component;
    }

    public static final Codec<LightLevel> CODEC = Codec.STRING.xmap(LightLevel::valueOf, LightLevel::toString);

    public TranslatableComponent getDisplay() {
        return component;
    }
}
