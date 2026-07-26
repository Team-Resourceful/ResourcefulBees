package com.teamresourceful.resourcefulbees.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record CentrifugeRotations(int rotations) {

    public static final Codec<CentrifugeRotations> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("rotations").forGetter(CentrifugeRotations::rotations)
    ).apply(i, CentrifugeRotations::new));

    public static final StreamCodec<ByteBuf, CentrifugeRotations> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            CentrifugeRotations::rotations,
            CentrifugeRotations::new
    );

    public static final CentrifugeRotations DEFAULT = new CentrifugeRotations(0);
}
