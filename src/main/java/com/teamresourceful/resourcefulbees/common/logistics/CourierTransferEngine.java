package com.teamresourceful.resourcefulbees.common.logistics;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.function.Predicate;

public final class CourierTransferEngine {

    public static final int DEFAULT_TRANSFER_LIMIT = 16;

    private CourierTransferEngine() {
    }

    public static TransferResult transfer(ServerLevel level, CourierRoute route) {
        return transfer(level, route, DEFAULT_TRANSFER_LIMIT);
    }

    public static TransferResult transfer(ServerLevel level, CourierRoute route, int maxItems) {
        if (!route.enabled()) {
            return result(TransferStatus.DISABLED);
        }

        if (route.resourceType() != CourierResourceType.ITEM) {
            return result(TransferStatus.UNSUPPORTED_RESOURCE);
        }

        if (maxItems <= 0) {
            return result(TransferStatus.INVALID_LIMIT);
        }

        CourierEndpoint sourceEndpoint = route.source();
        CourierEndpoint destinationEndpoint = route.destination();

        if (sourceEndpoint.pos().equals(destinationEndpoint.pos()) || isSameDoubleChest(level, sourceEndpoint.pos(), destinationEndpoint.pos())) {
            return result(TransferStatus.SOURCE_UNAVAILABLE);
        }

        if (!level.hasChunkAt(sourceEndpoint.pos())) {
            return result(TransferStatus.SOURCE_UNAVAILABLE);
        }

        if (!level.hasChunkAt(destinationEndpoint.pos())) {
            return result(TransferStatus.DESTINATION_UNAVAILABLE);
        }

        ResourceHandler<ItemResource> source = level.getCapability(Capabilities.Item.BLOCK, sourceEndpoint.pos(), sourceEndpoint.side());

        if (source == null) {
            return result(TransferStatus.SOURCE_UNAVAILABLE);
        }

        ResourceHandler<ItemResource> destination = level.getCapability(Capabilities.Item.BLOCK, destinationEndpoint.pos(), destinationEndpoint.side());

        if (destination == null) {
            return result(TransferStatus.DESTINATION_UNAVAILABLE);
        }

        return transferItems(source, destination, maxItems, resource -> route.courierFilter().allows(resource.toStack().getItem()));
    }


    public static TransferResult transferItems(ResourceHandler<ItemResource> source, ResourceHandler<ItemResource> destination, int maxItems) {
        return transferItems(source, destination, maxItems, _ -> true);
    }

    public static TransferResult transferItems(ResourceHandler<ItemResource> source, ResourceHandler<ItemResource> destination, int maxItems, Predicate<ItemResource> filter) {
        if (maxItems <= 0) {
            return result(TransferStatus.INVALID_LIMIT);
        }

        if (source == destination) {
            return result(TransferStatus.SOURCE_UNAVAILABLE);
        }

        boolean foundEligibleItems = false;

        for (int slot = 0; slot < source.size(); slot++) {
            int available = source.getAmountAsInt(slot);

            if (available <= 0) {
                continue;
            }

            ItemResource resource = source.getResource(slot);

            // Never extract items rejected by the filter.
            if (!filter.test(resource)) {
                continue;
            }

            foundEligibleItems = true;

            int requested = Math.min(available, maxItems);

            int moved = attemptTransfer(source, destination, slot, resource, requested);

            if (moved > 0) {
                TransferStatus status = moved == maxItems ? TransferStatus.SUCCESS : TransferStatus.PARTIAL;

                return new TransferResult(status, moved);
            }
        }

        return result(foundEligibleItems ? TransferStatus.DESTINATION_FULL : TransferStatus.SOURCE_EMPTY);
    }


    /**
     * Attempts the largest transferable quantity first.
     * <p>
     * Failed attempts are rolled back before retrying.
     */
    private static int attemptTransfer(ResourceHandler<ItemResource> source, ResourceHandler<ItemResource> destination, int sourceSlot, ItemResource resource, int requested) {
        int limit = requested;

        while (limit > 0) {
            int retryLimit = 0;

            try (Transaction transaction = Transaction.openRoot()) {

                int extracted = source.extract(sourceSlot, resource, limit, transaction);

                if (extracted <= 0) {
                    return 0;
                }

                int inserted = destination.insert(resource, extracted, transaction);

                if (inserted == extracted) {
                    transaction.commit();
                    return inserted;
                }

                // Closing without committing rolls back both operations.
                retryLimit = Math.min(inserted, extracted);
            }

            if (retryLimit <= 0) {
                return 0;
            }

            limit = retryLimit;
        }

        return 0;
    }

    private static TransferResult result(TransferStatus status) {
        return new TransferResult(status, 0);
    }


    private static boolean isSameDoubleChest(ServerLevel level, BlockPos source, BlockPos destination) {
        BlockState sourceState = level.getBlockState(source);
        BlockState destinationState = level.getBlockState(destination);

        if (!(sourceState.getBlock() instanceof ChestBlock) || sourceState.getBlock() != destinationState.getBlock()) {
            return false;
        }

        ChestType sourceType = sourceState.getValue(ChestBlock.TYPE);
        ChestType destinationType = destinationState.getValue(ChestBlock.TYPE);

        if (sourceType == ChestType.SINGLE || destinationType == ChestType.SINGLE || sourceType == destinationType) {
            return false;
        }

        if (sourceState.getValue(ChestBlock.FACING) != destinationState.getValue(ChestBlock.FACING)) {
            return false;
        }

        return source.relative(ChestBlock.getConnectedDirection(sourceState)).equals(destination) && destination.relative(ChestBlock.getConnectedDirection(destinationState)).equals(source);
    }

}
