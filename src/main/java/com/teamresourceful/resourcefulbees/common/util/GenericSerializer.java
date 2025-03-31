package com.teamresourceful.resourcefulbees.common.util;

import com.mojang.serialization.Codec;

public interface GenericSerializer<T> {

    Codec<? extends T> codec();

    String id();
}
