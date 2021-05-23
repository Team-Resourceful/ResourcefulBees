package com.teamresourceful.resourcefulbees.lib;

import com.mojang.serialization.Codec;

public enum ModelTypes {
    DEFAULT,
    ORE,
    SLIME,
    DRAGON,
    QUEEN,
    VILLAGER,
    MUSHROOM,
    CROP,
    ARMORED,
    GUARDIAN,
    CLOAKED,
    YETI;

    public static final Codec<ModelTypes> CODEC = Codec.STRING.xmap(ModelTypes::valueOf, ModelTypes::toString);
}
