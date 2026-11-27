package com.teamresourceful.resourcefulbees_test.entities;

import com.teamresourceful.resourcefulbees.common.blockentities.ColonyControllerBlockEntity;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.logistics.*;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierAssignment;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees_test.util.TestUtilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Phase 3B.5B.7F: INSTANT scheduler fairness, priority and persistence semantics.
 */
public final class CourierSchedulerFairnessGameTests {
    private static final BlockPos CONTROLLER = new BlockPos(2, 2, 2);
    private static final BlockPos SOURCE_A = new BlockPos(4, 2, 2);
    private static final BlockPos DEST_A = new BlockPos(6, 2, 2);
    private static final BlockPos SOURCE_B = new BlockPos(4, 2, 5);
    private static final BlockPos DEST_B = new BlockPos(6, 2, 5);
    private static final BlockPos SOURCE_C = new BlockPos(4, 2, 8);
    private static final BlockPos DEST_C = new BlockPos(6, 2, 8);
    private static final BlockPos BEE_START = new BlockPos(8, 3, 6);
    private static final long START_TICK = 1_000_000_000L;

    private CourierSchedulerFairnessGameTests() {
    }

    private record Lane(ChestBlockEntity source, ChestBlockEntity destination, CourierRoute route) {
    }

    private static Lane lane(GameTestHelper helper, BlockPos sourcePos, BlockPos destPos, UUID id, int priority, CourierRouteExecutionMode mode) {
        helper.setBlock(sourcePos, Blocks.CHEST);
        helper.setBlock(destPos, Blocks.CHEST);
        ChestBlockEntity source = helper.getBlockEntity(sourcePos, ChestBlockEntity.class);
        ChestBlockEntity dest = helper.getBlockEntity(destPos, ChestBlockEntity.class);
        source.clearContent();
        dest.clearContent();
        source.setItem(0, new ItemStack(Items.IRON_INGOT, 16));
        CourierRoute route = new CourierRoute(id, CourierResourceType.ITEM, new CourierEndpoint(helper.absolutePos(sourcePos), Direction.UP), new CourierEndpoint(helper.absolutePos(destPos), Direction.UP), true, priority, mode, CourierFilter.NONE);
        return new Lane(source, dest, route);
    }

    private static int iron(ChestBlockEntity chest) {
        int total = 0;
        for (int i = 0; i < chest.getContainerSize(); i++) {
            ItemStack stack = chest.getItem(i);
            if (stack.is(Items.IRON_INGOT)) total += stack.getCount();
        }
        return total;
    }

    private static void assertLane(GameTestHelper helper, Lane lane, int expectedSource, int expectedDest) {
        helper.assertTrue(iron(lane.source()) == expectedSource && iron(lane.destination()) == expectedDest, "Unexpected lane inventory: source=" + iron(lane.source()) + ", destination=" + iron(lane.destination()) + ", expected=" + expectedSource + "/" + expectedDest);
    }

    private static CourierRouteScheduler scheduler() {
        CourierRouteScheduler scheduler = new CourierRouteScheduler();
        scheduler.setInterval(1);
        scheduler.setTransferLimit(1);
        return scheduler;
    }

    private static Map<UUID, CourierRoute> routes(Lane... lanes) {
        Map<UUID, CourierRoute> routes = new LinkedHashMap<>();
        for (Lane lane : lanes) routes.put(lane.route().id(), lane.route());
        return routes;
    }

    /**
     * Equal-priority routes alternate in UUID order regardless of map insertion order.
     */
    public static void equalPriorityRoundRobin(GameTestHelper helper) {
        Lane a = lane(helper, SOURCE_A, DEST_A, new UUID(0, 1), 5, CourierRouteExecutionMode.INSTANT);
        Lane b = lane(helper, SOURCE_B, DEST_B, new UUID(0, 2), 5, CourierRouteExecutionMode.INSTANT);
        CourierRouteScheduler scheduler = scheduler();
        Map<UUID, CourierRoute> routes = routes(b, a); // Deliberately reversed insertion order.
        for (int i = 0; i < 8; i++) {
            scheduler.tick(helper.getLevel(), routes, START_TICK + i);
            assertLane(helper, a, 16 - ((i + 2) / 2), (i + 2) / 2);
            assertLane(helper, b, 16 - ((i + 1) / 2), (i + 1) / 2);
        }
        helper.succeed();
    }

    /**
     * Higher-priority INSTANT routes always win while eligible.
     */
    public static void highestPriorityFirst(GameTestHelper helper) {
        Lane low = lane(helper, SOURCE_A, DEST_A, new UUID(0, 1), 1, CourierRouteExecutionMode.INSTANT);
        Lane high = lane(helper, SOURCE_B, DEST_B, new UUID(0, 2), 10, CourierRouteExecutionMode.INSTANT);
        CourierRouteScheduler scheduler = scheduler();
        Map<UUID, CourierRoute> routes = routes(low, high);
        for (int i = 0; i < 5; i++) scheduler.tick(helper.getLevel(), routes, START_TICK + i);
        assertLane(helper, high, 11, 5);
        assertLane(helper, low, 16, 0);
        helper.succeed();
    }

    /**
     * Documents strict priority: a lower-priority route runs only after the higher is disabled.
     */
    public static void lowerPriorityWaitsForEligibility(GameTestHelper helper) {
        Lane low = lane(helper, SOURCE_A, DEST_A, new UUID(0, 1), 1, CourierRouteExecutionMode.INSTANT);
        Lane high = lane(helper, SOURCE_B, DEST_B, new UUID(0, 2), 10, CourierRouteExecutionMode.INSTANT);
        CourierRouteScheduler scheduler = scheduler();
        Map<UUID, CourierRoute> routes = routes(low, high);
        for (int i = 0; i < 6; i++) scheduler.tick(helper.getLevel(), routes, START_TICK + i);
        assertLane(helper, low, 16, 0);
        assertLane(helper, high, 10, 6);
        routes.remove(high.route().id());
        for (int i = 6; i < 9; i++) scheduler.tick(helper.getLevel(), routes, START_TICK + i);
        assertLane(helper, low, 13, 3);
        assertLane(helper, high, 10, 6);
        helper.succeed();
    }

    /**
     * INSTANT transfers must not replace the assignment of an already busy COURIER bee.
     */
    public static void busyCourierNotReassigned(GameTestHelper helper) {
        helper.setBlock(CONTROLLER, ModBlocks.COLONY_CONTROLLER.get());
        ColonyControllerBlockEntity controller = helper.getBlockEntity(CONTROLLER, ColonyControllerBlockEntity.class);
        Lane courierLane = lane(helper, SOURCE_A, DEST_A, UUID.randomUUID(), 10, CourierRouteExecutionMode.COURIER);
        Lane instantLane = lane(helper, SOURCE_B, DEST_B, UUID.randomUUID(), 20, CourierRouteExecutionMode.INSTANT);
        helper.assertTrue(controller.addRoute(courierLane.route()), "Could not add courier route");
        helper.assertTrue(controller.addRoute(instantLane.route()), "Could not add instant route");
        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);
        BlockPos start = helper.absolutePos(BEE_START);
        bee.setPos(start.getX() + 0.5D, start.getY() + 0.5D, start.getZ() + 0.5D);
        bee.setNoAi(true);
        helper.assertTrue(controller.registerCourier(bee), "Could not register bee");
        helper.assertTrue(bee.assignCourierRoute(new CourierAssignment(controller.getColonyId(), courierLane.route().id()), helper.absolutePos(CONTROLLER)), "Could not assign busy courier");
        CourierRouteScheduler scheduler = scheduler();
        Map<UUID, CourierRoute> routes = routes(courierLane, instantLane);
        for (int i = 0; i < 4; i++) scheduler.tick(helper.getLevel(), routes, START_TICK + i);
        helper.assertTrue(bee.getCourierDelivery().isAssigned(), "Busy bee lost its assignment");
        helper.assertTrue(courierLane.route().id().equals(bee.getAssignedCourierRoute().id()), "Instant scheduler reassigned busy courier");
        assertLane(helper, instantLane, 12, 4);
        assertLane(helper, courierLane, 16, 0);
        helper.succeed();
    }

    /**
     * Controller NBT preserves scheduler settings; execution cursor is intentionally reset on load.
     */
    public static void schedulerSettingsSurviveReload(GameTestHelper helper) {
        helper.setBlock(CONTROLLER, ModBlocks.COLONY_CONTROLLER.get());
        ColonyControllerBlockEntity controller = helper.getBlockEntity(CONTROLLER, ColonyControllerBlockEntity.class);
        controller.setTransferInterval(7);
        controller.setTransferLimit(3);
        Lane lane = lane(helper, SOURCE_A, DEST_A, UUID.randomUUID(), 4, CourierRouteExecutionMode.INSTANT);
        helper.assertTrue(controller.addRoute(lane.route()), "Could not add persistent scheduler route");
        CompoundTag saved = controller.saveWithFullMetadata(helper.getLevel().registryAccess());
        BlockEntity loaded = BlockEntity.loadStatic(controller.getBlockPos(), controller.getBlockState(), saved, helper.getLevel().registryAccess());
        if (!(loaded instanceof ColonyControllerBlockEntity restored)) {
            helper.fail("Controller NBT did not restore a colony controller");
            return;
        }
        helper.assertTrue(restored.getTransferInterval() == 7, "Transfer interval was not persisted");
        helper.assertTrue(restored.getTransferLimit() == 3, "Transfer limit was not persisted");
        helper.assertTrue(restored.getColonyId().equals(controller.getColonyId()), "Colony ID changed");
        helper.assertTrue(restored.getRoutes().values().stream().anyMatch(route -> route.id().equals(lane.route().id())), "Scheduler route was not persisted");
        helper.succeed();
    }
}
