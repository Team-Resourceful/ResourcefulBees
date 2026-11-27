package com.teamresourceful.resourcefulbees_test.entities;

import com.teamresourceful.resourcefulbees.common.blockentities.ColonyControllerBlockEntity;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.logistics.*;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierAssignment;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierState;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees_test.util.TestUtilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import java.util.UUID;

/**
 * Phase 3B.5B.8A: colony membership and courier registration lifecycle.
 */
public final class CourierColonyMembershipGameTests {
    private static final BlockPos FIRST = new BlockPos(2, 2, 2);
    private static final BlockPos SECOND = new BlockPos(18, 2, 2);
    private static final BlockPos SOURCE = new BlockPos(4, 2, 2);
    private static final BlockPos DEST = new BlockPos(12, 2, 2);
    private static final BlockPos START = new BlockPos(9, 3, 6);
    private static final ItemResource IRON = ItemResource.of(new ItemStack(Items.IRON_INGOT));

    private CourierColonyMembershipGameTests() {
    }

    private static ColonyControllerBlockEntity controller(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos, ModBlocks.COLONY_CONTROLLER.get());
        return helper.getBlockEntity(pos, ColonyControllerBlockEntity.class);
    }

    private static ResourcefulBee bee(GameTestHelper helper) {
        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);
        BlockPos start = helper.absolutePos(START);
        bee.setPos(start.getX() + 0.5D, start.getY() + 0.5D, start.getZ() + 0.5D);
        bee.setNoAi(true);
        return bee;
    }

    private static CourierRoute route(GameTestHelper helper) {
        return new CourierRoute(UUID.randomUUID(), CourierResourceType.ITEM, new CourierEndpoint(helper.absolutePos(SOURCE), Direction.UP), new CourierEndpoint(helper.absolutePos(DEST), Direction.UP), true, 10, CourierRouteExecutionMode.COURIER, CourierFilter.NONE);
    }

    private static void register(GameTestHelper helper, ColonyControllerBlockEntity controller, ResourcefulBee bee) {
        helper.assertTrue(controller.registerCourier(bee), "Courier registration failed");
        helper.assertTrue(controller.isCourierRegistered(bee.getUUID()), "Controller omitted registered courier");
        helper.assertTrue(bee.belongsToColony(controller.getColonyId()), "Bee did not join expected colony");
    }

    private static void assign(GameTestHelper helper, ColonyControllerBlockEntity controller, BlockPos pos, ResourcefulBee bee, CourierRoute route) {
        helper.assertTrue(controller.addRoute(route), "Could not add courier route");
        helper.assertTrue(bee.assignCourierRoute(new CourierAssignment(controller.getColonyId(), route.id()), helper.absolutePos(pos)), "Could not assign courier route");
    }

    private static void cargo(GameTestHelper helper, ResourcefulBee bee, int expected) {
        helper.assertTrue(bee.getCourierCargo().getAmountAsInt(0) == expected, "Courier cargo count changed; expected " + expected);
        if (expected > 0) {
            helper.assertTrue(IRON.equals(bee.getCourierCargo().getResource(0)), "Courier cargo item type changed");
        }
    }

    private static void unassigned(GameTestHelper helper, ResourcefulBee bee) {
        helper.assertTrue(!bee.getCourierDelivery().isAssigned(), "Courier retained an assignment");
        helper.assertTrue(bee.getCourierState() == CourierState.IDLE, "Courier did not return to IDLE");
    }

    /**
     * Unregistering is the atomic, two-sided way to leave a colony.
     */
    public static void courierLeaveColonyWhileIdle(GameTestHelper helper) {
        ColonyControllerBlockEntity colony = controller(helper, FIRST);
        ResourcefulBee courier = bee(helper);
        register(helper, colony, courier);
        helper.assertTrue(colony.unregisterCourier(courier), "Unregister failed");
        helper.assertTrue(!courier.isColonyMember(), "Bee retained colony membership");
        helper.assertTrue(courier.getColonyId() == null && courier.getColonyControllerPos() == null, "Bee retained colony identity or controller position");
        helper.assertTrue(!colony.isCourierRegistered(courier.getUUID()), "Controller retained registration");
        unassigned(helper, courier);
        CourierRoute candidate = route(helper);
        helper.assertTrue(colony.addRoute(candidate), "Could not add route after unregister");
        helper.assertTrue(!courier.assignCourierRoute(new CourierAssignment(colony.getColonyId(), candidate.id()), helper.absolutePos(FIRST)), "Unregistered bee accepted an assignment");
        helper.succeed();
    }

    public static void courierLeaveColonyWithCargo(GameTestHelper helper) {
        ColonyControllerBlockEntity colony = controller(helper, FIRST);
        ResourcefulBee courier = bee(helper);
        register(helper, colony, courier);
        CourierRoute candidate = route(helper);
        assign(helper, colony, FIRST, courier, candidate);
        ((ItemStacksResourceHandler) courier.getCourierCargo()).set(0, IRON, 8);
        cargo(helper, courier, 8);
        helper.assertTrue(colony.unregisterCourier(courier), "Unregister with cargo failed");
        helper.assertTrue(!courier.isColonyMember(), "Bee retained colony membership");
        helper.assertTrue(!colony.isCourierRegistered(courier.getUUID()), "Controller retained registration");
        unassigned(helper, courier);
        cargo(helper, courier, 8);
        helper.assertTrue(!courier.assignCourierRoute(new CourierAssignment(colony.getColonyId(), candidate.id()), helper.absolutePos(FIRST)), "Departed courier accepted old-colony assignment");
        cargo(helper, courier, 8);
        helper.succeed();
    }

    public static void courierTransferBetweenColonies(GameTestHelper helper) {
        ColonyControllerBlockEntity first = controller(helper, FIRST);
        ColonyControllerBlockEntity second = controller(helper, SECOND);
        helper.assertTrue(!first.getColonyId().equals(second.getColonyId()), "Distinct controllers unexpectedly have identical colony IDs");
        ResourcefulBee courier = bee(helper);
        register(helper, first, courier);
        CourierRoute oldRoute = route(helper);
        assign(helper, first, FIRST, courier, oldRoute);
        ((ItemStacksResourceHandler) courier.getCourierCargo()).set(0, IRON, 8);
        helper.assertTrue(!second.registerCourier(courier), "Second controller accepted a courier still owned by first colony");
        helper.assertTrue(first.unregisterCourier(courier), "Old colony unregister failed");
        unassigned(helper, courier);
        cargo(helper, courier, 8);
        register(helper, second, courier);
        helper.assertTrue(!first.isCourierRegistered(courier.getUUID()), "Old colony retained registration");
        helper.assertTrue(helper.absolutePos(SECOND).equals(courier.getColonyControllerPos()), "Courier did not adopt second controller position");
        helper.assertTrue(!courier.assignCourierRoute(new CourierAssignment(first.getColonyId(), oldRoute.id()), helper.absolutePos(FIRST)), "Courier accepted old-colony assignment after transfer");
        CourierRoute newRoute = route(helper);
        assign(helper, second, SECOND, courier, newRoute);
        helper.assertTrue(courier.getCourierDelivery().getAssignment().colonyId().equals(second.getColonyId()), "Assigned route belongs to wrong colony");
        cargo(helper, courier, 8);
        helper.succeed();
    }

    public static void courierRegistrationIdempotent(GameTestHelper helper) {
        ColonyControllerBlockEntity colony = controller(helper, FIRST);
        ResourcefulBee courier = bee(helper);
        for (int i = 0; i < 5; i++) {
            helper.assertTrue(colony.registerCourier(courier), "Repeat registration failed at " + i);
            helper.assertTrue(colony.getRegisteredCouriers().size() == 1, "Repeat registration duplicated courier UUID");
        }
        helper.assertTrue(colony.unregisterCourier(courier), "Unregister failed");
        helper.assertTrue(!colony.unregisterCourier(courier), "Duplicate unregister unexpectedly succeeded");
        helper.assertTrue(colony.getRegisteredCouriers().isEmpty(), "Registry not empty after unregister");
        helper.assertTrue(!courier.isColonyMember(), "Bee retained membership after unregister");
        helper.succeed();
    }

    /**
     * Stale UUIDs are currently retained; verify they do not prevent live courier assignments.
     */
    public static void courierStaleRegistrationRecovery(GameTestHelper helper) {
        ColonyControllerBlockEntity colony = controller(helper, FIRST);
        ResourcefulBee stale = bee(helper);
        register(helper, colony, stale);
        UUID staleId = stale.getUUID();
        stale.discard();
        helper.assertTrue(colony.isCourierRegistered(staleId), "Test precondition failed: discarded bee UUID should remain in registry");
        ResourcefulBee live = bee(helper);
        register(helper, colony, live);
        helper.assertTrue(colony.getRegisteredCouriers().size() == 2, "Expected one stale and one live registration");
        CourierRoute candidate = route(helper);
        assign(helper, colony, FIRST, live, candidate);
        helper.assertTrue(live.getCourierDelivery().isAssigned(), "Stale registry entry prevented live courier assignment");
        helper.assertTrue(colony.isCourierRegistered(live.getUUID()), "Live courier lost registration");
        helper.assertTrue(live.belongsToColony(colony.getColonyId()), "Live courier lost colony membership");
        helper.succeed();
    }
}
