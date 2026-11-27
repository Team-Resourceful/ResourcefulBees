package com.teamresourceful.resourcefulbees.common.logistics.courier;

import com.teamresourceful.resourcefulbees.common.logistics.CourierEnergyTransferEngine;
import com.teamresourceful.resourcefulbees.common.logistics.CourierFluidTransferEngine;
import com.teamresourceful.resourcefulbees.common.logistics.CourierRoute;
import com.teamresourceful.resourcefulbees.common.logistics.CourierRouteExecutionMode;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public final class CourierWorkProbe {

    private CourierWorkProbe() {
    }

    public static boolean hasPendingWork(ServerLevel level, CourierRoute route) {
        if (!route.enabled() || route.executionMode() != CourierRouteExecutionMode.COURIER || route.source().pos().equals(route.destination().pos()) || !level.hasChunkAt(route.source().pos()) || !level.hasChunkAt(route.destination().pos())) {
            return false;
        }

        return switch (route.resourceType()) {
            case ITEM -> hasItemWork(level, route);
            case FLUID -> hasFluidWork(level, route);
            case ENERGY -> hasEnergyWork(level, route);
        };
    }

    private static boolean hasItemWork(ServerLevel level, CourierRoute route) {
        ResourceHandler<ItemResource> source = level.getCapability(Capabilities.Item.BLOCK, route.source().pos(), route.source().side());
        ResourceHandler<ItemResource> destination = level.getCapability(Capabilities.Item.BLOCK, route.destination().pos(), route.destination().side());

        if (source == null || destination == null || source == destination) {
            return false;
        }

        for (int slot = 0; slot < source.size(); slot++) {
            int available = source.getAmountAsInt(slot);

            if (available <= 0) {
                continue;
            }

            ItemResource resource = source.getResource(slot);

            if (resource.isEmpty() || !route.courierFilter().allows(resource.toStack().getItem())) {
                continue;
            }

            if (canTransferItem(source, destination, slot, resource, Math.min(available, CourierDeliveryExecutor.DEFAULT_CARGO_LIMIT))) {
                return true;
            }
        }

        return false;
    }

    private static boolean canTransferItem(ResourceHandler<ItemResource> source, ResourceHandler<ItemResource> destination, int slot, ItemResource resource, int amount) {
        // Every attempt is rolled back.
        while (amount > 0) {
            int retry;

            try (Transaction transaction = Transaction.openRoot()) {
                int extracted = source.extract(slot, resource, amount, transaction);

                if (extracted <= 0) {
                    return false;
                }

                int inserted = destination.insert(resource, extracted, transaction);

                if (inserted == extracted) {
                    return true;
                }

                retry = Math.min(extracted, inserted);
            }

            if (retry <= 0 || retry >= amount) {
                return false;
            }

            amount = retry;
        }

        return false;
    }

    private static boolean hasFluidWork(ServerLevel level, CourierRoute route) {
        ResourceHandler<FluidResource> source = level.getCapability(Capabilities.Fluid.BLOCK, route.source().pos(), route.source().side());
        ResourceHandler<FluidResource> destination = level.getCapability(Capabilities.Fluid.BLOCK, route.destination().pos(), route.destination().side());

        if (source == null || destination == null || source == destination) {
            return false;
        }

        for (int tank = 0; tank < source.size(); tank++) {
            int available = source.getAmountAsInt(tank);

            if (available <= 0) {
                continue;
            }

            FluidResource resource = source.getResource(tank);

            if (resource.isEmpty() || !route.courierFilter().allows(resource.getFluid())) {
                continue;
            }

            if (canTransferFluid(source, destination, tank, resource, Math.min(available, CourierFluidTransferEngine.DEFAULT_TRANSFER_LIMIT))) {
                return true;
            }
        }

        return false;
    }

    private static boolean canTransferFluid(ResourceHandler<FluidResource> source, ResourceHandler<FluidResource> destination, int tank, FluidResource resource, int amount) {
        while (amount > 0) {
            int retry;

            try (Transaction transaction = Transaction.openRoot()) {
                int extracted = source.extract(tank, resource, amount, transaction);

                if (extracted <= 0) {
                    return false;
                }

                int inserted = destination.insert(resource, extracted, transaction);

                if (inserted == extracted) {
                    return true;
                }

                retry = Math.min(extracted, inserted);
            }

            if (retry <= 0 || retry >= amount) {
                return false;
            }

            amount = retry;
        }

        return false;
    }

    private static boolean hasEnergyWork(ServerLevel level, CourierRoute route) {
        EnergyHandler source = level.getCapability(Capabilities.Energy.BLOCK, route.source().pos(), route.source().side());
        EnergyHandler destination = level.getCapability(Capabilities.Energy.BLOCK, route.destination().pos(), route.destination().side());

        if (source == null || destination == null || source == destination || source.getAmountAsInt() <= 0) {
            return false;
        }

        int amount = Math.min(source.getAmountAsInt(), CourierEnergyTransferEngine.DEFAULT_TRANSFER_LIMIT);

        try (Transaction transaction = Transaction.openRoot()) {
            int extracted = source.extract(amount, transaction);

            if (extracted <= 0) {
                return false;
            }

            int inserted = destination.insert(extracted, transaction);

            return inserted > 0;
        }
    }
}
