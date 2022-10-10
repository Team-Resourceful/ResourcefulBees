package com.teamresourceful.resourcefulbees.common.lib.enums;

import com.mojang.serialization.Codec;

public enum LayerEffect {
    NONE,
    ENCHANTED,
    GLOW;

    public static final Codec<LayerEffect> CODEC = Codec.STRING.xmap(LayerEffect::valueOf, LayerEffect::toString);

}
