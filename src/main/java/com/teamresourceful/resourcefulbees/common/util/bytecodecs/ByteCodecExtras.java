package com.teamresourceful.resourcefulbees.common.util.bytecodecs;

import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.ToDoubleFunction;

public final class ByteCodecExtras {

    //doesnt work right for some reason and idk why
    public static <T> StreamCodec<RegistryFriendlyByteBuf, WeightedCollection<T>> weightedCollection(StreamCodec<RegistryFriendlyByteBuf, T> codec, ToDoubleFunction<T> weighter) {
        return codec.apply(ByteBufCodecs.list()).map(t -> WeightedCollection.of(t, weighter), object -> object.stream().toList());
    }
}
