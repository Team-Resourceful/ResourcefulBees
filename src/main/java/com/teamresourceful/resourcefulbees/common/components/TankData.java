package com.teamresourceful.resourcefulbees.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public record TankData(FluidStack fluid, int capacity) {

    public static final TankData EMPTY = new TankData(FluidStack.EMPTY, 0);
    public static final List<TankData> EMPTY_LIST = List.of();

    public static final Codec<TankData> CODEC = RecordCodecBuilder.create(i -> i.group(
            FluidStack.CODEC.fieldOf("fluid").forGetter(TankData::fluid),
            Codec.INT.fieldOf("capacity").forGetter(TankData::capacity)
    ).apply(i, TankData::new));

    public static final Codec<List<TankData>> LIST_CODEC = CODEC.listOf();

    public static final StreamCodec<RegistryFriendlyByteBuf, TankData> STREAM_CODEC = StreamCodec.composite(
            FluidStack.OPTIONAL_STREAM_CODEC,
            TankData::fluid,

            ByteBufCodecs.VAR_INT,
            TankData::capacity,

            TankData::new
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, List<TankData>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());

    public boolean isEmpty() {
        return this.equals(EMPTY);
    }
}
