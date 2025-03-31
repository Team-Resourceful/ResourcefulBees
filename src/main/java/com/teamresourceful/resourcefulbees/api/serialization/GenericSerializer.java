package com.teamresourceful.resourcefulbees.api.serialization;

import com.mojang.serialization.Codec;

public interface GenericSerializer<T> {

    Codec<? extends T> codec();

    String id();
}
