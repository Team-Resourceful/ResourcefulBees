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
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import java.util.UUID;

/**
 * Controller lifecycle regression tests. All coordinates are GameTest-relative.
 */
public final class CourierControllerLifecycleGameTests {
    private static final BlockPos CONTROLLER = new BlockPos(2, 2, 2);
    private static final BlockPos SOURCE = new BlockPos(4, 2, 2);
    private static final BlockPos DESTINATION = new BlockPos(12, 2, 2);
    private static final BlockPos BEE_START = new BlockPos(8, 3, 6);
    private static final ItemResource IRON = ItemResource.of(new ItemStack(Items.IRON_INGOT));

    private CourierControllerLifecycleGameTests() {
    }

    private record Fixture(ColonyControllerBlockEntity controller, ResourcefulBee bee, CourierRoute route) {
    }

    private static Fixture setup(GameTestHelper helper, boolean cargo) {
        helper.setBlock(CONTROLLER, ModBlocks.COLONY_CONTROLLER.get());
        helper.setBlock(SOURCE, Blocks.CHEST);
        helper.setBlock(DESTINATION, Blocks.CHEST);
        helper.getBlockEntity(SOURCE, net.minecraft.world.level.block.entity.ChestBlockEntity.class).clearContent();
        helper.getBlockEntity(DESTINATION, net.minecraft.world.level.block.entity.ChestBlockEntity.class).clearContent();

        ColonyControllerBlockEntity controller = helper.getBlockEntity(CONTROLLER, ColonyControllerBlockEntity.class);
        CourierRoute route = new CourierRoute(UUID.randomUUID(), CourierResourceType.ITEM, new CourierEndpoint(helper.absolutePos(SOURCE), Direction.UP), new CourierEndpoint(helper.absolutePos(DESTINATION), Direction.UP), true, 0, CourierRouteExecutionMode.COURIER, CourierFilter.NONE);
        helper.assertTrue(controller.addRoute(route), "Failed to add courier route");

        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);
        BlockPos start = helper.absolutePos(BEE_START);
        bee.setPos(start.getX() + 0.5, start.getY() + 0.5, start.getZ() + 0.5);
        bee.setNoAi(true); // Keep the test deterministic: no pickup or navigation.
        helper.assertTrue(controller.registerCourier(bee), "Failed to register courier");
        helper.assertTrue(bee.assignCourierRoute(new CourierAssignment(controller.getColonyId(), route.id()), helper.absolutePos(CONTROLLER)), "Failed to assign courier");
        if (cargo) {
            ((ItemStacksResourceHandler) bee.getCourierCargo()).set(0, IRON, 8);
            helper.assertTrue(bee.getCourierDelivery().resumeDeliveryWithCargo(), "Failed to resume delivery with cargo");
        }
        return new Fixture(controller, bee, route);
    }

    private static void assertCargo(GameTestHelper helper, ResourcefulBee bee, int expected) {
        helper.assertTrue(bee.getCourierCargo().getAmountAsInt(0) == expected, "Courier cargo count changed");
        if (expected > 0) {
            helper.assertTrue(IRON.equals(bee.getCourierCargo().getResource(0)), "Courier cargo item changed");
        }
    }

    private static void assertUnassigned(GameTestHelper helper, ResourcefulBee bee, int cargo) {
        helper.assertTrue(!bee.getCourierDelivery().isAssigned(), "Assignment remained after controller removal");
        helper.assertTrue(bee.getCourierState() == CourierState.IDLE, "Courier did not return to IDLE");
        assertCargo(helper, bee, cargo);
    }

    private static void removeController(GameTestHelper helper) {
        helper.setBlock(CONTROLLER, Blocks.AIR);
        helper.assertTrue(helper.getLevel().getBlockEntity(helper.absolutePos(CONTROLLER)) == null, "Controller block entity still exists");
    }

    private static void assertAfterTicks(GameTestHelper helper, ResourcefulBee bee, int cargo) {
        final int[] ticks = {0};
        helper.onEachTick(() -> {
            if (++ticks[0] < 65) return;
            helper.assertTrue(bee.isAlive(), "Courier unexpectedly died");
            assertUnassigned(helper, bee, cargo);
            helper.succeed();
        });
    }

    public static void controllerRemovedIdleCourier(GameTestHelper helper) {
        Fixture fixture = setup(helper, false);
        UUID colonyId = fixture.controller().getColonyId();
        removeController(helper);
        helper.assertTrue(fixture.bee().hasInvalidCourierAssignment(), "Missing controller not recognized as invalid");
        fixture.bee().reconcileCourierAssignment();
        assertUnassigned(helper, fixture.bee(), 0);
        // Assignment reconciliation does not currently remove colony membership.
        helper.assertTrue(fixture.bee().belongsToColony(colonyId), "Unexpected colony membership mutation");
        assertAfterTicks(helper, fixture.bee(), 0);
    }

    public static void controllerRemovedWithCargo(GameTestHelper helper) {
        Fixture fixture = setup(helper, true);
        removeController(helper);
        fixture.bee().reconcileCourierAssignment();
        assertUnassigned(helper, fixture.bee(), 8);
        assertAfterTicks(helper, fixture.bee(), 8);
    }

    public static void controllerReplacedDifferentColony(GameTestHelper helper) {
        Fixture fixture = setup(helper, true);
        UUID oldColonyId = fixture.controller().getColonyId();
        removeController(helper);
        helper.setBlock(CONTROLLER, ModBlocks.COLONY_CONTROLLER.get());
        ColonyControllerBlockEntity replacement = helper.getBlockEntity(CONTROLLER, ColonyControllerBlockEntity.class);
        helper.assertTrue(!oldColonyId.equals(replacement.getColonyId()), "Replacement controller unexpectedly reused colony UUID");
        helper.assertTrue(fixture.bee().hasInvalidCourierAssignment(), "Replacement colony was accepted as original colony");
        fixture.bee().reconcileCourierAssignment();
        assertUnassigned(helper, fixture.bee(), 8);
        helper.assertTrue(!fixture.bee().belongsToColony(replacement.getColonyId()), "Courier silently joined replacement colony");
        helper.assertTrue(!replacement.isCourierRegistered(fixture.bee().getUUID()), "Replacement controller silently registered courier");
        assertAfterTicks(helper, fixture.bee(), 8);
    }

    public static void controllerRemovedDuringDelivery(GameTestHelper helper) {
        Fixture fixture = setup(helper, true);
        // With cargo, an assigned courier should be in the destination-travel state.
        helper.assertTrue(fixture.bee().getCourierState() == CourierState.TRAVELING_TO_DESTINATION, "Cargo-bearing courier did not enter delivery travel");
        removeController(helper);
        fixture.bee().reconcileCourierAssignment();
        assertUnassigned(helper, fixture.bee(), 8);
        helper.assertTrue(helper.getBlockEntity(DESTINATION, net.minecraft.world.level.block.entity.ChestBlockEntity.class).isEmpty(), "Cargo was delivered after controller removal");
        assertAfterTicks(helper, fixture.bee(), 8);
    }

    public static void controllerRemovedReplacedThenReconciled(GameTestHelper helper) {
        Fixture fixture = setup(helper, true);
        UUID originalId = fixture.controller().getColonyId();
        removeController(helper);
        fixture.bee().reconcileCourierAssignment();
        assertUnassigned(helper, fixture.bee(), 8);
        helper.setBlock(CONTROLLER, ModBlocks.COLONY_CONTROLLER.get());
        ColonyControllerBlockEntity replacement = helper.getBlockEntity(CONTROLLER, ColonyControllerBlockEntity.class);
        helper.assertTrue(!originalId.equals(replacement.getColonyId()), "Replacement controller reused old colony identity");
        helper.assertTrue(!replacement.registerCourier(fixture.bee()), "Replacement colony incorrectly accepted bee still belonging to old colony");
        helper.assertTrue(!replacement.isCourierRegistered(fixture.bee().getUUID()), "Replacement colony registry changed unexpectedly");
        assertAfterTicks(helper, fixture.bee(), 8);
    }
}
