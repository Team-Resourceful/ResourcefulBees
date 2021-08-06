package com.teamresourceful.resourcefulbees.common.lib.enums;

import com.mojang.serialization.Codec;

public enum MutationType {
    NONE,
    ITEM,
    BLOCK,
    ENTITY;

    public static final Codec<MutationType> CODEC = Codec.STRING.xmap(MutationType::valueOf, MutationType::toString).stable();
}
