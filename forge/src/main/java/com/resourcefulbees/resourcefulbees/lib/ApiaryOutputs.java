package com.resourcefulbees.resourcefulbees.lib;

import com.mojang.serialization.Codec;

public enum ApiaryOutputs {
    COMB,
    BLOCK;

    public static final Codec<ApiaryOutputs> CODEC = Codec.STRING.xmap(ApiaryOutputs::valueOf, ApiaryOutputs::toString);
}
