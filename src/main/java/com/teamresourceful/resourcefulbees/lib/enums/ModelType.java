package com.teamresourceful.resourcefulbees.lib.enums;

import com.mojang.serialization.Codec;

public enum ModelType {
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

    public static final Codec<ModelType> CODEC = Codec.STRING.xmap(ModelType::valueOf, ModelType::toString);
}
