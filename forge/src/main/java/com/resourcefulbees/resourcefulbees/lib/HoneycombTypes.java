package com.resourcefulbees.resourcefulbees.lib;

import com.mojang.serialization.Codec;

public enum HoneycombTypes {
    NONE,
    DEFAULT,
    CUSTOM;

    public static final Codec<HoneycombTypes> CODEC = Codec.STRING.xmap(HoneycombTypes::valueOf, HoneycombTypes::toString);
}
