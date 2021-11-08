package com.teamresourceful.resourcefulbees.common.lib.enums;

import com.mojang.serialization.Codec;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public enum LightLevel {
    DAY("gui.resourcefulbees.light.day"),
    NIGHT("gui.resourcefulbees.light.night"),
    ANY("gui.resourcefulbees.light.any");

    TranslationTextComponent translationComponent;

    LightLevel(String translationKey) {
        this.translationComponent = new TranslationTextComponent(translationKey);
    }

    public static final Codec<LightLevel> CODEC = Codec.STRING.xmap(LightLevel::valueOf, LightLevel::toString);

    public ITextComponent getDisplay() {
        return translationComponent;
    }
}
