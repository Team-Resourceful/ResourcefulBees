package com.teamresourceful.resourcefulbees.common.logistics;

import com.mojang.serialization.Codec;

public enum CourierResourceType {
    ITEM,
    FLUID,
    ENERGY;

    public static final Codec<CourierResourceType> CODEC = Codec.STRING.xmap(CourierResourceType::valueOf, CourierResourceType::toString);
}