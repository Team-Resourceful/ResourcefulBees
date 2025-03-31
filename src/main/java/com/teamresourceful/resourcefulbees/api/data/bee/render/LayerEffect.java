package com.teamresourceful.resourcefulbees.api.data.bee.render;

import com.teamresourceful.resourcefullib.common.codecs.EnumCodec;

public enum LayerEffect {
    NONE,
    ENCHANTED,
    GLOW;

    public static final EnumCodec<LayerEffect> CODEC = EnumCodec.of(LayerEffect.class);

}
