package com.teamresourceful.resourcefulbees.api.tiers;

import com.teamresourceful.resourcefullib.common.codecs.EnumCodec;

public enum ApiaryOutputType {
    COMB,
    BLOCK;

    public static final EnumCodec<ApiaryOutputType> CODEC = EnumCodec.of(ApiaryOutputType.class);

    public boolean isComb() {
        return this == COMB;
    }
}