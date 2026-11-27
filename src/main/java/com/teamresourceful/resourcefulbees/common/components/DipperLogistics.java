
package com.teamresourceful.resourcefulbees.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.logistics.CourierEndpoint;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;

public record DipperLogistics(
        ResourceKey<Level> dimension,
        BlockPos controllerPos,
        UUID colonyId,
        Optional<CourierEndpoint> source
) {

    public DipperLogistics(
            ResourceKey<Level> dimension,
            BlockPos controllerPos,
            UUID colonyId
    ) {
        this(dimension, controllerPos, colonyId, Optional.empty());
    }

    public static final Codec<DipperLogistics> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Level.RESOURCE_KEY_CODEC.fieldOf("dimension")
                    .forGetter(DipperLogistics::dimension),
            BlockPos.CODEC.fieldOf("controller_pos")
                    .forGetter(DipperLogistics::controllerPos),
            UUIDUtil.CODEC.fieldOf("colony_id")
                    .forGetter(DipperLogistics::colonyId),
            CourierEndpoint.CODEC.optionalFieldOf("source")
                    .forGetter(DipperLogistics::source)
    ).apply(instance, DipperLogistics::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DipperLogistics> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION),
            DipperLogistics::dimension,
            BlockPos.STREAM_CODEC,
            DipperLogistics::controllerPos,
            UUIDUtil.STREAM_CODEC,
            DipperLogistics::colonyId,
            ByteBufCodecs.optional(CourierEndpoint.STREAM_CODEC),
            DipperLogistics::source,
            DipperLogistics::new
    );

    public DipperLogistics withSource(CourierEndpoint endpoint) {
        return new DipperLogistics(
                dimension,
                controllerPos,
                colonyId,
                Optional.of(endpoint)
        );
    }

    public DipperLogistics clearSource() {
        return new DipperLogistics(
                dimension,
                controllerPos,
                colonyId,
                Optional.empty()
        );
    }
}
