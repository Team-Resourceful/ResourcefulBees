package com.teamresourceful.resourcefulbees.common.components;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.UUID;

public record DipperRouteEdit(
        ResourceKey<Level> dimension,
        BlockPos controllerPos,
        UUID colonyId,
        UUID routeId,
        EndpointType endpointType
) {
    public enum EndpointType {
        SOURCE,
        DESTINATION
    }
}