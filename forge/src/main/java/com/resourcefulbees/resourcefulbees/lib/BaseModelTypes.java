package com.resourcefulbees.resourcefulbees.lib;

import com.mojang.serialization.Codec;

public enum BaseModelTypes {
    DEFAULT,
    KITTEN;

    public static final Codec<BaseModelTypes> CODEC = Codec.STRING.xmap(BaseModelTypes::valueOf, BaseModelTypes::toString);
}
