package com.teamresourceful.resourcefulbees.common.menus.content;

import com.teamresourceful.resourcefulbees.common.blockentities.ColonyControllerBlockEntity;
import net.minecraft.world.inventory.ContainerData;

public class ColonyControllerData implements ContainerData {

    public static final int RADIUS = 0;
    public static final int MAX_ROUTES = 1;
    public static final int MAX_BEES = 2;
    public static final int REGISTERED_COURIERS = 3;
    public static final int ROUTE_COUNT = 4;
    public static final int STORED_BEES = 5;

    public static final int SIZE = 6;

    private final ColonyControllerBlockEntity controller;

    public ColonyControllerData(ColonyControllerBlockEntity controller) {
        this.controller = controller;
    }

    @Override
    public int get(int index) {
        return switch (index) {
            case RADIUS -> controller.getOperatingRadius();
            case MAX_ROUTES -> controller.getMaxRoutes();
            case MAX_BEES -> controller.getMaxBees();
            case REGISTERED_COURIERS -> controller.getRegisteredCourierCount();
            case ROUTE_COUNT -> controller.getRouteCount();
            case STORED_BEES -> controller.beeCount();
            default -> 0;
        };
    }

    @Override
    public void set(int index, int value) {
        // Server-authoritative values; no direct mutation.
    }

    @Override
    public int getCount() {
        return SIZE;
    }
}