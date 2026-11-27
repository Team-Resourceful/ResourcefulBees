package com.teamresourceful.resourcefulbees_test.logistics;

import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierAssignment;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierDeliveryController;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierState;
import net.minecraft.gametest.framework.GameTestHelper;

import java.util.UUID;

public final class CourierDeliveryGameTests {

    private CourierDeliveryGameTests() {
    }

    private static CourierAssignment assignment() {
        return new CourierAssignment(UUID.randomUUID(), UUID.randomUUID());
    }

    public static void courierAssignment(GameTestHelper helper) {
        CourierDeliveryController controller = new CourierDeliveryController();

        CourierAssignment assignment = assignment();

        if (!controller.assign(assignment)) {
            helper.fail("Initial assignment was rejected");
            return;
        }

        if (!controller.isAssigned() || !assignment.equals(controller.getAssignment()) || controller.getState() != CourierState.TRAVELING_TO_SOURCE) {
            helper.fail("Assignment did not initialize courier state");
            return;
        }

        if (controller.assign(assignment())) {
            helper.fail("Courier accepted a second assignment");
            return;
        }

        helper.succeed();
    }

    public static void courierLifecycle(GameTestHelper helper) {
        CourierDeliveryController controller = new CourierDeliveryController();

        controller.assign(assignment());

        if (!controller.arriveAtSource() || controller.getState() != CourierState.PICKING_UP) {
            helper.fail("Failed to enter PICKING_UP");
            return;
        }

        if (!controller.pickupSucceeded() || controller.getState() != CourierState.TRAVELING_TO_DESTINATION) {
            helper.fail("Failed to enter TRAVELING_TO_DESTINATION");
            return;
        }

        if (!controller.arriveAtDestination() || controller.getState() != CourierState.DELIVERING) {
            helper.fail("Failed to enter DELIVERING");
            return;
        }

        if (!controller.deliverySucceeded() || controller.getState() != CourierState.RETURNING) {
            helper.fail("Failed to enter RETURNING");
            return;
        }

        if (!controller.returnCompleted() || controller.getState() != CourierState.TRAVELING_TO_SOURCE) {
            helper.fail("Courier did not begin another cycle");
            return;
        }

        helper.succeed();
    }

    public static void courierRejectsInvalidTransitions(GameTestHelper helper) {
        CourierDeliveryController controller = new CourierDeliveryController();

        if (controller.arriveAtSource() || controller.pickupSucceeded() || controller.arriveAtDestination() || controller.deliverySucceeded() || controller.returnCompleted()) {
            helper.fail("Unassigned courier accepted a transition");
            return;
        }

        controller.assign(assignment());

        if (controller.pickupSucceeded() || controller.arriveAtDestination() || controller.deliverySucceeded() || controller.returnCompleted()) {
            helper.fail("Courier skipped a required state");
            return;
        }

        if (controller.getState() != CourierState.TRAVELING_TO_SOURCE) {
            helper.fail("Invalid transition modified courier state");
            return;
        }

        helper.succeed();
    }

    public static void courierClearsAssignment(GameTestHelper helper) {
        CourierDeliveryController controller = new CourierDeliveryController();

        controller.assign(assignment());
        controller.arriveAtSource();
        controller.block();

        controller.clearAssignment();

        if (controller.isAssigned() || controller.getAssignment() != null || controller.getState() != CourierState.IDLE) {
            helper.fail("Clearing assignment did not reset courier");
            return;
        }

        if (!controller.assign(assignment()) || controller.getState() != CourierState.TRAVELING_TO_SOURCE) {
            helper.fail("Courier could not be reassigned");
            return;
        }

        helper.succeed();
    }


    public static void courierReloadWithCargo(GameTestHelper helper) {
        CourierDeliveryController controller = new CourierDeliveryController();

        CourierAssignment assignment = new CourierAssignment(UUID.randomUUID(), UUID.randomUUID());

        controller.restore(assignment, CourierState.PICKING_UP, true);

        if (!controller.isAssigned() || controller.getState() != CourierState.TRAVELING_TO_DESTINATION) {
            helper.fail("Courier carrying cargo did not resume destination travel");
            return;
        }

        helper.succeed();
    }

    public static void courierReloadWithoutCargo(GameTestHelper helper) {
        CourierDeliveryController controller = new CourierDeliveryController();

        CourierAssignment assignment = new CourierAssignment(UUID.randomUUID(), UUID.randomUUID());

        controller.restore(assignment, CourierState.TRAVELING_TO_DESTINATION, false);

        if (!controller.isAssigned() || controller.getState() != CourierState.TRAVELING_TO_SOURCE) {
            helper.fail("Empty courier did not recover to source travel");
            return;
        }

        helper.succeed();
    }

}
