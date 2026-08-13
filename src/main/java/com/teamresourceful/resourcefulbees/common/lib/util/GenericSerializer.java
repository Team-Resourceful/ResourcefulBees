package com.teamresourceful.resourcefulbees.common.lib.util;

import com.mojang.serialization.MapCodec;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.MutationType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface GenericSerializer<T extends MutationType> {

    StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();

    MapCodec<T> codec();

    String id();
}
