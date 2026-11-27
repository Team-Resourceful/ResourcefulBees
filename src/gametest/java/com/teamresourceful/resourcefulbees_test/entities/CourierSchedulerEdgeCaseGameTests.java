package com.teamresourceful.resourcefulbees_test.entities;

import com.teamresourceful.resourcefulbees.common.logistics.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Phase 3B.5B.7G: deterministic INSTANT scheduler boundary and mutation tests.
 */
public final class CourierSchedulerEdgeCaseGameTests {
    private static final BlockPos SOURCE_A = new BlockPos(4, 2, 2);
    private static final BlockPos DEST_A = new BlockPos(6, 2, 2);
    private static final BlockPos SOURCE_B = new BlockPos(4, 2, 5);
    private static final BlockPos DEST_B = new BlockPos(6, 2, 5);
    private static final BlockPos SOURCE_C = new BlockPos(4, 2, 8);
    private static final BlockPos DEST_C = new BlockPos(6, 2, 8);
    private static final long START = 1_000_000_000L;

    private CourierSchedulerEdgeCaseGameTests() {
    }

    private record Lane(ChestBlockEntity source, ChestBlockEntity destination, CourierRoute route) {
    }

    private static Lane lane(GameTestHelper helper, BlockPos sourcePos, BlockPos destinationPos, UUID id, int priority) {
        helper.setBlock(sourcePos, Blocks.CHEST);
        helper.setBlock(destinationPos, Blocks.CHEST);
        ChestBlockEntity source = helper.getBlockEntity(sourcePos, ChestBlockEntity.class);
        ChestBlockEntity destination = helper.getBlockEntity(destinationPos, ChestBlockEntity.class);
        source.clearContent();
        destination.clearContent();
        source.setItem(0, new ItemStack(Items.IRON_INGOT, 32));
        CourierRoute route = new CourierRoute(id, CourierResourceType.ITEM, new CourierEndpoint(helper.absolutePos(sourcePos), Direction.UP), new CourierEndpoint(helper.absolutePos(destinationPos), Direction.UP), true, priority, CourierRouteExecutionMode.INSTANT, CourierFilter.NONE);
        return new Lane(source, destination, route);
    }

    private static CourierRoute withPriority(CourierRoute route, int priority) {
        return new CourierRoute(route.id(), route.resourceType(), route.source(), route.destination(), route.enabled(), priority, route.executionMode(), route.courierFilter());
    }

    private static Map<UUID, CourierRoute> routes(Lane... lanes) {
        Map<UUID, CourierRoute> result = new LinkedHashMap<>();
        for (Lane lane : lanes) result.put(lane.route().id(), lane.route());
        return result;
    }

    private static int iron(ChestBlockEntity chest) {
        int count = 0;
        for (int i = 0; i < chest.getContainerSize(); i++) {
            ItemStack stack = chest.getItem(i);
            if (stack.is(Items.IRON_INGOT)) count += stack.getCount();
        }
        return count;
    }

    private static void assertTransferred(GameTestHelper helper, Lane lane, int expected) {
        int source = iron(lane.source());
        int destination = iron(lane.destination());
        helper.assertTrue(source == 32 - expected && destination == expected, "Unexpected transfer for route " + lane.route().id() + ": source=" + source + ", destination=" + destination + ", expected transferred=" + expected);
    }

    private static void tick(GameTestHelper helper, CourierRouteScheduler scheduler, Map<UUID, CourierRoute> routes, long time) {
        scheduler.tick(helper.getLevel(), routes, time);
    }

    /**
     * Only ticks START, START+5, START+10 execute when interval=5.
     */
    public static void intervalBoundaries(GameTestHelper helper) {
        Lane a = lane(helper, SOURCE_A, DEST_A, new UUID(0, 1), 1);
        CourierRouteScheduler scheduler = new CourierRouteScheduler();
        scheduler.setInterval(5);
        scheduler.setTransferLimit(1);
        Map<UUID, CourierRoute> routes = routes(a);
        tick(helper, scheduler, routes, START);
        assertTransferred(helper, a, 1);
        tick(helper, scheduler, routes, START + 1);
        tick(helper, scheduler, routes, START + 4);
        assertTransferred(helper, a, 1);
        tick(helper, scheduler, routes, START + 5);
        assertTransferred(helper, a, 2);
        tick(helper, scheduler, routes, START + 9);
        assertTransferred(helper, a, 2);
        tick(helper, scheduler, routes, START + 10);
        assertTransferred(helper, a, 3);
        helper.succeed();
    }

    /**
     * Per-execution transfer cap is honored, including a final smaller remainder.
     */
    public static void transferLimitEnforced(GameTestHelper helper) {
        Lane a = lane(helper, SOURCE_A, DEST_A, new UUID(0, 1), 1);
        a.source().setItem(0, new ItemStack(Items.IRON_INGOT, 10));
        CourierRouteScheduler scheduler = new CourierRouteScheduler();
        scheduler.setInterval(1);
        scheduler.setTransferLimit(3);
        Map<UUID, CourierRoute> routes = routes(a);
        for (int i = 0; i < 4; i++) {
            tick(helper, scheduler, routes, START + i);
            int expected = Math.min(10, (i + 1) * 3);
            helper.assertTrue(iron(a.destination()) == expected && iron(a.source()) == 10 - expected, "Transfer limit violated at execution " + (i + 1) + ": source=" + iron(a.source()) + ", destination=" + iron(a.destination()));
        }
        helper.succeed();
    }

    /**
     * Removing the last-executed route continues at the next higher UUID.
     */
    public static void routeRemovalRoundRobin(GameTestHelper helper) {
        Lane a = lane(helper, SOURCE_A, DEST_A, new UUID(0, 1), 5);
        Lane b = lane(helper, SOURCE_B, DEST_B, new UUID(0, 2), 5);
        Lane c = lane(helper, SOURCE_C, DEST_C, new UUID(0, 3), 5);
        CourierRouteScheduler scheduler = new CourierRouteScheduler();
        scheduler.setInterval(1);
        scheduler.setTransferLimit(1);
        Map<UUID, CourierRoute> routes = routes(c, a, b);
        tick(helper, scheduler, routes, START);     // A
        tick(helper, scheduler, routes, START + 1); // B
        assertTransferred(helper, a, 1);
        assertTransferred(helper, b, 1);
        assertTransferred(helper, c, 0);
        routes.remove(b.route().id());
        tick(helper, scheduler, routes, START + 2); // C, after removed B
        assertTransferred(helper, c, 1);
        tick(helper, scheduler, routes, START + 3); // A, wrap
        assertTransferred(helper, a, 2);
        assertTransferred(helper, b, 1);
        assertTransferred(helper, c, 1);
        helper.succeed();
    }

    /**
     * Changing priority is observed at the next eligible tick, not mid-interval.
     */
    public static void dynamicPriorityChanges(GameTestHelper helper) {
        Lane a = lane(helper, SOURCE_A, DEST_A, new UUID(0, 1), 10);
        Lane b = lane(helper, SOURCE_B, DEST_B, new UUID(0, 2), 1);
        CourierRouteScheduler scheduler = new CourierRouteScheduler();
        scheduler.setInterval(3);
        scheduler.setTransferLimit(1);
        Map<UUID, CourierRoute> routes = routes(a, b);
        tick(helper, scheduler, routes, START);
        assertTransferred(helper, a, 1);
        assertTransferred(helper, b, 0);
        routes.put(b.route().id(), withPriority(b.route(), 20));
        tick(helper, scheduler, routes, START + 1);
        tick(helper, scheduler, routes, START + 2);
        assertTransferred(helper, a, 1);
        assertTransferred(helper, b, 0);
        tick(helper, scheduler, routes, START + 3);
        assertTransferred(helper, a, 1);
        assertTransferred(helper, b, 1);
        routes.put(a.route().id(), withPriority(a.route(), 30));
        tick(helper, scheduler, routes, START + 6);
        assertTransferred(helper, a, 2);
        assertTransferred(helper, b, 1);
        helper.succeed();
    }

    /**
     * reset() removes both cooldown and round-robin cursor.
     */
    public static void schedulerReset(GameTestHelper helper) {
        Lane a = lane(helper, SOURCE_A, DEST_A, new UUID(0, 1), 5);
        Lane b = lane(helper, SOURCE_B, DEST_B, new UUID(0, 2), 5);
        CourierRouteScheduler scheduler = new CourierRouteScheduler();
        scheduler.setInterval(10);
        scheduler.setTransferLimit(1);
        Map<UUID, CourierRoute> routes = routes(b, a);
        tick(helper, scheduler, routes, START);     // A
        assertTransferred(helper, a, 1);
        tick(helper, scheduler, routes, START + 1); // cooldown
        assertTransferred(helper, b, 0);
        scheduler.reset();
        tick(helper, scheduler, routes, START + 1); // immediate, cursor reset -> A
        assertTransferred(helper, a, 2);
        assertTransferred(helper, b, 0);
        tick(helper, scheduler, routes, START + 10); // still in cooldown
        assertTransferred(helper, a, 2);
        tick(helper, scheduler, routes, START + 11); // B next
        assertTransferred(helper, b, 1);
        helper.succeed();
    }
}
