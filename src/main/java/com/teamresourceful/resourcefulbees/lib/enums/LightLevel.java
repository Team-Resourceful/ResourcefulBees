package com.teamresourceful.resourcefulbees.lib.enums;

import com.mojang.serialization.Codec;

public enum LightLevel {
    DAY,
    NIGHT,
    ANY;

    public static final Codec<LightLevel> CODEC = Codec.STRING.xmap(LightLevel::valueOf, LightLevel::toString);
}
