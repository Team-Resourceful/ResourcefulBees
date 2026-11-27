package com.teamresourceful.resourcefulbees.common.logistics;

import com.mojang.serialization.Codec;

public enum CourierRouteExecutionMode {
    INSTANT,
    COURIER;

    public static final Codec<CourierRouteExecutionMode> CODEC = Codec.STRING.xmap(CourierRouteExecutionMode::valueOf, CourierRouteExecutionMode::toString);
}