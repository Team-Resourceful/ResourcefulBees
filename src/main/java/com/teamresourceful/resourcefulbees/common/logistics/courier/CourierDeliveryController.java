
package com.teamresourceful.resourcefulbees.common.logistics.courier;

import org.jspecify.annotations.Nullable;

public final class CourierDeliveryController {

    private CourierState state = CourierState.IDLE;

    @Nullable
    private CourierAssignment assignment;

    public CourierState getState() {
        return state;
    }

    public @Nullable CourierAssignment getAssignment() {
        return assignment;
    }

    public boolean isAssigned() {
        return assignment != null;
    }

    public boolean assign(CourierAssignment assignment) {
        if (this.assignment != null) {
            return false;
        }

        this.assignment = assignment;
        this.state = CourierState.TRAVELING_TO_SOURCE;
        return true;
    }

    public void clearAssignment() {
        assignment = null;
        state = CourierState.IDLE;
    }

    public boolean arriveAtSource() {
        if (state != CourierState.TRAVELING_TO_SOURCE) {
            return false;
        }

        state = CourierState.PICKING_UP;
        return true;
    }

    public boolean pickupSucceeded() {
        if (state != CourierState.PICKING_UP) {
            return false;
        }

        state = CourierState.TRAVELING_TO_DESTINATION;
        return true;
    }

    public boolean arriveAtDestination() {
        if (state != CourierState.TRAVELING_TO_DESTINATION) {
            return false;
        }

        state = CourierState.DELIVERING;
        return true;
    }

    public boolean deliverySucceeded() {
        if (state != CourierState.DELIVERING) {
            return false;
        }

        state = CourierState.RETURNING;
        return true;
    }

    public boolean returnCompleted() {
        if (state != CourierState.RETURNING) {
            return false;
        }

        state = CourierState.TRAVELING_TO_SOURCE;
        return true;
    }

    public void block() {
        if (assignment != null) {
            state = CourierState.BLOCKED;
        }
    }

    public boolean retryBlockedDelivery() {
        if (assignment == null || state != CourierState.BLOCKED) {
            return false;
        }

        state = CourierState.DELIVERING;
        return true;
    }

    public void restore(
            CourierAssignment assignment,
            CourierState savedState,
            boolean hasCargo
    ) {
        this.assignment = assignment;

        if (assignment == null) {
            this.state = CourierState.IDLE;
            return;
        }

        if (hasCargo) {
            this.state = CourierState.TRAVELING_TO_DESTINATION;
        } else {
            this.state = switch (savedState) {
                case RETURNING -> CourierState.RETURNING;
                case PICKING_UP -> CourierState.PICKING_UP;
                case TRAVELING_TO_SOURCE -> CourierState.TRAVELING_TO_SOURCE;
                case WAITING_FOR_ROUTE_UPDATE -> CourierState.WAITING_FOR_ROUTE_UPDATE;
                default -> CourierState.TRAVELING_TO_SOURCE;
            };
        }
    }

    public boolean resumeTravelToEndpoint() {
        if (assignment == null) {
            return false;
        }

        switch (state) {
            case PICKING_UP -> {
                state = CourierState.TRAVELING_TO_SOURCE;
                return true;
            }
            case DELIVERING -> {
                state = CourierState.TRAVELING_TO_DESTINATION;
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    public boolean resumeDeliveryWithCargo() {
        if (assignment == null) {
            return false;
        }

        if (state != CourierState.PICKING_UP
                && state != CourierState.TRAVELING_TO_SOURCE
                && state != CourierState.RETURNING) {
            return false;
        }

        state = CourierState.TRAVELING_TO_DESTINATION;
        return true;
    }

    public boolean waitForRouteUpdate(boolean hasCargo) {
        if (hasCargo || assignment == null) {
            return false;
        }

        if (state == CourierState.WAITING_FOR_ROUTE_UPDATE) {
            return true;
        }

        switch (state) {
            case TRAVELING_TO_SOURCE,
                 PICKING_UP,
                 TRAVELING_TO_DESTINATION,
                 DELIVERING,
                 RETURNING,
                 BLOCKED -> {
                state = CourierState.WAITING_FOR_ROUTE_UPDATE;
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    public boolean resumeAfterRouteUpdate() {
        if (assignment == null
                || state != CourierState.WAITING_FOR_ROUTE_UPDATE) {
            return false;
        }

        state = CourierState.TRAVELING_TO_SOURCE;
        return true;
    }
}
