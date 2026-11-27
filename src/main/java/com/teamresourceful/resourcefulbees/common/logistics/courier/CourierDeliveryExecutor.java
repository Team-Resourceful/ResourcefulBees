package com.teamresourceful.resourcefulbees.common.logistics.courier;

import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.logistics.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.VisibleForDebug;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;

public final class CourierDeliveryExecutor {
    public static final int DEFAULT_CARGO_LIMIT = 16;
    public static final double INTERACTION_DISTANCE_SQUARED = 4.0D;

    private CourierDeliveryExecutor() {
    }

    @VisibleForDebug
    public static TransferResult pickup(ServerLevel level, CourierRoute route, ResourceHandler<ItemResource> cargo, int maxItems) {
        var invalid = validate(route, CourierResourceType.ITEM, maxItems);
        if (invalid != null) return invalid;
        if (!isCargoEmpty(cargo)) return result(TransferStatus.DESTINATION_FULL);
        var source = resolveItem(level, route.source());
        if (source == null || source == cargo) return result(TransferStatus.SOURCE_UNAVAILABLE);
        return CourierTransferEngine.transferItems(source, cargo, maxItems, resource -> route.courierFilter().allows(resource.toStack().getItem()));
    }

    @VisibleForDebug
    public static TransferResult deliver(ServerLevel level, CourierRoute route, ResourceHandler<ItemResource> cargo, int maxItems) {
        var invalid = validate(route, CourierResourceType.ITEM, maxItems);
        if (invalid != null) return invalid;
        if (isCargoEmpty(cargo)) return result(TransferStatus.SOURCE_EMPTY);
        var destination = resolveItem(level, route.destination());
        if (destination == null || destination == cargo)
            return result(TransferStatus.DESTINATION_UNAVAILABLE);
        return CourierTransferEngine.transferItems(cargo, destination, maxItems);
    }

    @VisibleForDebug
    public static TransferResult pickupFluid(ServerLevel level, CourierRoute route, ResourceHandler<FluidResource> cargo, int maxFluid) {
        var invalid = validate(route, CourierResourceType.FLUID, maxFluid);
        if (invalid != null) return invalid;
        if (!isCargoEmpty(cargo)) return result(TransferStatus.DESTINATION_FULL);
        var source = resolveFluid(level, route.source());
        if (source == null || source == cargo) return result(TransferStatus.SOURCE_UNAVAILABLE);
        return CourierFluidTransferEngine.transferFluids(source, cargo, maxFluid, route.courierFilter());
    }

    @VisibleForDebug
    public static TransferResult deliverFluid(ServerLevel level, CourierRoute route, ResourceHandler<FluidResource> cargo, int maxFluid) {
        var invalid = validate(route, CourierResourceType.FLUID, maxFluid);
        if (invalid != null) return invalid;
        if (isCargoEmpty(cargo)) return result(TransferStatus.SOURCE_EMPTY);
        var destination = resolveFluid(level, route.destination());
        if (destination == null || destination == cargo)
            return result(TransferStatus.DESTINATION_UNAVAILABLE);
        return CourierFluidTransferEngine.transferFluids(cargo, destination, maxFluid);
    }

    @VisibleForDebug
    public static TransferResult pickupEnergy(
            ServerLevel level, CourierRoute route, EnergyHandler cargo, int maxEnergy) {
        var invalid = validate(route, CourierResourceType.ENERGY, maxEnergy);
        if (invalid != null) return invalid;
        if (cargo.getAmountAsInt() > 0)
            return result(TransferStatus.DESTINATION_FULL);
        var source = resolveEnergy(level, route.source());
        if (source == null || source == cargo)
            return result(TransferStatus.SOURCE_UNAVAILABLE);
        return CourierEnergyTransferEngine.transferEnergy(source, cargo, maxEnergy);
    }

    @VisibleForDebug
    public static TransferResult deliverEnergy(
            ServerLevel level, CourierRoute route, EnergyHandler cargo, int maxEnergy) {
        var invalid = validate(route, CourierResourceType.ENERGY, maxEnergy);
        if (invalid != null) return invalid;
        if (cargo.getAmountAsInt() <= 0)
            return result(TransferStatus.SOURCE_EMPTY);
        var destination = resolveEnergy(level, route.destination());
        if (destination == null || destination == cargo)
            return result(TransferStatus.DESTINATION_UNAVAILABLE);
        return CourierEnergyTransferEngine.transferEnergy(cargo, destination, maxEnergy);
    }

    private static EnergyHandler resolveEnergy(ServerLevel level, CourierEndpoint endpoint) {
        return level.hasChunkAt(endpoint.pos())
                ? level.getCapability(Capabilities.Energy.BLOCK, endpoint.pos(), endpoint.side())
                : null;
    }

    public static TransferResult pickup(ResourcefulBee bee, CourierRoute route, int limit) {
        if (!(bee.level() instanceof ServerLevel level) || !isWithinInteractionRange(bee, route.source().pos())) {
            return result(TransferStatus.SOURCE_UNAVAILABLE);
        }
        if (bee.hasCourierCargoOtherThan(route.resourceType())) {
            return result(TransferStatus.DESTINATION_FULL);
        }
        return switch (route.resourceType()) {
            case ITEM -> pickup(level, route, bee.getCourierCargo(), limit);
            case FLUID -> pickupFluid(level, route, bee.getCourierFluidCargo(), limit);
            case ENERGY -> {
                var ability = bee.getCourierAbility();
                if (ability == null) yield result(TransferStatus.UNSUPPORTED_RESOURCE);
                int remainingCapacity = Math.max(0,
                        ability.energyTransferLimit() - bee.getCourierEnergyCargo().getAmountAsInt());
                int permitted = Math.min(limit, remainingCapacity);
                if (permitted <= 0) yield result(TransferStatus.DESTINATION_FULL);
                yield pickupEnergy(level, route, bee.getCourierEnergyCargo(), permitted);
            }
        };
    }

    public static TransferResult deliver(ResourcefulBee bee, CourierRoute route, int limit) {
        if (!(bee.level() instanceof ServerLevel level) || !isWithinInteractionRange(bee, route.destination().pos())) {
            return result(TransferStatus.DESTINATION_UNAVAILABLE);
        }
        return switch (route.resourceType()) {
            case ITEM -> deliver(level, route, bee.getCourierCargo(), limit);
            case FLUID -> deliverFluid(level, route, bee.getCourierFluidCargo(), limit);
            case ENERGY -> deliverEnergy(level, route, bee.getCourierEnergyCargo(), limit);
        };
    }

    public static boolean isWithinInteractionRange(ResourcefulBee bee, BlockPos endpoint) {
        return bee.distanceToSqr(endpoint.getX() + .5D, endpoint.getY() + .5D, endpoint.getZ() + .5D) <= INTERACTION_DISTANCE_SQUARED;
    }

    public static boolean isEndpointAvailable(ServerLevel level, CourierEndpoint endpoint, CourierResourceType type) {
        if (!level.hasChunkAt(endpoint.pos())) {
            return false;
        }

        return switch (type) {
            case ITEM -> resolveItem(level, endpoint) != null;
            case FLUID -> resolveFluid(level, endpoint) != null;
            case ENERGY -> resolveEnergy(level, endpoint) != null;
        };
    }

    private static ResourceHandler<ItemResource> resolveItem(ServerLevel level, CourierEndpoint endpoint) {
        return level.hasChunkAt(endpoint.pos()) ? level.getCapability(Capabilities.Item.BLOCK, endpoint.pos(), endpoint.side()) : null;
    }

    private static ResourceHandler<FluidResource> resolveFluid(ServerLevel level, CourierEndpoint endpoint) {
        return level.hasChunkAt(endpoint.pos()) ? level.getCapability(Capabilities.Fluid.BLOCK, endpoint.pos(), endpoint.side()) : null;
    }

    private static boolean isCargoEmpty(ResourceHandler<?> cargo) {
        for (int i = 0; i < cargo.size(); i++) if (cargo.getAmountAsInt(i) > 0) return false;
        return true;
    }

    private static TransferResult validate(CourierRoute route, CourierResourceType type, int limit) {
        if (!route.enabled()) return result(TransferStatus.DISABLED);
        if (route.resourceType() != type || route.executionMode() != CourierRouteExecutionMode.COURIER)
            return result(TransferStatus.UNSUPPORTED_RESOURCE);
        if (limit <= 0) return result(TransferStatus.INVALID_LIMIT);
        return null;
    }

    private static TransferResult result(TransferStatus status) {
        return new TransferResult(status, 0);
    }
}
