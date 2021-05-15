package com.teamresourceful.resourcefulbees.lib;

import com.mojang.serialization.Codec;

public enum ModelTypes {
    DEFAULT,
    ORE,
    GELATINOUS,
    DRAGON,
    QUEEN,
    VILLAGER,
    MUSHROOM,
    CROP,
    ARMORED,
    GUARDIAN;

    public static final Codec<ModelTypes> CODEC = Codec.STRING.xmap(ModelTypes::valueOf, ModelTypes::toString);
}
