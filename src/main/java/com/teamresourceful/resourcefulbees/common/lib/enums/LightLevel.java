package com.teamresourceful.resourcefulbees.common.lib.enums;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import net.minecraft.util.text.TranslationTextComponent;

public enum LightLevel {
    DAY(TranslationConstants.LightLevel.DAY),
    NIGHT(TranslationConstants.LightLevel.NIGHT),
    ANY(TranslationConstants.LightLevel.ANY);

    TranslationTextComponent component;

    LightLevel(TranslationTextComponent component) {
        this.component = component;
    }

    public static final Codec<LightLevel> CODEC = Codec.STRING.xmap(LightLevel::valueOf, LightLevel::toString);

    public TranslationTextComponent getDisplay() {
        return component;
    }
}
