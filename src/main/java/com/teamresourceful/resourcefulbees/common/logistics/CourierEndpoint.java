package com.teamresourceful.resourcefulbees.common.logistics;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;

public record CourierEndpoint(
        BlockPos pos,
        Direction side
) {

    public static final Codec<CourierEndpoint> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(CourierEndpoint::pos),
            Direction.CODEC.fieldOf("side").forGetter(CourierEndpoint::side)
    ).apply(instance, CourierEndpoint::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CourierEndpoint> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            CourierEndpoint::pos,
            Direction.STREAM_CODEC,
            CourierEndpoint::side,
            CourierEndpoint::new
    );

    public CourierEndpoint {
        Objects.requireNonNull(pos, "pos");
        Objects.requireNonNull(side, "side");
        pos = pos.immutable();
    }
}