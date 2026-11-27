package com.teamresourceful.resourcefulbees.common.logistics;

import com.mojang.serialization.Codec;

public enum CourierFilterMode {
    NONE,
    WHITELIST,
    BLACKLIST;

    public static final Codec<CourierFilterMode> CODEC = Codec.STRING.xmap(CourierFilterMode::valueOf, CourierFilterMode::name);
}