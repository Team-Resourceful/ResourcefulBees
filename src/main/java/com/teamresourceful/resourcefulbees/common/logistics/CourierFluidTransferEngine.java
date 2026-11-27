package com.teamresourceful.resourcefulbees.common.logistics;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.Objects;


public final class CourierFluidTransferEngine {
    public static final int DEFAULT_TRANSFER_LIMIT = 1_000;

    private CourierFluidTransferEngine() {}

    public static TransferResult transfer(ServerLevel level, CourierRoute route, int maxFluid) {
        if (!route.enabled()) return result(TransferStatus.DISABLED);
        if (route.resourceType() != CourierResourceType.FLUID) {
            return result(TransferStatus.UNSUPPORTED_RESOURCE);
        }
        if (maxFluid <= 0) return result(TransferStatus.INVALID_LIMIT);

        var sourcePos = route.source().pos();
        var destinationPos = route.destination().pos();
        if (sourcePos.equals(destinationPos)) {
            return result(TransferStatus.SOURCE_UNAVAILABLE);
        }
        if (!level.hasChunkAt(sourcePos)) {
            return result(TransferStatus.SOURCE_UNAVAILABLE);
        }
        if (!level.hasChunkAt(destinationPos)) {
            return result(TransferStatus.DESTINATION_UNAVAILABLE);
        }

        ResourceHandler<FluidResource> source = level.getCapability(
                Capabilities.Fluid.BLOCK, sourcePos, route.source().side());
        if (source == null) return result(TransferStatus.SOURCE_UNAVAILABLE);

        ResourceHandler<FluidResource> destination = level.getCapability(
                Capabilities.Fluid.BLOCK, destinationPos, route.destination().side());
        if (destination == null) return result(TransferStatus.DESTINATION_UNAVAILABLE);

        return transferFluids(source, destination, maxFluid, route.courierFilter());
    }


    public static TransferResult transferFluids(
            ResourceHandler<FluidResource> source,
            ResourceHandler<FluidResource> destination,
            int maxFluid
    ) {
        return transferFluids(
                source,
                destination,
                maxFluid,
                CourierFilter.NONE
        );
    }

    public static TransferResult transferFluids(
            ResourceHandler<FluidResource> source,
            ResourceHandler<FluidResource> destination,
            int maxFluid,
            CourierFilter filter
    ) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(destination, "destination");
        Objects.requireNonNull(filter, "filter");

        if (maxFluid <= 0) {
            return result(
                    TransferStatus.INVALID_LIMIT
            );
        }

        if (source == destination) {
            return result(
                    TransferStatus.SOURCE_UNAVAILABLE
            );
        }

        boolean foundAllowedFluid = false;

        for (int tank = 0; tank < source.size(); tank++) {
            int available = source.getAmountAsInt(tank);

            if (available <= 0) {
                continue;
            }

            FluidResource resource = source.getResource(tank);

            if (resource.isEmpty()) {
                continue;
            }

            if (!filter.allows(resource.getFluid())) {
                continue;
            }

            foundAllowedFluid = true;

            int moved = attemptTransfer(
                    source,
                    destination,
                    tank,
                    resource,
                    Math.min(available, maxFluid)
            );

            if (moved > 0) {
                return new TransferResult(
                        moved == maxFluid
                                ? TransferStatus.SUCCESS
                                : TransferStatus.PARTIAL,
                        moved
                );
            }
        }

        return result(
                foundAllowedFluid
                        ? TransferStatus.DESTINATION_FULL
                        : TransferStatus.SOURCE_EMPTY
        );
    }


    private static int attemptTransfer(
            ResourceHandler<FluidResource> source,
            ResourceHandler<FluidResource> destination,
            int tank,
            FluidResource resource,
            int requested
    ) {
        int limit = requested;
        while (limit > 0) {
            int retryLimit;
            try (Transaction transaction = Transaction.openRoot()) {
                int extracted = source.extract(tank, resource, limit, transaction);
                if (extracted <= 0) return 0;
                int inserted = destination.insert(resource, extracted, transaction);
                if (inserted == extracted) {
                    transaction.commit();
                    return inserted;
                }
                // Both operations are rolled back on close; retry at the accepted quantity.
                retryLimit = Math.min(inserted, extracted);
            }
            if (retryLimit <= 0 || retryLimit >= limit) return 0;
            limit = retryLimit;
        }
        return 0;
    }

    private static TransferResult result(TransferStatus status) {
        return new TransferResult(status, 0);
    }
}
