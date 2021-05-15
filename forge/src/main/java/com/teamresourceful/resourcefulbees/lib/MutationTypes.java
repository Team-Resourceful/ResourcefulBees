package com.teamresourceful.resourcefulbees.lib;

import com.mojang.serialization.Codec;

public enum MutationTypes {
    NONE,
    ITEM,
    BLOCK,
    ENTITY;

    public static final Codec<MutationTypes> CODEC = Codec.STRING.xmap(MutationTypes::valueOf, MutationTypes::toString).stable();
}
