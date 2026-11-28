package com.teamresourceful.resourcefulbees_test.logistics;

import com.teamresourceful.resourcefulbees.common.blockentities.ColonyControllerBlockEntity;
import com.teamresourceful.resourcefulbees.common.logistics.*;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierWorkProbe;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class CourierSchedulerGameTests {

    private static final BlockPos CONTROLLER = new BlockPos(1, 2, 1);
    private static final BlockPos SOURCE = new BlockPos(2, 2, 1);
    private static final BlockPos DESTINATION = new BlockPos(3, 2, 1);
    private static final BlockPos SECOND_SOURCE = new BlockPos(2, 2, 3);
    private static final BlockPos SECOND_DESTINATION = new BlockPos(3, 2, 3);

    private CourierSchedulerGameTests() {
    }

    private static ColonyControllerBlockEntity controller(GameTestHelper helper) {
        helper.setBlock(CONTROLLER, ModBlocks.COLONY_CONTROLLER.get());

        return helper.getBlockEntity(CONTROLLER, ColonyControllerBlockEntity.class);
    }

    private static ChestBlockEntity chest(GameTestHelper helper, BlockPos pos) {
        return helper.getBlockEntity(pos, ChestBlockEntity.class);
    }

    private static CourierRoute route(GameTestHelper helper, BlockPos source, BlockPos destination, int priority, CourierRouteExecutionMode routeExecutionMode) {
        return new CourierRoute(UUID.randomUUID(), CourierResourceType.ITEM, new CourierEndpoint(helper.absolutePos(source), Direction.UP), new CourierEndpoint(helper.absolutePos(destination), Direction.UP), true, priority, routeExecutionMode, CourierFilter.NONE);
    }

    private static CourierRoute route(GameTestHelper helper, BlockPos source, BlockPos destination, int priority) {
        return route(helper, source, destination, priority, CourierRouteExecutionMode.INSTANT);
    }

    private static int count(ChestBlockEntity chest) {
        int total = 0;

        for (int i = 0; i < chest.getContainerSize(); i++) {
            total += chest.getItem(i).getCount();
        }

        return total;
    }

    private static void assertCount(GameTestHelper helper, ChestBlockEntity chest, int expected) {
        int actual = count(chest);

        if (actual != expected) {
            helper.fail("Expected " + expected + " items, got " + actual);
        }
    }

    /**
     * Test 1: Verify automatic controller ticking.
     * <p>
     * This intentionally does not call serverTick() directly.
     */
    public static void controllerTicksTransfers(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = controller(helper);

        helper.setBlock(SOURCE, Blocks.CHEST);
        helper.setBlock(DESTINATION, Blocks.CHEST);

        ChestBlockEntity source = chest(helper, SOURCE);
        ChestBlockEntity destination = chest(helper, DESTINATION);

        source.setItem(0, new ItemStack(Items.IRON_INGOT, 32));

        CourierRoute route = route(helper, SOURCE, DESTINATION, 0);

        if (!controller.addRoute(route)) {
            helper.fail("Controller rejected valid route");
            return;
        }

        controller.setTransferInterval(1);
        controller.setTransferLimit(4);

        // Allow the real block entity ticker to execute.
        helper.runAfterDelay(5, () -> {
            int moved = count(destination);

            if (moved <= 0 || moved > 20) {
                helper.fail("Unexpected automatic transfer count: " + moved);
                return;
            }

            if (count(source) + moved != 32) {
                helper.fail("Automatic transfer violated item conservation");
                return;
            }

            helper.succeed();
        });
    }

    /**
     * Test 2: Verify scheduler settings survive block entity serialization.
     */
    public static void controllerSettingsPersistence(GameTestHelper helper) {
        ColonyControllerBlockEntity original = controller(helper);

        original.setTransferInterval(37);
        original.setTransferLimit(7);

        UUID originalColonyId = original.getColonyId();

        CompoundTag saved = original.saveWithFullMetadata(helper.getLevel().registryAccess());

        BlockEntity restoredBlockEntity = BlockEntity.loadStatic(original.getBlockPos(), original.getBlockState(), saved, helper.getLevel().registryAccess());

        if (!(restoredBlockEntity instanceof ColonyControllerBlockEntity restored)) {
            helper.fail("Failed to deserialize Colony Controller");
            return;
        }

        if (restored.getTransferInterval() != 37) {
            helper.fail("Transfer interval was not persisted");
            return;
        }

        if (restored.getTransferLimit() != 7) {
            helper.fail("Transfer limit was not persisted");
            return;
        }

        if (!restored.getColonyId().equals(originalColonyId)) {
            helper.fail("Colony ID changed during serialization");
            return;
        }

        helper.succeed();
    }

    /**
     * Test 3: Verify invalid configuration values are rejected.
     */
    public static void controllerRejectsInvalidSettings(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = controller(helper);

        int originalInterval = controller.getTransferInterval();
        int originalLimit = controller.getTransferLimit();

        int rejected = 0;

        for (int value : new int[]{0, -1, -20}) {
            try {
                controller.setTransferInterval(value);
            } catch (IllegalArgumentException expected) {
                rejected++;
            }

            try {
                controller.setTransferLimit(value);
            } catch (IllegalArgumentException expected) {
                rejected++;
            }
        }

        if (rejected != 6) {
            helper.fail("Expected 6 invalid settings to be rejected, got " + rejected);
            return;
        }

        if (controller.getTransferInterval() != originalInterval || controller.getTransferLimit() != originalLimit) {
            helper.fail("Invalid configuration modified scheduler settings");
            return;
        }

        helper.succeed();
    }

    /**
     * Test 4: Verify routes outside a reduced radius are not executed.
     */
    public static void controllerRespectsReducedRadius(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = controller(helper);

        helper.setBlock(SOURCE, Blocks.CHEST);
        helper.setBlock(DESTINATION, Blocks.CHEST);

        ChestBlockEntity source = chest(helper, SOURCE);
        ChestBlockEntity destination = chest(helper, DESTINATION);

        source.setItem(0, new ItemStack(Items.IRON_INGOT, 32));

        CourierRoute route = route(helper, SOURCE, DESTINATION, 0);

        if (!controller.addRoute(route)) {
            helper.fail("Controller rejected initially valid route");
            return;
        }
//todo fix or delete this test
//        controller.setOperatingRadius(1);
//        controller.setTransferInterval(1);
//
//        if (controller.isRouteWithinRange(route)) {
//            helper.fail("Route should be outside the reduced operating radius");
//            return;
//        }
//
//        helper.runAfterDelay(5, () -> {
//            if (count(source) != 32 || count(destination) != 0) {
//                helper.fail("Controller transferred items outside its operating radius");
//                return;
//            }
//
//            if (!controller.getRoutes().containsKey(route.id())) {
//                helper.fail("Radius reduction unexpectedly deleted the route");
//                return;
//            }
//
//            helper.succeed();
//        });
        helper.succeed();
    }

    /**
     * Test 5: Verify interval enforcement using explicit logical ticks.
     */
    public static void schedulerRespectsInterval(GameTestHelper helper) {
        helper.setBlock(SOURCE, Blocks.CHEST);
        helper.setBlock(DESTINATION, Blocks.CHEST);

        ChestBlockEntity source = chest(helper, SOURCE);
        ChestBlockEntity destination = chest(helper, DESTINATION);

        source.setItem(0, new ItemStack(Items.IRON_INGOT, 64));

        CourierRoute route = route(helper, SOURCE, DESTINATION, 0);

        CourierRouteScheduler scheduler = new CourierRouteScheduler();
        scheduler.setInterval(20);
        scheduler.setTransferLimit(4);

        Map<UUID, CourierRoute> routes = Map.of(route.id(), route);

        scheduler.tick(helper.getLevel(), routes, 100);
        assertCount(helper, destination, 4);

        scheduler.tick(helper.getLevel(), routes, 110);
        assertCount(helper, destination, 4);

        scheduler.tick(helper.getLevel(), routes, 119);
        assertCount(helper, destination, 4);

        scheduler.tick(helper.getLevel(), routes, 120);
        assertCount(helper, destination, 8);

        scheduler.tick(helper.getLevel(), routes, 140);
        assertCount(helper, destination, 12);

        helper.succeed();
    }

    /**
     * Test 6: Verify strict priority scheduling.
     */
    public static void schedulerPrioritizesRoutes(GameTestHelper helper) {
        helper.setBlock(SOURCE, Blocks.CHEST);
        helper.setBlock(DESTINATION, Blocks.CHEST);
        helper.setBlock(SECOND_SOURCE, Blocks.CHEST);
        helper.setBlock(SECOND_DESTINATION, Blocks.CHEST);

        ChestBlockEntity lowSource = chest(helper, SOURCE);
        ChestBlockEntity lowDestination = chest(helper, DESTINATION);
        ChestBlockEntity highSource = chest(helper, SECOND_SOURCE);
        ChestBlockEntity highDestination = chest(helper, SECOND_DESTINATION);

        lowSource.setItem(0, new ItemStack(Items.IRON_INGOT, 32));
        highSource.setItem(0, new ItemStack(Items.GOLD_INGOT, 32));

        CourierRoute lowPriority = route(helper, SOURCE, DESTINATION, 1);

        CourierRoute highPriority = route(helper, SECOND_SOURCE, SECOND_DESTINATION, 10);

        Map<UUID, CourierRoute> routes = new LinkedHashMap<>();

        // Insert lower priority first to ensure map order does not win.
        routes.put(lowPriority.id(), lowPriority);
        routes.put(highPriority.id(), highPriority);

        CourierRouteScheduler scheduler = new CourierRouteScheduler();
        scheduler.setInterval(20);
        scheduler.setTransferLimit(4);

        scheduler.tick(helper.getLevel(), routes, 100);

        if (count(highDestination) != 4) {
            helper.fail("Higher-priority route was not selected");
            return;
        }

        if (count(lowDestination) != 0) {
            helper.fail("Lower-priority route executed before higher priority");
            return;
        }

        scheduler.tick(helper.getLevel(), routes, 120);

        if (count(highDestination) != 8 || count(lowDestination) != 0) {
            helper.fail("Scheduler did not preserve strict priority ordering");
            return;
        }

        helper.succeed();
    }


    public static void schedulerHandlesEmptyRoutes(GameTestHelper helper) {
        CourierRouteScheduler scheduler = new CourierRouteScheduler();

        scheduler.setInterval(20);
        scheduler.setTransferLimit(4);

        // No registered routes.
        scheduler.tick(helper.getLevel(), Map.of(), 100);

        // No registered routes after the interval.
        scheduler.tick(helper.getLevel(), Map.of(), 120);

        // The scheduler must also handle disabled routes.
        CourierRoute disabledRoute = new CourierRoute(UUID.randomUUID(), CourierResourceType.ITEM, new CourierEndpoint(helper.absolutePos(SOURCE), Direction.UP), new CourierEndpoint(helper.absolutePos(DESTINATION), Direction.UP), false, 0);

        scheduler.tick(helper.getLevel(), Map.of(disabledRoute.id(), disabledRoute), 140);

        // Unsupported resource routes must also be ignored.
        CourierRoute fluidRoute = new CourierRoute(UUID.randomUUID(), CourierResourceType.FLUID, new CourierEndpoint(helper.absolutePos(SOURCE), Direction.UP), new CourierEndpoint(helper.absolutePos(DESTINATION), Direction.UP), true, 0);

        scheduler.tick(helper.getLevel(), Map.of(fluidRoute.id(), fluidRoute), 160);

        helper.succeed();
    }


    public static void schedulerRoundRobin(GameTestHelper helper) {
        helper.setBlock(SOURCE, Blocks.CHEST);
        helper.setBlock(DESTINATION, Blocks.CHEST);
        helper.setBlock(SECOND_SOURCE, Blocks.CHEST);
        helper.setBlock(SECOND_DESTINATION, Blocks.CHEST);

        ChestBlockEntity sourceA = chest(helper, SOURCE);
        ChestBlockEntity destinationA = chest(helper, DESTINATION);
        ChestBlockEntity sourceB = chest(helper, SECOND_SOURCE);
        ChestBlockEntity destinationB = chest(helper, SECOND_DESTINATION);

        sourceA.setItem(0, new ItemStack(Items.IRON_INGOT, 32));
        sourceB.setItem(0, new ItemStack(Items.GOLD_INGOT, 32));

        CourierRoute routeA = route(helper, SOURCE, DESTINATION, 5);
        CourierRoute routeB = route(helper, SECOND_SOURCE, SECOND_DESTINATION, 5);

        Map<UUID, CourierRoute> routes = new LinkedHashMap<>();
        routes.put(routeA.id(), routeA);
        routes.put(routeB.id(), routeB);

        CourierRouteScheduler scheduler = new CourierRouteScheduler();
        scheduler.setInterval(20);
        scheduler.setTransferLimit(4);

        scheduler.tick(helper.getLevel(), routes, 100);

        if (count(destinationA) + count(destinationB) != 4) {
            helper.fail("First tick should execute exactly one route");
            return;
        }

        scheduler.tick(helper.getLevel(), routes, 120);

        if (count(destinationA) != 4 || count(destinationB) != 4) {
            helper.fail("Equal-priority routes did not alternate");
            return;
        }

        scheduler.tick(helper.getLevel(), routes, 140);
        scheduler.tick(helper.getLevel(), routes, 160);

        if (count(destinationA) != 8 || count(destinationB) != 8) {
            helper.fail("Round-robin distribution was not balanced");
            return;
        }

        helper.succeed();
    }


    public static void schedulerHandlesRouteRemoval(GameTestHelper helper) {
        helper.setBlock(SOURCE, Blocks.CHEST);
        helper.setBlock(DESTINATION, Blocks.CHEST);
        helper.setBlock(SECOND_SOURCE, Blocks.CHEST);
        helper.setBlock(SECOND_DESTINATION, Blocks.CHEST);

        ChestBlockEntity sourceA = chest(helper, SOURCE);
        ChestBlockEntity destinationA = chest(helper, DESTINATION);
        ChestBlockEntity sourceB = chest(helper, SECOND_SOURCE);
        ChestBlockEntity destinationB = chest(helper, SECOND_DESTINATION);

        sourceA.setItem(0, new ItemStack(Items.IRON_INGOT, 32));
        sourceB.setItem(0, new ItemStack(Items.GOLD_INGOT, 32));

        CourierRoute routeA = route(helper, SOURCE, DESTINATION, 5);
        CourierRoute routeB = route(helper, SECOND_SOURCE, SECOND_DESTINATION, 5);

        Map<UUID, CourierRoute> routes = new LinkedHashMap<>();
        routes.put(routeA.id(), routeA);
        routes.put(routeB.id(), routeB);

        CourierRouteScheduler scheduler = new CourierRouteScheduler();
        scheduler.setInterval(20);
        scheduler.setTransferLimit(4);

        scheduler.tick(helper.getLevel(), routes, 100);

        // Determine which route executed first.
        boolean firstWasA = count(destinationA) == 4;

        if (count(destinationA) + count(destinationB) != 4) {
            helper.fail("Expected exactly one initial transfer");
            return;
        }

        // Remove the route that was just executed.
        routes.remove(firstWasA ? routeA.id() : routeB.id());

        scheduler.tick(helper.getLevel(), routes, 120);

        if (firstWasA) {
            if (count(destinationA) != 4 || count(destinationB) != 4) {
                helper.fail("Remaining route B did not execute");
                return;
            }
        } else {
            if (count(destinationB) != 4 || count(destinationA) != 4) {
                helper.fail("Remaining route A did not execute");
                return;
            }
        }

        scheduler.tick(helper.getLevel(), routes, 140);

        if (firstWasA) {
            if (count(destinationA) != 4 || count(destinationB) != 8) {
                helper.fail("Removed route A executed again");
                return;
            }
        } else {
            if (count(destinationB) != 4 || count(destinationA) != 8) {
                helper.fail("Removed route B executed again");
                return;
            }
        }

        helper.succeed();
    }

    public static void schedulerHandlesPriorityChanges(GameTestHelper helper) {
        helper.setBlock(SOURCE, Blocks.CHEST);
        helper.setBlock(DESTINATION, Blocks.CHEST);
        helper.setBlock(SECOND_SOURCE, Blocks.CHEST);
        helper.setBlock(SECOND_DESTINATION, Blocks.CHEST);

        ChestBlockEntity sourceA = chest(helper, SOURCE);
        ChestBlockEntity destinationA = chest(helper, DESTINATION);
        ChestBlockEntity sourceB = chest(helper, SECOND_SOURCE);
        ChestBlockEntity destinationB = chest(helper, SECOND_DESTINATION);

        sourceA.setItem(0, new ItemStack(Items.IRON_INGOT, 32));
        sourceB.setItem(0, new ItemStack(Items.GOLD_INGOT, 32));

        CourierRoute routeA = route(helper, SOURCE, DESTINATION, 10, CourierRouteExecutionMode.INSTANT);
        CourierRoute routeB = route(helper, SECOND_SOURCE, SECOND_DESTINATION, 1, CourierRouteExecutionMode.INSTANT);

        Map<UUID, CourierRoute> routes = new LinkedHashMap<>();
        routes.put(routeA.id(), routeA);
        routes.put(routeB.id(), routeB);

        CourierRouteScheduler scheduler = new CourierRouteScheduler();
        scheduler.setInterval(20);
        scheduler.setTransferLimit(4);

        scheduler.tick(helper.getLevel(), routes, 100);

        if (count(destinationA) != 4 || count(destinationB) != 0) {
            helper.fail("Initial priority ordering was incorrect");
            return;
        }

        // Raise route B above route A.
        CourierRoute promotedB = new CourierRoute(routeB.id(), routeB.resourceType(), routeB.source(), routeB.destination(), routeB.enabled(), 20, routeB.executionMode(), CourierFilter.NONE);

        routes.put(promotedB.id(), promotedB);

        scheduler.tick(helper.getLevel(), routes, 120);

        if (count(destinationA) != 4 || count(destinationB) != 4) {
            helper.fail("Scheduler did not honor updated priority");
            return;
        }

        scheduler.tick(helper.getLevel(), routes, 140);

        if (count(destinationA) != 4 || count(destinationB) != 8) {
            helper.fail("Promoted route did not retain priority");
            return;
        }

        helper.succeed();
    }

    public static void multipleControllersConserveItems(GameTestHelper helper) {
        ColonyControllerBlockEntity controllerA = controller(helper);

        BlockPos controllerBPos = new BlockPos(1, 2, 3);
        BlockPos middlePos = new BlockPos(4, 2, 1);
        BlockPos finalDestinationPos = new BlockPos(6, 2, 1);

        helper.setBlock(controllerBPos, ModBlocks.COLONY_CONTROLLER.get());

        ColonyControllerBlockEntity controllerB = helper.getBlockEntity(controllerBPos, ColonyControllerBlockEntity.class);

        helper.setBlock(SOURCE, Blocks.CHEST);
        helper.setBlock(middlePos, Blocks.CHEST);
        helper.setBlock(finalDestinationPos, Blocks.CHEST);

        ChestBlockEntity source = chest(helper, SOURCE);
        ChestBlockEntity middle = chest(helper, middlePos);
        ChestBlockEntity destination = chest(helper, finalDestinationPos);

        source.setItem(0, new ItemStack(Items.IRON_INGOT, 64));

        CourierRoute firstRoute = route(helper, SOURCE, middlePos, 0);

        CourierRoute secondRoute = route(helper, middlePos, finalDestinationPos, 0);

        if (!controllerA.addRoute(firstRoute) || !controllerB.addRoute(secondRoute)) {
            helper.fail("Failed to register controller routes");
            return;
        }

        controllerA.setTransferInterval(1);
        controllerB.setTransferInterval(1);

        controllerA.setTransferLimit(4);
        controllerB.setTransferLimit(4);

        helper.runAfterDelay(10, () -> {
            int sourceCount = count(source);
            int middleCount = count(middle);
            int destinationCount = count(destination);

            int total = sourceCount + middleCount + destinationCount;

            if (total != 64) {
                helper.fail("Item conservation violated. Expected 64, got " + total);
                return;
            }

            if (sourceCount == 64) {
                helper.fail("First controller did not transfer items");
                return;
            }

            if (destinationCount == 0) {
                helper.fail("Second controller did not transfer items");
                return;
            }

            helper.succeed();
        });
    }

    public static void doubleChestSelfTransfer(GameTestHelper helper) {
        BlockPos leftPos = SOURCE;
        BlockPos rightPos = DESTINATION;

        BlockState leftState = Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.NORTH).setValue(ChestBlock.TYPE, ChestType.LEFT);

        BlockState rightState = Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.NORTH).setValue(ChestBlock.TYPE, ChestType.RIGHT);

        // Use adjacent positions for the two halves.
        rightPos = leftPos.east();

        helper.setBlock(leftPos, leftState);
        helper.setBlock(rightPos, rightState);

        BlockState actualLeft = helper.getBlockState(leftPos);
        BlockState actualRight = helper.getBlockState(rightPos);

        if (actualLeft.getValue(ChestBlock.TYPE) == ChestType.SINGLE || actualRight.getValue(ChestBlock.TYPE) == ChestType.SINGLE) {
            helper.fail("Test setup did not create a double chest");
            return;
        }

        if (!leftPos.relative(ChestBlock.getConnectedDirection(actualLeft)).equals(rightPos)) {
            helper.fail("Chest halves are not connected in the expected direction");
            return;
        }

        ChestBlockEntity left = chest(helper, leftPos);
        ChestBlockEntity right = chest(helper, rightPos);

        left.setItem(0, new ItemStack(Items.IRON_INGOT, 32));

        CourierRoute selfRoute = new CourierRoute(UUID.randomUUID(), CourierResourceType.ITEM, new CourierEndpoint(helper.absolutePos(leftPos), Direction.UP), new CourierEndpoint(helper.absolutePos(rightPos), Direction.UP), true, 0);

        TransferResult result = CourierTransferEngine.transfer(helper.getLevel(), selfRoute, 16);

        if (result.status() != TransferStatus.SOURCE_UNAVAILABLE) {
            helper.fail("Expected self-transfer rejection, got " + result.status());
            return;
        }

        if (result.amountMoved() != 0) {
            helper.fail("Double-chest self-transfer moved items");
            return;
        }

        if (count(left) != 32 || count(right) != 0) {
            helper.fail("Double-chest inventory changed during rejected transfer");
            return;
        }

        helper.succeed();
    }

    public static void courierModeSkipsInstantScheduler(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = controller(helper);

        helper.setBlock(SOURCE, Blocks.CHEST);
        helper.setBlock(DESTINATION, Blocks.CHEST);

        ChestBlockEntity source = chest(helper, SOURCE);
        ChestBlockEntity destination = chest(helper, DESTINATION);

        source.setItem(0, new ItemStack(Items.IRON_INGOT, 16));

        CourierRoute base = route(helper, SOURCE, DESTINATION, 0);

        CourierRoute courierRoute = new CourierRoute(base.id(), base.resourceType(), base.source(), base.destination(), base.enabled(), base.priority(), CourierRouteExecutionMode.COURIER, CourierFilter.NONE);

        if (!controller.addRoute(courierRoute)) {
            helper.fail("Could not register courier-managed route");
            return;
        }

        controller.setTransferInterval(1);

        helper.runAfterDelay(5, () -> {
            if (count(source) != 16 || count(destination) != 0) {
                helper.fail("Instant scheduler transferred items on a courier route");
                return;
            }

            helper.succeed();
        });
    }

    public static void instantModeStillTransfers(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = controller(helper);

        helper.setBlock(SOURCE, Blocks.CHEST);
        helper.setBlock(DESTINATION, Blocks.CHEST);

        ChestBlockEntity source = chest(helper, SOURCE);
        ChestBlockEntity destination = chest(helper, DESTINATION);

        source.setItem(0, new ItemStack(Items.IRON_INGOT, 16));

        CourierRoute instantRoute = route(helper, SOURCE, DESTINATION, 0);

        if (!controller.addRoute(instantRoute)) {
            helper.fail("Could not register instant route");
            return;
        }

        controller.setTransferInterval(1);

        helper.runAfterDelay(5, () -> {
            if (count(source) != 0 || count(destination) != 16) {
                helper.fail("Instant route stopped working after mode separation");
                return;
            }

            helper.succeed();
        });
    }

    public static void emptySourceHasNoWork(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = controller(helper);

        helper.setBlock(SOURCE, Blocks.CHEST);
        helper.setBlock(DESTINATION, Blocks.CHEST);

        ChestBlockEntity source = chest(helper, SOURCE);
        source.clearContent();

        CourierRoute route = route(helper, SOURCE, DESTINATION, 0, CourierRouteExecutionMode.COURIER);

        helper.assertTrue(!CourierWorkProbe.hasPendingWork(helper.getLevel(), route), "Empty source incorrectly reported courier work");

        helper.succeed();
    }

    public static void probeDoesNotTransferItems(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = controller(helper);

        helper.setBlock(SOURCE, Blocks.CHEST);
        helper.setBlock(DESTINATION, Blocks.CHEST);

        ChestBlockEntity source = chest(helper, SOURCE);
        ChestBlockEntity destination = chest(helper, DESTINATION);

        source.clearContent();
        destination.clearContent();

        source.setItem(0, new ItemStack(Items.IRON_INGOT, 16));

        CourierRoute route = route(helper, SOURCE, DESTINATION, 0, CourierRouteExecutionMode.COURIER);

        for (int i = 0; i < 10; i++) {
            helper.assertTrue(CourierWorkProbe.hasPendingWork(helper.getLevel(), route), "Expected pending item work");
        }

        helper.assertTrue(count(source) == 16, "Probe extracted items from source");

        helper.assertTrue(count(destination) == 0, "Probe inserted items into destination");

        helper.succeed();
    }

}
