package com.teamresourceful.resourcefulbees.common.logistics.courier;

public enum CourierState {
    IDLE,
    TRAVELING_TO_SOURCE,
    PICKING_UP,
    TRAVELING_TO_DESTINATION,
    DELIVERING,
    RETURNING,
    BLOCKED,
    WAITING_FOR_ROUTE_UPDATE
}
