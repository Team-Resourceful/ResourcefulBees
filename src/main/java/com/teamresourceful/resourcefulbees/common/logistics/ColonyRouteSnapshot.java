package com.teamresourceful.resourcefulbees.common.logistics;

import net.minecraft.core.BlockPos;

import java.util.UUID;

public record ColonyRouteSnapshot(
        UUID id,
        CourierResourceType resourceType,
        BlockPos source,
        BlockPos destination,
        boolean enabled,
        int priority,
        CourierRouteExecutionMode executionMode
) {

    public static ColonyRouteSnapshot from(CourierRoute route) {
        return new ColonyRouteSnapshot(
                route.id(),
                route.resourceType(),
                route.source().pos(),
                route.destination().pos(),
                route.enabled(),
                route.priority(),
                route.executionMode()
        );
    }
}