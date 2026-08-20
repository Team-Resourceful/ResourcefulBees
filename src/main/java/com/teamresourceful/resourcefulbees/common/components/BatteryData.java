package com.teamresourceful.resourcefulbees.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record BatteryData(int energy, int capacity, int maxTransfer) {

    public static final BatteryData EMPTY = new BatteryData(0, 0, 0);

    public static final Codec<BatteryData> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("energy").forGetter(BatteryData::energy),
            Codec.INT.fieldOf("capacity").forGetter(BatteryData::capacity),
            Codec.INT.fieldOf("maxTransfer").forGetter(BatteryData::maxTransfer)
    ).apply(i, BatteryData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BatteryData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            BatteryData::energy,

            ByteBufCodecs.VAR_INT,
            BatteryData::capacity,

            ByteBufCodecs.VAR_INT,
            BatteryData::maxTransfer,

            BatteryData::new
    );

    public boolean isEmpty() {
        return this == EMPTY;
    }
}
