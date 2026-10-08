package com.teamresourceful.resourcefulbees.common.resources.storage.beepedia;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.time.Instant;

public record DiscoveredBee(long discoveredAt) {

    public static final Codec<DiscoveredBee> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.fieldOf("discovered_at").forGetter(DiscoveredBee::discoveredAt)
    ).apply(instance, DiscoveredBee::new));

    public static final StreamCodec<ByteBuf, DiscoveredBee> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG,
            DiscoveredBee::discoveredAt,
            DiscoveredBee::new
    );

    public static DiscoveredBee now() {
        return new DiscoveredBee(Instant.now().getEpochSecond());
    }


}