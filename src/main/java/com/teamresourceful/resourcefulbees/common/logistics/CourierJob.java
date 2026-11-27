package com.teamresourceful.resourcefulbees.common.logistics;

import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;

import java.util.UUID;

public record CourierJob(
        UUID jobId,
        CourierResourceType resourceType,
        GlobalPos source,
        Direction sourceSide,
        GlobalPos destination,
        Direction destinationSide,
        int requestedAmount
) {}