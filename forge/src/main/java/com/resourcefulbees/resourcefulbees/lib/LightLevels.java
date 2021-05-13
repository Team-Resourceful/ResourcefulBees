package com.resourcefulbees.resourcefulbees.lib;

import com.mojang.serialization.Codec;

public enum LightLevels {
    DAY,
    NIGHT,
    ANY;

    public static final Codec<LightLevels> CODEC = Codec.STRING.xmap(LightLevels::valueOf, LightLevels::toString);
}
