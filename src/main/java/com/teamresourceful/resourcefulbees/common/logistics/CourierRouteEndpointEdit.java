package com.teamresourceful.resourcefulbees.common.logistics;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.UUID;

public record CourierRouteEndpointEdit(
        ResourceKey<Level> dimension,
        BlockPos controllerPos,
        UUID colonyId,
        UUID routeId,
        EndpointType endpointType
) {

    public CourierRouteEndpointEdit {
        Objects.requireNonNull(dimension, "dimension");
        Objects.requireNonNull(controllerPos, "controllerPos");
        Objects.requireNonNull(colonyId, "colonyId");
        Objects.requireNonNull(routeId, "routeId");
        Objects.requireNonNull(endpointType, "endpointType");

        controllerPos = controllerPos.immutable();
    }

    public enum EndpointType {
        SOURCE,
        DESTINATION
    }

    public static final Codec<EndpointType> ENDPOINT_TYPE_CODEC =
            Codec.STRING.xmap(EndpointType::valueOf, EndpointType::name);

    public static final Codec<CourierRouteEndpointEdit> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Level.RESOURCE_KEY_CODEC.fieldOf("dimension")
                    .forGetter(CourierRouteEndpointEdit::dimension),
            BlockPos.CODEC.fieldOf("controller_pos")
                    .forGetter(CourierRouteEndpointEdit::controllerPos),
            UUIDUtil.CODEC.fieldOf("colony_id")
                    .forGetter(CourierRouteEndpointEdit::colonyId),
            UUIDUtil.CODEC.fieldOf("route_id")
                    .forGetter(CourierRouteEndpointEdit::routeId),
            ENDPOINT_TYPE_CODEC.fieldOf("endpoint_type")
                    .forGetter(CourierRouteEndpointEdit::endpointType)
    ).apply(instance, CourierRouteEndpointEdit::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CourierRouteEndpointEdit> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION),
            CourierRouteEndpointEdit::dimension,
            BlockPos.STREAM_CODEC,
            CourierRouteEndpointEdit::controllerPos,
            UUIDUtil.STREAM_CODEC,
            CourierRouteEndpointEdit::colonyId,
            UUIDUtil.STREAM_CODEC,
            CourierRouteEndpointEdit::routeId,
            ByteBufCodecs.idMapper(
                    index -> EndpointType.values()[index],
                    EndpointType::ordinal
            ),
            CourierRouteEndpointEdit::endpointType,
            CourierRouteEndpointEdit::new
    );
}