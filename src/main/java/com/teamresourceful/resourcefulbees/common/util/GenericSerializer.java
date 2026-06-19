package com.teamresourceful.resourcefulbees.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import java.util.Map;

public interface GenericSerializer<T> {

    MapCodec<? extends T> codec();

    String id();
}
