package com.teamresourceful.resourcefulbees.common.entities.goals;

import com.teamresourceful.resourcefulbees.common.blockentities.ColonyControllerBlockEntity;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.logistics.CourierResourceType;
import com.teamresourceful.resourcefulbees.common.logistics.CourierRoute;
import com.teamresourceful.resourcefulbees.common.logistics.TransferStatus;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierDeliveryController;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierDeliveryExecutor;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public final class CourierDeliveryGoal extends Goal {

    private static final double SPEED = 1.2D;
    private static final int REPATH_INTERVAL = 20;
    private static final int TRANSFER_RETRY_INTERVAL = 20;

    private int transferCooldown;

    private final ResourcefulBee bee;
    private int repathCooldown;

    public CourierDeliveryGoal(ResourcefulBee bee) {
        this.bee = bee;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return bee.level() instanceof ServerLevel
                && !bee.isNoAi()
                && bee.getCourierAbility() != null
                && bee.getCourierDelivery().isAssigned()
                && bee.getCourierState() != CourierState.WAITING_FOR_ROUTE_UPDATE
                && bee.getAssignedCourierRoute() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void start() {
        repathCooldown = 0;
        transferCooldown = 0;
    }


    @Override
    public void tick() {
        if (!(bee.level() instanceof ServerLevel level)) {
            return;
        }

        CourierRoute route = bee.getAssignedCourierRoute();

        if (route == null) {
            bee.getNavigation().stop();
            return;
        }


        CourierDeliveryController delivery = bee.getCourierDelivery();

        ColonyControllerBlockEntity controller = getCourierController(level);

        if (controller != null && controller.hasPendingRouteUpdate(route.id()) && !bee.hasCourierCargo() && delivery.waitForRouteUpdate(false)) {
            bee.getNavigation().stop();

            controller.acknowledgeRouteUpdate(
                    route.id(),
                    bee.getUUID()
            );

            return;
        }

        if (!bee.hasCourierCargo() && endpointsLoaded(level, route) && (!CourierDeliveryExecutor.isEndpointAvailable(level, route.source(), route.resourceType()) || !CourierDeliveryExecutor.isEndpointAvailable(level, route.destination(), route.resourceType()))) {

            bee.clearCourierAssignment();
            bee.getNavigation().stop();
            return;
        }

        CourierState state = delivery.getState();

        switch (state) {
            case PICKING_UP -> {
                if (!CourierDeliveryExecutor.isWithinInteractionRange(bee, route.source().pos())) {
                    delivery.resumeTravelToEndpoint();
                    repathCooldown = 0;
                    return;
                }

                if (!isCargoEmpty(route)) {
                    delivery.resumeDeliveryWithCargo();
                    repathCooldown = 0;
                    return;
                }

                bee.getNavigation().stop();

                if (transferCooldown-- > 0) {
                    return;
                }

                transferCooldown = TRANSFER_RETRY_INTERVAL;

                var result = CourierDeliveryExecutor.pickup(bee, route, transferLimit(route.resourceType()));

                if (result.itemsMoved() > 0) {
                    delivery.pickupSucceeded();
                    repathCooldown = 0;
                } else if (result.status() == TransferStatus.SOURCE_EMPTY) {
                    bee.clearCourierAssignment();
                    bee.getNavigation().stop();
                }
            }


            case DELIVERING -> {
                if (!CourierDeliveryExecutor.isWithinInteractionRange(bee, route.destination().pos())) {
                    delivery.resumeTravelToEndpoint();
                    repathCooldown = 0;
                    return;
                }

                bee.getNavigation().stop();

                if (transferCooldown-- > 0) {
                    return;
                }

                transferCooldown = TRANSFER_RETRY_INTERVAL;

                // Cargo might already have been delivered by
                // another valid operation.
                if (isCargoEmpty(route)) {
                    delivery.deliverySucceeded();
                    repathCooldown = 0;
                    return;
                }

                var result = CourierDeliveryExecutor.deliver(bee, route, transferLimit(route.resourceType()));

                if (isCargoEmpty(route)) {
                    delivery.deliverySucceeded();
                    repathCooldown = 0;
                } else if (result.itemsMoved() == 0) {
                    // The destination cannot currently accept
                    // any cargo. Preserve it and wait to retry.
                    delivery.block();
                }

                // A partial transfer leaves the bee in DELIVERING,
                // so it can retry the remaining cargo.
            }

            case BLOCKED -> {
                bee.getNavigation().stop();

                if (transferCooldown-- > 0) {
                    return;
                }

                transferCooldown = TRANSFER_RETRY_INTERVAL;

                if (isCargoEmpty(route)) {
                    // This is a defensive fallback. A blocked
                    // courier normally still has its cargo.
                    delivery.retryBlockedDelivery();
                    delivery.deliverySucceeded();
                    repathCooldown = 0;
                    return;
                }

                if (!CourierDeliveryExecutor.isWithinInteractionRange(bee, route.destination().pos())) {
                    // Resume traveling if the bee has drifted
                    // away from the destination.
                    delivery.retryBlockedDelivery();
                    delivery.resumeTravelToEndpoint();
                    repathCooldown = 0;
                    return;
                }

                // Retry the delivery on the next goal tick.
                delivery.retryBlockedDelivery();
            }

            case TRAVELING_TO_SOURCE, TRAVELING_TO_DESTINATION, RETURNING -> {
                BlockPos target = switch (state) {
                    case TRAVELING_TO_SOURCE, RETURNING -> route.source().pos();
                    case TRAVELING_TO_DESTINATION -> route.destination().pos();
                    default -> throw new IllegalStateException();
                };

                if (!level.hasChunkAt(target)) {
                    bee.getNavigation().stop();
                    return;
                }

                double x = target.getX() + 0.5D;
                double y = target.getY() + 0.5D;
                double z = target.getZ() + 0.5D;

                if (CourierDeliveryExecutor.isWithinInteractionRange(bee, target)) {
                    bee.getNavigation().stop();

                    switch (state) {
                        case TRAVELING_TO_SOURCE -> delivery.arriveAtSource();
                        case TRAVELING_TO_DESTINATION -> delivery.arriveAtDestination();
                        case RETURNING -> bee.completeCourierReturn();
                        default -> {
                        }
                    }

                    transferCooldown = 0;
                    repathCooldown = 0;
                    return;
                }

                if (--repathCooldown <= 0) {
                    repathCooldown = REPATH_INTERVAL;
                    bee.getNavigation().moveTo(x, y, z, SPEED);
                }
            }

            default -> bee.getNavigation().stop();
        }
    }

    private static boolean endpointsLoaded(ServerLevel level, CourierRoute route) {
        return level.hasChunkAt(route.source().pos()) && level.hasChunkAt(route.destination().pos());
    }

    private int transferLimit(CourierResourceType type) {
        return switch (type) {
            case ITEM -> bee.getCourierAbility().itemTransferLimit();
            case FLUID -> bee.getCourierAbility().fluidTransferLimit();
            case ENERGY -> bee.getCourierAbility().energyTransferLimit();
        };
    }


    private boolean isCargoEmpty(CourierRoute route) {
        return !bee.hasCourierCargo(route.resourceType());
    }


    @Override
    public void stop() {
        bee.getNavigation().stop();
    }

    private boolean hasPendingRouteUpdate(ServerLevel level, CourierRoute route) {
        BlockPos controllerPos = bee.getColonyControllerPos();

        if (controllerPos == null || !level.hasChunkAt(controllerPos)) {
            return false;
        }

        if (!(level.getBlockEntity(controllerPos) instanceof ColonyControllerBlockEntity controller)) {
            return false;
        }

        CourierDeliveryController delivery = bee.getCourierDelivery();
        var assignment = delivery.getAssignment();

        return assignment != null && controller.getColonyId().equals(assignment.colonyId()) && controller.isCourierRegistered(bee.getUUID()) && controller.hasPendingRouteUpdate(route.id());
    }

    private ColonyControllerBlockEntity getCourierController(ServerLevel level) {
        BlockPos controllerPos = bee.getColonyControllerPos();

        if (controllerPos == null
                || !level.hasChunkAt(controllerPos)) {
            return null;
        }

        if (!(level.getBlockEntity(controllerPos)
                instanceof ColonyControllerBlockEntity controller)) {
            return null;
        }

        var assignment = bee.getCourierDelivery().getAssignment();

        if (assignment == null
                || !controller.getColonyId().equals(assignment.colonyId())
                || !controller.isCourierRegistered(bee.getUUID())) {
            return null;
        }

        return controller;
    }
}
