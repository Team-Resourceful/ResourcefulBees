package com.teamresourceful.resourcefulbees.common.logistics;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Objects;
import java.util.UUID;

public record CourierRoute(
        UUID id,
        CourierResourceType resourceType,
        CourierEndpoint source,
        CourierEndpoint destination,
        boolean enabled,
        int priority,
        CourierRouteExecutionMode executionMode,
        CourierFilter courierFilter
) {

    private static final Codec<UUID> UUID_CODEC = Codec.STRING.comapFlatMap(value -> {
        try {
            return DataResult.success(UUID.fromString(value));
        } catch (IllegalArgumentException _) {
            return DataResult.error(() -> "Invalid route UUID: " + value);
        }}, UUID::toString
    );

    public static final Codec<CourierRoute> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUID_CODEC.fieldOf("id")
                    .forGetter(CourierRoute::id),
            CourierResourceType.CODEC.fieldOf("resourceType")
                    .forGetter(CourierRoute::resourceType),
            CourierEndpoint.CODEC.fieldOf("source")
                    .forGetter(CourierRoute::source),
            CourierEndpoint.CODEC.fieldOf("destination")
                    .forGetter(CourierRoute::destination),
            Codec.BOOL.optionalFieldOf("enabled", true)
                    .forGetter(CourierRoute::enabled),
            Codec.intRange(0, 100)
                    .optionalFieldOf("priority", 0)
                    .forGetter(CourierRoute::priority),
            CourierRouteExecutionMode.CODEC
                    .optionalFieldOf("execution_mode", CourierRouteExecutionMode.COURIER)
                    .forGetter(CourierRoute::executionMode),
            CourierFilter.CODEC
                    .optionalFieldOf("courier_filter", CourierFilter.NONE)
                    .forGetter(CourierRoute::courierFilter)
    ).apply(instance, CourierRoute::new));

    public CourierRoute(UUID id, CourierResourceType resourceType, CourierEndpoint source, CourierEndpoint destination, boolean enabled, int priority) {
        this(id, resourceType, source, destination, enabled, priority, CourierRouteExecutionMode.COURIER, CourierFilter.NONE);
    }

    public CourierRoute {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(resourceType, "resourceType");
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(destination, "destination");
        Objects.requireNonNull(courierFilter, "courierFilter");

        if (source.equals(destination)) {
            // todo change these to send chat messages and not throw runtime exceptions
            throw new IllegalArgumentException(
                    "Source and destination cannot be identical"
            );
        }

        if (priority < 0) {
            throw new IllegalArgumentException(
                    "Route priority cannot be negative"
            );
        }
    }


    public CourierRoute withEnabled(boolean enabled) {
        return new CourierRoute(
                id, resourceType, source, destination,
                enabled, priority, executionMode, courierFilter
        );
    }

    public CourierRoute withPriority(int priority) {
        return new CourierRoute(
                id, resourceType, source, destination,
                enabled, priority, executionMode, courierFilter
        );
    }

    public CourierRoute withExecutionMode(
            CourierRouteExecutionMode mode
    ) {
        return new CourierRoute(
                id, resourceType, source, destination,
                enabled, priority, mode, courierFilter
        );
    }

    public CourierRoute withResourceType(
            CourierResourceType type
    ) {
        return new CourierRoute(
                id, type, source, destination,
                enabled, priority, executionMode, courierFilter
        );
    }

    public CourierRoute withEndpoints(
            CourierEndpoint source,
            CourierEndpoint destination
    ) {
        return new CourierRoute(
                id, resourceType, source, destination,
                enabled, priority, executionMode, courierFilter
        );
    }

    public CourierRoute withItemFilter(
            CourierFilter filter
    ) {
        return new CourierRoute(
                id, resourceType, source, destination,
                enabled, priority, executionMode, filter
        );
    }

}