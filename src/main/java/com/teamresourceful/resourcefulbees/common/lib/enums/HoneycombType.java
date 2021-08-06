package com.teamresourceful.resourcefulbees.common.lib.enums;

import com.mojang.serialization.Codec;

public enum HoneycombType {
    NONE,
    DEFAULT,
    CUSTOM;

    public static final Codec<HoneycombType> CODEC = Codec.STRING.xmap(HoneycombType::valueOf, HoneycombType::toString);
}
