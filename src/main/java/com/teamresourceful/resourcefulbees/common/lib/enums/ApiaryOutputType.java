package com.teamresourceful.resourcefulbees.common.lib.enums;

import com.teamresourceful.resourcefullib.common.codecs.EnumCodec;

public enum ApiaryOutputType {
    COMB,
    BLOCK;

    public static final EnumCodec<ApiaryOutputType> CODEC = EnumCodec.of(ApiaryOutputType.class);

    //made so we don't have to do equals everywhere
    public boolean isComb() {
        return this == COMB;
    }
}
