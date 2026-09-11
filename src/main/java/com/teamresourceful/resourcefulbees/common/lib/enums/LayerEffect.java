package com.teamresourceful.resourcefulbees.common.lib.enums;

import com.teamresourceful.resourcefullib.common.codecs.EnumCodec;

public enum LayerEffect {
    NONE,
    ENCHANTED,
    GLOW,
    TRANSLUCENT;

    public static final EnumCodec<LayerEffect> CODEC = EnumCodec.of(LayerEffect.class);

}
