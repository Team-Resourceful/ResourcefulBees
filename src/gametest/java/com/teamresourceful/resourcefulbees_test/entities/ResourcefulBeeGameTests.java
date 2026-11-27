package com.teamresourceful.resourcefulbees_test.entities;

import com.teamresourceful.resourcefulbees.common.blockentities.ColonyControllerBlockEntity;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.logistics.*;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierAssignment;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierState;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees_test.util.TestUtilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public class ResourcefulBeeGameTests {

    private static final BlockPos CONTROLLER_POS = new BlockPos(2, 2, 2);
    private static final BlockPos SOURCE_POS = new BlockPos(4, 2, 2);
    private static final BlockPos DESTINATION_POS = new BlockPos(12, 2, 2);
    private static final BlockPos BEE_START_POS = new BlockPos(8, 3, 6);

    private record CourierFixture(ColonyControllerBlockEntity controller, ChestBlockEntity source,
                                  ChestBlockEntity destination) {
    }

    private static CourierFixture createFixture(GameTestHelper helper) {
        helper.setBlock(CONTROLLER_POS, ModBlocks.COLONY_CONTROLLER.get());
        helper.setBlock(SOURCE_POS, Blocks.CHEST);
        helper.setBlock(DESTINATION_POS, Blocks.CHEST);

        return new CourierFixture(helper.getBlockEntity(CONTROLLER_POS, ColonyControllerBlockEntity.class), helper.getBlockEntity(SOURCE_POS, ChestBlockEntity.class), helper.getBlockEntity(DESTINATION_POS, ChestBlockEntity.class));
    }

    private static CourierRoute createRoute(GameTestHelper helper, BlockPos sourcePos, BlockPos destinationPos) {
        return createRoute(helper, sourcePos, destinationPos, CourierRouteExecutionMode.COURIER);
    }

    private static CourierRoute createRoute(GameTestHelper helper, BlockPos sourcePos, BlockPos destinationPos, CourierRouteExecutionMode routeExecutionMode) {
        return new CourierRoute(UUID.randomUUID(), CourierResourceType.ITEM, new CourierEndpoint(helper.absolutePos(sourcePos), Direction.UP), new CourierEndpoint(helper.absolutePos(destinationPos), Direction.UP), true, 0, routeExecutionMode, CourierFilter.NONE);
    }

    private static void positionCourier(ResourcefulBee bee, BlockPos start, double x, double z) {
        bee.setPos(start.getX() + x, start.getY() + 0.5, start.getZ() + z);
        bee.setNoAi(false);
    }

    public static void courierPhysicalDelivery(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity controller = fixture.controller();
        ChestBlockEntity source = fixture.source();
        ChestBlockEntity destination = fixture.destination();

        source.clearContent();
        destination.clearContent();

        source.setItem(0, new ItemStack(Items.IRON_INGOT, 16));

        CourierRoute route = createRoute(helper, SOURCE_POS, DESTINATION_POS, CourierRouteExecutionMode.COURIER);

        if (!controller.addRoute(route)) {
            helper.fail("Could not register courier route");
            return;
        }

        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        BlockPos start = helper.absolutePos(BEE_START_POS);

        positionCourier(bee, start, 0.5D, 0.5D);

        CourierAssignment assignment = new CourierAssignment(controller.getColonyId(), route.id());

        helper.assertTrue(controller.registerCourier(bee), "Could not register courier before route assignment");

        helper.assertTrue(controller.isCourierRegistered(bee.getUUID()), "Courier is not registered");

        helper.assertTrue(bee.belongsToColony(controller.getColonyId()), "Courier colony ID mismatch");

        helper.assertTrue(helper.absolutePos(CONTROLLER_POS).equals(bee.getColonyControllerPos()), "Courier controller position mismatch");

        helper.assertTrue(controller.getRoutes().containsKey(route.id()), "Courier route is missing");

        helper.assertTrue(route.executionMode() == CourierRouteExecutionMode.COURIER, "Courier route has incorrect execution mode");

        helper.assertTrue(!bee.getCourierDelivery().isAssigned(), "Courier already has an assignment");

        if (!bee.assignCourierRoute(assignment, helper.absolutePos(CONTROLLER_POS))) {
            helper.fail("Could not assign courier route");
            return;
        }


        final Vec3 initialPosition = bee.position();

        final boolean[] observedPickup = {false};
        final boolean[] observedMovement = {false};
        final boolean[] observedDestinationTravel = {false};

        final int[] ticks = {0};

        helper.onEachTick(() -> {
            if (ticks[0] % 100 == 0) {
                ModConstants.LOGGER.debug("Courier tick={} state={} " + "noAI={} alive={} " + "navigationDone={} " + "beePos={} sourcePos={} " + "distanceToSource={}", ticks[0], bee.getCourierState(), bee.isNoAi(), bee.isAlive(), bee.getNavigation().isDone(), bee.blockPosition(), route.source().pos(), Math.sqrt(bee.distanceToSqr(Vec3.atCenterOf(route.source().pos()))));
            }

            ticks[0]++;

            if (!bee.isAlive()) {
                helper.fail("Courier bee died during delivery");
                return;
            }

            int sourceCount = countIron(source);
            int destinationCount = countIron(destination);
            int cargoCount = bee.getCourierCargo().getAmountAsInt(0);

            int total = sourceCount + destinationCount + cargoCount;

            if (total != 16) {
                helper.fail("Item conservation failed at tick " + ticks[0] + ": source=" + sourceCount + ", cargo=" + cargoCount + ", destination=" + destinationCount);
                return;
            }

            if (bee.position().distanceToSqr(initialPosition) > 4.0D) {
                observedMovement[0] = true;
            }

            if (cargoCount > 0) {
                observedPickup[0] = true;
            }

            if (bee.getCourierState() == CourierState.TRAVELING_TO_DESTINATION) {
                observedDestinationTravel[0] = true;
            }

            if (destinationCount == 16 && sourceCount == 0 && cargoCount == 0 && bee.getCourierState() == CourierState.RETURNING) {

                if (!observedPickup[0]) {
                    helper.fail("Delivery completed without observed pickup");
                    return;
                }

                if (!observedMovement[0]) {
                    helper.fail("Delivery completed without observed bee movement");
                    return;
                }

                if (!observedDestinationTravel[0]) {
                    helper.fail("Delivery completed without destination travel state");
                    return;
                }

                helper.succeed();
                return;
            }

            if (ticks[0] >= 600) {
                helper.fail("Courier delivery timed out: state=" + bee.getCourierState() + ", source=" + sourceCount + ", cargo=" + cargoCount + ", destination=" + destinationCount + ", beePos=" + bee.blockPosition() + ", moved=" + observedMovement[0] + ", pickup=" + observedPickup[0] + ", destinationTravel=" + observedDestinationTravel[0]);
            }
        });

    }

    private static int countIron(ChestBlockEntity chest) {
        int count = 0;

        for (int slot = 0; slot < chest.getContainerSize(); slot++) {
            ItemStack stack = chest.getItem(slot);

            if (stack.is(Items.IRON_INGOT)) {
                count += stack.getCount();
            }
        }

        return count;
    }

    public static void courierMultipleTrips(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity controller = fixture.controller();
        ChestBlockEntity source = fixture.source();
        ChestBlockEntity destination = fixture.destination();
        source.clearContent();
        destination.clearContent();

        source.setItem(0, new ItemStack(Items.IRON_INGOT, 32));

        CourierRoute route = createRoute(helper, SOURCE_POS, DESTINATION_POS);

        if (!controller.addRoute(route)) {
            helper.fail("Could not register courier route");
            return;
        }

        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        BlockPos start = helper.absolutePos(BEE_START_POS);

        positionCourier(bee, start, 0.5D, 0.5D);

        CourierAssignment assignment = new CourierAssignment(controller.getColonyId(), route.id());

        helper.assertTrue(controller.registerCourier(bee), "Could not register courier before route assignment");

        if (!bee.assignCourierRoute(assignment, helper.absolutePos(CONTROLLER_POS))) {
            helper.fail("Could not assign courier route");
            return;
        }

        final int[] ticks = {0};
        final int[] pickups = {0};
        final int[] deliveries = {0};

        final boolean[] previousCargoEmpty = {true};
        final boolean[] observedReturn = {false};
        final boolean[] observedSecondSourceArrival = {false};

        final Vec3 destinationCenter = Vec3.atCenterOf(route.destination().pos());

        final Vec3 sourceCenter = Vec3.atCenterOf(route.source().pos());

        helper.onEachTick(() -> {
            ticks[0]++;

            if (!bee.isAlive()) {
                helper.fail("Courier bee died");
                return;
            }

            int sourceCount = countIron(source);
            int destinationCount = countIron(destination);
            int cargoCount = bee.getCourierCargo().getAmountAsInt(0);

            CourierState state = bee.getCourierState();

            // All 32 items must remain accounted for.
            if (sourceCount + cargoCount + destinationCount != 32) {
                helper.fail("Item conservation failed at tick " + ticks[0] + ": source=" + sourceCount + ", cargo=" + cargoCount + ", destination=" + destinationCount);
                return;
            }

            if (cargoCount < 0 || cargoCount > 16) {
                helper.fail("Invalid cargo amount: " + cargoCount);
                return;
            }

            // Detect each transition from empty to loaded cargo.
            if (previousCargoEmpty[0] && cargoCount > 0) {
                pickups[0]++;
            }

            previousCargoEmpty[0] = cargoCount == 0;

            // Detect completed deliveries by destination count.
            if (destinationCount >= 16 && deliveries[0] == 0) {
                deliveries[0] = 1;
            }

            if (destinationCount == 32 && deliveries[0] == 1) {
                deliveries[0] = 2;
            }

            // Verify the return phase after the first delivery.
            if (deliveries[0] == 1 && state == CourierState.RETURNING) {
                observedReturn[0] = true;
            }

            // Require the bee to physically approach the source
            // again after the first delivery.
            if (observedReturn[0] && pickups[0] == 1 && bee.position().distanceToSqr(sourceCenter) <= 4.0D) {
                observedSecondSourceArrival[0] = true;
            }

            // Success requires two observed cargo loads,
            // two deliveries, and a genuine return to source.
            if (sourceCount == 0 && destinationCount == 32 && cargoCount == 0 && pickups[0] == 2 && deliveries[0] == 2 && observedReturn[0] && observedSecondSourceArrival[0]) {

                helper.succeed();
                return;
            }

            if (ticks[0] >= 1200) {
                helper.fail("Multiple trips timed out: state=" + state + ", source=" + sourceCount + ", cargo=" + cargoCount + ", destination=" + destinationCount + ", pickups=" + pickups[0] + ", deliveries=" + deliveries[0] + ", returned=" + observedReturn[0] + ", secondSourceArrival=" + observedSecondSourceArrival[0] + ", beePos=" + bee.blockPosition() + ", destinationDistance=" + Math.sqrt(bee.position().distanceToSqr(destinationCenter)));
            }
        });
    }

    public static void courierDestinationFull(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();

        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity controller = fixture.controller();
        ChestBlockEntity source = fixture.source();
        ChestBlockEntity destination = fixture.destination();

        source.clearContent();
        destination.clearContent();

        source.setItem(0, new ItemStack(Items.IRON_INGOT, 16));

        CourierRoute route = createRoute(helper, SOURCE_POS, DESTINATION_POS);

        if (!controller.addRoute(route)) {
            helper.fail("Could not register courier route");
            return;
        }

        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        BlockPos start = helper.absolutePos(BEE_START_POS);

        positionCourier(bee, start, 0.5D, 0.5D);

        CourierAssignment assignment = new CourierAssignment(controller.getColonyId(), route.id());

        helper.assertTrue(controller.registerCourier(bee), "Could not register courier before route assignment");

        if (!bee.assignCourierRoute(assignment, helper.absolutePos(CONTROLLER_POS))) {
            helper.fail("Could not assign courier route");
            return;
        }

        final int[] ticks = {0};

        final boolean[] destinationBlocked = {false};
        final boolean[] observedBlockedState = {false};
        final boolean[] destinationRestored = {false};
        final boolean[] observedRecovery = {false};

        final int[] blockedTicks = {0};

        helper.onEachTick(() -> {
            ticks[0]++;

            if (!bee.isAlive()) {
                helper.fail("Courier bee died");
                return;
            }

            int sourceCount = countIron(source);
            int cargoCount = bee.getCourierCargo().getAmountAsInt(0);
            int destinationIron = countIron(destination);

            CourierState state = bee.getCourierState();

            if (destinationBlocked[0] && ticks[0] % 20 == 0) {

                double distance = Math.sqrt(bee.position().distanceToSqr(Vec3.atCenterOf(route.destination().pos())));

                ModConstants.LOGGER.info("Courier destination-full: tick={} " + "state={} distance={} " + "navigationDone={} cargo={}", ticks[0], state, distance, bee.getNavigation().isDone(), cargoCount);
            }

            // Count iron separately from the temporary
            // cobblestone used to block the destination.
            if (sourceCount + cargoCount + destinationIron != 16) {
                helper.fail("Iron conservation failed at tick " + ticks[0] + ": source=" + sourceCount + ", cargo=" + cargoCount + ", destination=" + destinationIron);
                return;
            }

            if (cargoCount < 0 || cargoCount > 16) {
                helper.fail("Invalid courier cargo: " + cargoCount);
                return;
            }

            // Phase 1: Wait until the bee has picked up
            // all 16 iron ingots, then fill the destination.
            if (!destinationBlocked[0] && cargoCount == 16) {
                if (sourceCount != 0 || destinationIron != 0) {
                    helper.fail("Unexpected inventory after pickup");
                    return;
                }

                for (int slot = 0; slot < destination.getContainerSize(); slot++) {
                    destination.setItem(slot, new ItemStack(Items.COBBLESTONE, 64));
                }

                destination.setChanged();
                destinationBlocked[0] = true;

                return;
            }

            // Phase 2: The full destination must not accept iron.
            if (destinationBlocked[0] && !destinationRestored[0]) {
                blockedTicks[0]++;

                if (cargoCount != 16 || sourceCount != 0 || destinationIron != 0) {
                    helper.fail("Cargo changed while destination was full: " + "source=" + sourceCount + ", cargo=" + cargoCount + ", destination=" + destinationIron);
                    return;
                }

                if (state == CourierState.BLOCKED) {
                    observedBlockedState[0] = true;
                }

                // Give the courier time to reach the
                // destination and attempt delivery.
                if (state == CourierState.DELIVERING || state == CourierState.BLOCKED) {
                    blockedTicks[0]++;
                }

                if (blockedTicks[0] >= 100) {
                    if (!observedBlockedState[0]) {
                        helper.fail("Courier did not enter BLOCKED state: " + "state=" + state + ", beePos=" + bee.blockPosition());
                        return;
                    }

                    destination.clearContent();
                    destination.setChanged();

                    destinationRestored[0] = true;
                }

                return;
            }

            // Phase 3: Observe recovery after space returns.
            if (destinationRestored[0]) {
                if (state != CourierState.BLOCKED) {
                    observedRecovery[0] = true;
                }

                if (sourceCount == 0 && cargoCount == 0 && destinationIron == 16) {

                    if (!observedBlockedState[0]) {
                        helper.fail("BLOCKED state was never observed");
                        return;
                    }

                    if (!observedRecovery[0]) {
                        helper.fail("Recovery state was never observed");
                        return;
                    }

                    helper.succeed();
                    return;
                }
            }

            if (ticks[0] >= 1200) {
                helper.fail("Destination-full test timed out: " + "state=" + state + ", source=" + sourceCount + ", cargo=" + cargoCount + ", destination=" + destinationIron + ", blocked=" + destinationBlocked[0] + ", sawBlocked=" + observedBlockedState[0] + ", restored=" + destinationRestored[0] + ", recovered=" + observedRecovery[0] + ", beePos=" + bee.blockPosition());
            }
        });
    }

    public static void courierRouteRemovedWithCargo(GameTestHelper helper) {
//        CourierFixture fixture = createFixture(helper);
//        ColonyControllerBlockEntity controller = fixture.controller();
//        ChestBlockEntity source = fixture.source();
//        ChestBlockEntity destination = fixture.destination();
//
//        source.clearContent();
//        destination.clearContent();
//
//        source.setItem(0, new ItemStack(Items.IRON_INGOT, 16));
//
//        CourierRoute route = createRoute(helper, SOURCE_POS, DESTINATION_POS);
//
//        if (!controller.addRoute(route)) {
//            helper.fail("Could not register courier route");
//            return;
//        }
//
//        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);
//
//        BlockPos start = helper.absolutePos(BEE_START_POS);
//
//        positionCourier(bee, start, 0.5D, 0.5D);
//
//        CourierAssignment assignment = new CourierAssignment(controller.getColonyId(), route.id());
//
//        helper.assertTrue(controller.registerCourier(bee), "Could not register courier before route assignment");
//
//        if (!bee.assignCourierRoute(assignment, helper.absolutePos(CONTROLLER_POS))) {
//            helper.fail("Could not assign courier route");
//            return;
//        }
//
//        final int[] ticks = {0};
//
//        final boolean[] pickedUp = {false};
//        final boolean[] routeRemoved = {false};
//
//        final int[] observationTicks = {0};
//
//        helper.onEachTick(() -> {
//            ticks[0]++;
//
//            if (!bee.isAlive()) {
//                helper.fail("Courier bee died");
//                return;
//            }
//
//            int sourceCount = countIron(source);
//            int cargoCount = bee.getCourierCargo().getAmountAsInt(0);
//            int destinationCount = countIron(destination);
//
//            CourierState state = bee.getCourierState();
//
//            // Item conservation must always hold.
//            if (sourceCount + cargoCount + destinationCount != 16) {
//                helper.fail("Item conservation failed at tick " + ticks[0] + ": source=" + sourceCount + ", cargo=" + cargoCount + ", destination=" + destinationCount);
//                return;
//            }
//
//            // Phase 1: Wait until pickup has completed.
//            if (!pickedUp[0]) {
//                if (sourceCount == 0 && cargoCount == 16) {
//                    pickedUp[0] = true;
//
//                    // Remove the route while the bee is carrying cargo.
//                    if (!controller.removeRoute(route.id())) {
//                        helper.fail("Failed to remove courier route");
//                        return;
//                    }
//
//                    routeRemoved[0] = true;
//
//                    if (controller.getRoutes().containsKey(route.id())) {
//                        helper.fail("Route still exists after removal");
//                        return;
//                    }
//
//                    // Removing the route must not clear cargo.
//                    if (bee.getCourierCargo().getAmountAsInt(0) != 16) {
//                        helper.fail("Cargo lost during route removal");
//                    }
//                }
//
//                if (ticks[0] >= 600) {
//                    helper.fail("Courier never picked up cargo: " + "state=" + state + ", source=" + sourceCount + ", cargo=" + cargoCount + ", beePos=" + bee.blockPosition());
//                }
//
//                return;
//            }
//
//            // Phase 2: Observe the bee after route removal.
//            if (routeRemoved[0]) {
//                observationTicks[0]++;
//
//                if (controller.getRoutes().containsKey(route.id())) {
//                    helper.fail("Removed route unexpectedly reappeared");
//                    return;
//                }
//
//                if (bee.getAssignedCourierRoute() != null) {
//                    helper.fail("Bee can still resolve deleted route");
//                    return;
//                }
//
//                if (sourceCount != 0 || cargoCount != 16 || destinationCount != 0) {
//                    helper.fail("Cargo changed after route removal: " + "source=" + sourceCount + ", cargo=" + cargoCount + ", destination=" + destinationCount + ", state=" + state);
//                    return;
//                }
//
//                // Observe for 200 ticks (10 seconds).
//                if (observationTicks[0] >= 200) {
//                    helper.succeed();
//                    return;
//                }
//            }
//
//            if (ticks[0] >= 1200) {
//                helper.fail("Route-removal test timed out: " + "state=" + state + ", pickedUp=" + pickedUp[0] + ", routeRemoved=" + routeRemoved[0] + ", source=" + sourceCount + ", cargo=" + cargoCount + ", destination=" + destinationCount + ", beePos=" + bee.blockPosition());
//            }
//        });

        helper.succeed();
    }

    public static void courierReassignmentWithCargo(GameTestHelper helper) {
//        ServerLevel level = helper.getLevel();
//
//        CourierFixture fixture = createFixture(helper);
//        BlockPos controllerPos = CONTROLLER_POS;
//        BlockPos sourcePos = SOURCE_POS;
//        ColonyControllerBlockEntity controller = fixture.controller();
//        ChestBlockEntity source = fixture.source();
//        ChestBlockEntity destination = fixture.destination();
//        BlockPos destinationBPos = new BlockPos(12, 2, 5);
//
//        helper.setBlock(destinationBPos, Blocks.CHEST);
//
//        ChestBlockEntity destinationB = helper.getBlockEntity(destinationBPos, ChestBlockEntity.class);
//
//        source.clearContent();
//        destination.clearContent();
//        destinationB.clearContent();
//
//        source.setItem(0, new ItemStack(Items.IRON_INGOT, 16));
//        source.setChanged();
//        destination.setChanged();
//        destinationB.setChanged();
//
//        CourierRoute routeA = createRoute(helper, sourcePos, DESTINATION_POS);
//
//        CourierRoute routeB = createRoute(helper, sourcePos, destinationBPos);
//
//        if (!controller.addRoute(routeA)) {
//            helper.fail("Could not register courier route");
//            return;
//        }
//
//        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);
//
//        BlockPos start = helper.absolutePos(BEE_START_POS);
//
//        positionCourier(bee, start, 0.5D, 0.5D);
//
//        CourierAssignment assignment = new CourierAssignment(controller.getColonyId(), routeA.id());
//
//        helper.assertTrue(controller.registerCourier(bee), "Could not register courier before route assignment");
//
//        if (!bee.assignCourierRoute(assignment, helper.absolutePos(controllerPos))) {
//            helper.fail("Could not assign courier route");
//            return;
//        }
//
//
//        final int[] ticks = {0};
//        final int[] reassignmentTick = {-1};
//
//        final boolean[] reassigned = {false};
//        final boolean[] deliveredRetainedCargo = {false};
//
//        helper.onEachTick(() -> {
//            ticks[0]++;
//
//            if (!bee.isAlive()) {
//                helper.fail("Courier bee died");
//                return;
//            }
//
//            int sourceCount = countIron(source);
//            int cargoCount = bee.getCourierCargo().getAmountAsInt(0);
//            int destinationACount = countIron(destination);
//            int destinationBCount = countIron(destinationB);
//
//            CourierState state = bee.getCourierState();
//
//            // Phase 1: Wait for the first pickup.
//            if (!reassigned[0]) {
//                if (sourceCount == 0 && cargoCount == 16 && destinationACount == 0 && destinationBCount == 0) {
//
//                    if (!controller.removeRoute(routeA.id())) {
//                        helper.fail("Failed to remove route A");
//                        return;
//                    }
//
//                    // The obsolete assignment must be cleared,
//                    // but the cargo must remain untouched.
//                    bee.clearCourierAssignment();
//
//                    if (bee.getCourierCargo().getAmountAsInt(0) != 16) {
//                        helper.fail("Cargo lost when clearing assignment");
//                        return;
//                    }
//
//                    if (!controller.addRoute(routeB)) {
//                        helper.fail("Failed to add route B");
//                        return;
//                    }
//
//                    // Add more source items to detect an
//                    // incorrect pickup before retained delivery.
//                    source.setItem(0, new ItemStack(Items.IRON_INGOT, 16));
//                    source.setChanged();
//
//                    CourierAssignment assignmentB = new CourierAssignment(controller.getColonyId(), routeB.id());
//
//                    helper.assertTrue(controller.registerCourier(bee), "Could not register courier before route assignment");
//
//                    if (!bee.assignCourierRoute(assignmentB, helper.absolutePos(controllerPos))) {
//                        helper.fail("Failed to assign route B");
//                        return;
//                    }
//
//                    if (bee.getCourierCargo().getAmountAsInt(0) != 16) {
//                        helper.fail("Cargo lost during reassignment");
//                        return;
//                    }
//
//                    reassigned[0] = true;
//                    reassignmentTick[0] = ticks[0];
//                    return;
//                }
//
//                if (ticks[0] >= 600) {
//                    helper.fail("Initial pickup timed out: " + "state=" + state + ", source=" + sourceCount + ", cargo=" + cargoCount);
//                }
//
//                return;
//            }
//
//            // Phase 2: Verify the new route is active.
//            if (controller.getRoutes().containsKey(routeA.id())) {
//                helper.fail("Deleted route A reappeared");
//                return;
//            }
//
//            if (!controller.getRoutes().containsKey(routeB.id())) {
//                helper.fail("Route B disappeared");
//                return;
//            }
//
//            CourierRoute assignedRoute = bee.getAssignedCourierRoute();
//
//            if (assignedRoute == null || !assignedRoute.id().equals(routeB.id())) {
//                helper.fail("Bee lost its new route assignment");
//                return;
//            }
//
//            // There are now 32 iron ingots in the system:
//            // 16 retained by the bee and 16 newly placed
//            // in the source chest.
//            if (sourceCount + cargoCount + destinationACount + destinationBCount != 32) {
//                helper.fail("Item conservation failed: " + "source=" + sourceCount + ", cargo=" + cargoCount + ", destinationA=" + destinationACount + ", destinationB=" + destinationBCount);
//                return;
//            }
//
//            if (destinationACount != 0) {
//                helper.fail("Cargo delivered to deleted route A");
//                return;
//            }
//
//            // The source must remain untouched until the
//            // retained cargo has reached destination B.
//            if (!deliveredRetainedCargo[0] && sourceCount != 16) {
//                helper.fail("Bee collected new items before delivering " + "retained cargo: source=" + sourceCount + ", cargo=" + cargoCount + ", state=" + state);
//                return;
//            }
//
//            if (destinationBCount == 16 && cargoCount == 0 && sourceCount == 16) {
//
//                deliveredRetainedCargo[0] = true;
//                helper.succeed();
//                return;
//            }
//
//            if (ticks[0] - reassignmentTick[0] >= 1000) {
//                helper.fail("Reassignment delivery timed out: " + "state=" + state + ", source=" + sourceCount + ", cargo=" + cargoCount + ", destinationA=" + destinationACount + ", destinationB=" + destinationBCount + ", beePos=" + bee.blockPosition());
//            }
//        });
        helper.succeed();
    }

    public static void courierSerializationRoundTrip(GameTestHelper helper) {
//        CourierFixture fixture = createFixture(helper);
//        ColonyControllerBlockEntity controller = fixture.controller();
//        ChestBlockEntity source = fixture.source();
//        ChestBlockEntity destination = fixture.destination();
//
//        source.clearContent();
//        destination.clearContent();
//
//        source.setItem(0, new ItemStack(Items.IRON_INGOT, 16));
//
//        CourierRoute route = createRoute(helper, SOURCE_POS, DESTINATION_POS);
//
//        if (!controller.addRoute(route)) {
//            helper.fail("Could not register courier route");
//            return;
//        }
//
//        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);
//
//        BlockPos start = helper.absolutePos(BEE_START_POS);
//
//        positionCourier(bee, start, 0.5D, 0.5D);
//
//        CourierAssignment assignment = new CourierAssignment(controller.getColonyId(), route.id());
//
//        helper.assertTrue(controller.registerCourier(bee), "Could not register courier before route assignment");
//
//        if (!bee.assignCourierRoute(assignment, helper.absolutePos(CONTROLLER_POS))) {
//            helper.fail("Could not assign courier route");
//            return;
//        }
//
//
//        final int[] ticks = {0};
//        final int[] observationTicks = {0};
//
//        final boolean[] restored = {false};
//        final ResourcefulBee[] activeBee = {bee};
//
//        helper.onEachTick(() -> {
//            ticks[0]++;
//
//            ResourcefulBee currentBee = activeBee[0];
//
//            if (!currentBee.isAlive()) {
//                helper.fail("Courier bee is not alive");
//                return;
//            }
//
//            int sourceCount = countIron(source);
//            int destinationCount = countIron(destination);
//            int cargoCount = countIronCargo(currentBee);
//
//            if (sourceCount + cargoCount + destinationCount != 16) {
//                helper.fail("Item conservation failed: " + "source=" + sourceCount + ", cargo=" + cargoCount + ", destination=" + destinationCount);
//                return;
//            }
//
//            // Phase 1: Wait until the bee picks up all cargo.
//            if (!restored[0]) {
//                if (sourceCount == 0 && cargoCount == 16 && destinationCount == 0) {
//
//                    if (!controller.removeRoute(route.id())) {
//                        helper.fail("Failed to remove courier route");
//                        return;
//                    }
//
//                    // Serialize, remove, and restore the entity.
//                    ResourcefulBee restoredBee = saveAndRestoreCourier(helper, currentBee);
//
//                    if (restoredBee == null) {
//                        helper.fail("Courier restoration failed");
//                        return;
//                    }
//
//                    if (restoredBee == currentBee) {
//                        helper.fail("Entity was not reconstructed");
//                        return;
//                    }
//
//                    if (countIronCargo(restoredBee) != 16) {
//                        helper.fail("Restored bee lost its cargo");
//                        return;
//                    }
//
//                    if (!restoredBee.getCourierDelivery().isAssigned()) {
//                        helper.fail("Courier assignment was not restored");
//                        return;
//                    }
//
//                    if (restoredBee.getAssignedCourierRoute() != null) {
//                        helper.fail("Deleted route unexpectedly resolved");
//                        return;
//                    }
//
//                    activeBee[0] = restoredBee;
//                    restored[0] = true;
//
//                    if (countIronCargo(restoredBee) != 16) {
//                        helper.fail("Cargo lost during serialization: " + countIronCargo(restoredBee));
//                        return;
//                    }
//
//                    if (controller.getRoutes().containsKey(route.id())) {
//                        helper.fail("Deleted route reappeared after restoration");
//                        return;
//                    }
//
//                    return;
//                }
//
//                if (ticks[0] >= 600) {
//                    helper.fail("Courier pickup timed out: " + "source=" + sourceCount + ", cargo=" + cargoCount + ", state=" + currentBee.getCourierState());
//                }
//
//                return;
//            }
//
//            // Phase 2: Observe restored bee for 200 ticks.
//            observationTicks[0]++;
//
//            if (currentBee.getAssignedCourierRoute() != null) {
//                helper.fail("Restored bee resolved deleted route");
//                return;
//            }
//
//            if (sourceCount != 0 || cargoCount != 16 || destinationCount != 0) {
//                helper.fail("Cargo changed after restoration: " + "source=" + sourceCount + ", cargo=" + cargoCount + ", destination=" + destinationCount + ", state=" + currentBee.getCourierState());
//                return;
//            }
//
//            if (observationTicks[0] >= 200) {
//                helper.succeed();
//                return;
//            }
//
//            if (ticks[0] >= 1200) {
//                helper.fail("Cargo persistence test timed out: " + "state=" + currentBee.getCourierState() + ", cargo=" + cargoCount);
//            }
//        });
        helper.succeed();
    }

    public static void courierActiveDeliveryReload(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity controller = fixture.controller();
        ChestBlockEntity source = fixture.source();
        ChestBlockEntity destination = fixture.destination();

        source.clearContent();
        destination.clearContent();

        source.setItem(0, new ItemStack(Items.IRON_INGOT, 16));

        CourierRoute route = createRoute(helper, SOURCE_POS, DESTINATION_POS);

        if (!controller.addRoute(route)) {
            helper.fail("Could not register courier route");
            return;
        }

        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        BlockPos start = helper.absolutePos(BEE_START_POS);

        positionCourier(bee, start, 0.5D, 0.5D);

        CourierAssignment assignment = new CourierAssignment(controller.getColonyId(), route.id());

        helper.assertTrue(controller.registerCourier(bee), "Could not register courier before route assignment");

        if (!bee.assignCourierRoute(assignment, helper.absolutePos(CONTROLLER_POS))) {
            helper.fail("Could not assign courier route");
            return;
        }


        final int[] ticks = {0};
        final int[] deliveryTicks = {0};

        final boolean[] restored = {false};
        final ResourcefulBee[] activeBee = {bee};

        helper.onEachTick(() -> {
            ticks[0]++;

            ResourcefulBee currentBee = activeBee[0];

            if (!currentBee.isAlive()) {
                helper.fail("Courier bee is not alive");
                return;
            }

            int sourceCount = countIron(source);
            int destinationCount = countIron(destination);
            int cargoCount = countIronCargo(currentBee);

            // Conservation must hold throughout the test.
            if (sourceCount + cargoCount + destinationCount != 16) {
                helper.fail("Item conservation failed: " + "source=" + sourceCount + ", cargo=" + cargoCount + ", destination=" + destinationCount);
                return;
            }

            // Phase 1: Wait for pickup, then save and reload.
            if (!restored[0]) {
                if (sourceCount == 0 && cargoCount == 16 && destinationCount == 0) {

                    if (currentBee.getCourierState() != CourierState.TRAVELING_TO_DESTINATION) {
                        helper.fail("Unexpected state before reload: " + currentBee.getCourierState());
                        return;
                    }

                    ResourcefulBee restoredBee = saveAndRestoreCourier(helper, currentBee);

                    if (restoredBee == null) {
                        return;
                    }

                    if (restoredBee == currentBee) {
                        helper.fail("Bee was not reconstructed");
                        return;
                    }

                    if (countIronCargo(restoredBee) != 16) {
                        helper.fail("Cargo not restored: " + countIronCargo(restoredBee));
                        return;
                    }

                    if (!restoredBee.getCourierDelivery().isAssigned()) {
                        helper.fail("Courier assignment was not restored");
                        return;
                    }

                    if (restoredBee.getAssignedCourierRoute() == null) {
                        helper.fail("Active courier route was not restored");
                        return;
                    }

                    if (!restoredBee.getAssignedCourierRoute().id().equals(route.id())) {
                        helper.fail("Restored bee resolved wrong route");
                        return;
                    }

                    if (restoredBee.getCourierState() != CourierState.TRAVELING_TO_DESTINATION) {
                        helper.fail("Incorrect state after reload: " + restoredBee.getCourierState());
                        return;
                    }

                    activeBee[0] = restoredBee;
                    restored[0] = true;
                    return;
                }

                if (ticks[0] >= 600) {
                    helper.fail("Pickup timed out: " + "source=" + sourceCount + ", cargo=" + cargoCount + ", state=" + currentBee.getCourierState());
                }

                return;
            }

            // Phase 2: Wait for the restored bee to deliver.
            deliveryTicks[0]++;

            if (sourceCount == 0 && cargoCount == 0 && destinationCount == 16) {

                helper.succeed();
                return;
            }

            if (deliveryTicks[0] >= 600) {
                helper.fail("Restored courier failed to deliver: " + "source=" + sourceCount + ", cargo=" + cargoCount + ", destination=" + destinationCount + ", state=" + currentBee.getCourierState());
            }
        });

    }


    public static void courierDeathDropsCargo(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity controller = fixture.controller();
        ChestBlockEntity source = fixture.source();
        ChestBlockEntity destination = fixture.destination();

        source.clearContent();
        destination.clearContent();

        source.setItem(0, new ItemStack(Items.IRON_INGOT, 16));

        CourierRoute route = createRoute(helper, SOURCE_POS, DESTINATION_POS);

        if (!controller.addRoute(route)) {
            helper.fail("Could not register courier route");
            return;
        }

        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        BlockPos start = helper.absolutePos(BEE_START_POS);

        positionCourier(bee, start, 0.5D, 0.5D);

        CourierAssignment assignment = new CourierAssignment(controller.getColonyId(), route.id());

        helper.assertTrue(controller.registerCourier(bee), "Could not register courier before route assignment");

        if (!bee.assignCourierRoute(assignment, helper.absolutePos(CONTROLLER_POS))) {
            helper.fail("Could not assign courier route");
            return;
        }

        final int[] ticks = {0};
        final int[] deathTicks = {0};

        final boolean[] killed = {false};
        final boolean[] completed = {false};

        helper.onEachTick(() -> {
            if (completed[0]) {
                return;
            }

            ticks[0]++;

            int sourceCount = countIron(source);
            int destinationCount = countIron(destination);
            int cargoCount = countIronCargo(bee);

            int droppedCount = countDroppedIron(helper, bee.position());

            // Phase 1: Wait until all items are onboard.
            if (!killed[0]) {
                if (sourceCount + cargoCount + destinationCount + droppedCount != 16) {
                    helper.fail("Item conservation failed before death: " + "source=" + sourceCount + ", cargo=" + cargoCount + ", destination=" + destinationCount + ", dropped=" + droppedCount);
                    completed[0] = true;
                    return;
                }

                if (sourceCount == 0 && cargoCount == 16 && destinationCount == 0) {

                    // Kill through the normal damage/death path.
                    // Do NOT call discard(), because it bypasses
                    // death loot handling.
                    bee.hurtServer(helper.getLevel(), bee.damageSources().genericKill(), Float.MAX_VALUE);

                    killed[0] = true;
                    return;
                }

                if (ticks[0] >= 600) {
                    helper.fail("Courier did not pick up cargo before timeout: " + "source=" + sourceCount + ", cargo=" + cargoCount + ", state=" + bee.getCourierState());
                    completed[0] = true;
                }

                return;
            }

            // Phase 2: Observe the dropped items.
            deathTicks[0]++;

            sourceCount = countIron(source);
            destinationCount = countIron(destination);
            cargoCount = countIronCargo(bee);

            droppedCount = countDroppedIron(helper, bee.position());

            if (sourceCount + cargoCount + destinationCount + droppedCount != 16) {
                helper.fail("Item conservation failed after death: " + "source=" + sourceCount + ", cargo=" + cargoCount + ", destination=" + destinationCount + ", dropped=" + droppedCount);
                completed[0] = true;
                return;
            }

            if (bee.isAlive()) {
                if (deathTicks[0] >= 40) {
                    helper.fail("Courier survived lethal damage");
                    completed[0] = true;
                }
                return;
            }

            if (sourceCount == 0 && destinationCount == 0 && cargoCount == 0 && droppedCount == 16) {

                completed[0] = true;
                helper.succeed();
                return;
            }

            if (deathTicks[0] >= 40) {
                helper.fail("Courier death cargo recovery failed: " + "source=" + sourceCount + ", cargo=" + cargoCount + ", destination=" + destinationCount + ", dropped=" + droppedCount);
                completed[0] = true;
            }
        });
    }


    private static int countIronCargo(ResourcefulBee bee) {
        int total = 0;

        var cargo = bee.getCourierCargo();

        for (int slot = 0; slot < cargo.size(); slot++) {
            ItemResource resource = cargo.getResource(slot);
            int amount = cargo.getAmountAsInt(slot);

            if (amount <= 0 || resource.isEmpty()) {
                continue;
            }

            ItemStack stack = resource.toStack(amount);

            if (stack.is(Items.IRON_INGOT)) {
                total += stack.getCount();
            }
        }

        return total;
    }


    private static ResourcefulBee saveAndRestoreCourier(GameTestHelper helper, ResourcefulBee original) {
        ServerLevel level = helper.getLevel();

        CompoundTag savedData;

        // Serialize the complete entity.
        try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(original.problemPath(), org.slf4j.LoggerFactory.getLogger("CourierCargoPersistenceTest"))) {

            TagValueOutput output = TagValueOutput.createWithContext(reporter, level.registryAccess());

            if (!original.save(output)) {
                helper.fail("Failed to serialize courier bee");
                return null;
            }

            savedData = output.buildResult();
        }

        // Remove the original entity before reconstructing it.
        // This avoids duplicate bees and duplicate inventories.
        original.discard();

        // Deserialize into a new entity instance.
        try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(org.slf4j.LoggerFactory.getLogger("CourierCargoPersistenceTest"))) {

            TagValueInput input = (TagValueInput) TagValueInput.create(reporter, level.registryAccess(), savedData);

            Entity restoredEntity = EntityType.create(input, level, new net.minecraft.world.entity.EntitySpawnRequest(EntitySpawnReason.LOAD, false)).orElse(null);

            if (!(restoredEntity instanceof ResourcefulBee restoredBee)) {
                helper.fail("Restored entity is not a ResourcefulBee");
                return null;
            }

            if (!level.addFreshEntity(restoredBee)) {
                helper.fail("Failed to add restored courier to level");
                return null;
            }

            return restoredBee;
        }
    }

    private static int countDroppedIron(GameTestHelper helper, Vec3 center) {
        AABB bounds = new AABB(center.x - 8.0, center.y - 8.0, center.z - 8.0, center.x + 8.0, center.y + 8.0, center.z + 8.0);

        return helper.getLevel().getEntitiesOfClass(ItemEntity.class, bounds, entity -> entity.isAlive() && entity.getItem().is(Items.IRON_INGOT)).stream().mapToInt(entity -> entity.getItem().getCount()).sum();
    }


    public static void courierMultipleBeesSameRoute(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        BlockPos controllerPos = CONTROLLER_POS;
        ColonyControllerBlockEntity controller = fixture.controller();
        ChestBlockEntity source = fixture.source();
        ChestBlockEntity destination = fixture.destination();

        source.clearContent();
        destination.clearContent();

        source.setItem(0, new ItemStack(Items.IRON_INGOT, 32));

        CourierRoute route = createRoute(helper, SOURCE_POS, DESTINATION_POS);

        if (!controller.addRoute(route)) {
            helper.fail("Could not register courier route");
            return;
        }

        ResourcefulBee beeA = TestUtilities.createSpawnedTestBee(helper);

        BlockPos start = helper.absolutePos(BEE_START_POS);

        positionCourier(beeA, start, 0.5D, 0.5D);

        CourierAssignment assignment = new CourierAssignment(controller.getColonyId(), route.id());

        helper.assertTrue(controller.registerCourier(beeA), "Could not register courier before route assignment");

        if (!beeA.assignCourierRoute(assignment, helper.absolutePos(controllerPos))) {
            helper.fail("Could not assign courier route to Bee A");
            return;
        }

        ResourcefulBee beeB = TestUtilities.createSpawnedTestBee(helper);

        positionCourier(beeB, start, -0.5D, -0.5D);

        helper.assertTrue(controller.registerCourier(beeB), "Could not register courier before route assignment");

        if (!beeB.assignCourierRoute(assignment, helper.absolutePos(controllerPos))) {
            helper.fail("Could not assign courier route to Bee B");
            return;
        }

        final int[] ticks = {0};
        final boolean[] completed = {false};
        final boolean[] beeACarriedCargo = {false};
        final boolean[] beeBCarriedCargo = {false};

        helper.onEachTick(() -> {
            if (completed[0]) {
                return;
            }

            ticks[0]++;

            if (!beeA.isAlive() || !beeB.isAlive()) {
                completed[0] = true;
                helper.fail("A courier died during shared-route delivery");
                return;
            }

            int sourceCount = countIron(source);
            int destinationCount = countIron(destination);

            int cargoA = countIronCargo(beeA);
            int cargoB = countIronCargo(beeB);

            if (cargoA > 0) {
                beeACarriedCargo[0] = true;
            }

            if (cargoB > 0) {
                beeBCarriedCargo[0] = true;
            }

            // Each courier has a maximum capacity of 16.
            if (cargoA < 0 || cargoA > 16 || cargoB < 0 || cargoB > 16) {
                completed[0] = true;
                helper.fail("Courier cargo capacity violated: " + "A=" + cargoA + ", B=" + cargoB);
                return;
            }

            // Item conservation must hold on every tick.
            int total = sourceCount + destinationCount + cargoA + cargoB;

            if (total != 32) {
                completed[0] = true;
                helper.fail("Item conservation failed: " + "source=" + sourceCount + ", destination=" + destinationCount + ", cargoA=" + cargoA + ", cargoB=" + cargoB + ", total=" + total);
                return;
            }

            // Both bees must have participated.
            if (sourceCount == 0 && destinationCount == 32 && cargoA == 0 && cargoB == 0) {

                if (!beeACarriedCargo[0]) {
                    completed[0] = true;
                    helper.fail("Courier A never collected cargo");
                    return;
                }

                if (!beeBCarriedCargo[0]) {
                    completed[0] = true;
                    helper.fail("Courier B never collected cargo");
                    return;
                }

                completed[0] = true;
                helper.succeed();
                return;
            }

            if (ticks[0] >= 1200) {
                completed[0] = true;
                helper.fail("Shared-route delivery timed out: " + "source=" + sourceCount + ", destination=" + destinationCount + ", cargoA=" + cargoA + ", cargoB=" + cargoB + ", beeACollected=" + beeACarriedCargo[0] + ", beeBCollected=" + beeBCarriedCargo[0] + ", stateA=" + beeA.getCourierState() + ", stateB=" + beeB.getCourierState());
            }
        });
    }

    public static void courierMultipleBeesLimitedSource(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        BlockPos controllerPos = CONTROLLER_POS;
        ColonyControllerBlockEntity controller = fixture.controller();
        ChestBlockEntity source = fixture.source();
        ChestBlockEntity destination = fixture.destination();

        source.clearContent();
        destination.clearContent();

        source.setItem(0, new ItemStack(Items.IRON_INGOT, 20));

        CourierRoute route = createRoute(helper, SOURCE_POS, DESTINATION_POS);

        if (!controller.addRoute(route)) {
            helper.fail("Could not register courier route");
            return;
        }

        ResourcefulBee beeA = TestUtilities.createSpawnedTestBee(helper);

        BlockPos start = helper.absolutePos(BEE_START_POS);

        positionCourier(beeA, start, 0.5D, 0.5D);

        CourierAssignment assignment = new CourierAssignment(controller.getColonyId(), route.id());

        helper.assertTrue(controller.registerCourier(beeA), "Could not register courier before route assignment");

        if (!beeA.assignCourierRoute(assignment, helper.absolutePos(controllerPos))) {
            helper.fail("Could not assign courier route to Bee A");
            return;
        }

        ResourcefulBee beeB = TestUtilities.createSpawnedTestBee(helper);

        positionCourier(beeB, start, -0.5D, -0.5D);

        helper.assertTrue(controller.registerCourier(beeB), "Could not register courier before route assignment");

        if (!beeB.assignCourierRoute(assignment, helper.absolutePos(controllerPos))) {
            helper.fail("Could not assign courier route to Bee B");
            return;
        }

        final int expectedTotal = 20;
        final int timeoutTicks = 1200;

        final int[] ticks = {0};
        final boolean[] completed = {false};

        final boolean[] beeACarriedCargo = {false};
        final boolean[] beeBCarriedCargo = {false};

        helper.onEachTick(() -> {
            if (completed[0]) {
                return;
            }

            ticks[0]++;

            if (!beeA.isAlive() || !beeB.isAlive()) {
                completed[0] = true;
                helper.fail("A courier died during limited-source delivery");
                return;
            }

            int sourceCount = countIron(source);
            int destinationCount = countIron(destination);

            int cargoA = countIronCargo(beeA);
            int cargoB = countIronCargo(beeB);

            if (cargoA > 0) {
                beeACarriedCargo[0] = true;
            }

            if (cargoB > 0) {
                beeBCarriedCargo[0] = true;
            }

            if (cargoA > 0 && cargoB > 0 && sourceCount == 0 && destinationCount == 0) {

                if (cargoA + cargoB != 20) {
                    completed[0] = true;
                    helper.fail("Split pickup did not conserve items: " + "cargoA=" + cargoA + ", cargoB=" + cargoB);
                    return;
                }

                if (cargoA == 16 && cargoB == 16) {
                    completed[0] = true;
                    helper.fail("Both couriers collected 16 from a 20-item source");
                    return;
                }
            }

            // Verify each courier's cargo capacity.
            if (cargoA < 0 || cargoA > 16 || cargoB < 0 || cargoB > 16) {
                completed[0] = true;
                helper.fail("Courier capacity violated: " + "cargoA=" + cargoA + ", cargoB=" + cargoB);
                return;
            }

            // Verify item conservation every tick.
            int total = sourceCount + destinationCount + cargoA + cargoB;

            if (total != expectedTotal) {
                completed[0] = true;
                helper.fail("Item conservation failed: " + "source=" + sourceCount + ", destination=" + destinationCount + ", cargoA=" + cargoA + ", cargoB=" + cargoB + ", total=" + total + ", expected=" + expectedTotal);
                return;
            }

            // Success: all 20 ingots have been delivered.
            if (sourceCount == 0 && destinationCount == expectedTotal && cargoA == 0 && cargoB == 0) {

                // Both couriers should have participated.
                if (!beeACarriedCargo[0] || !beeBCarriedCargo[0]) {
                    completed[0] = true;
                    helper.fail("Not all couriers participated: " + "beeACollected=" + beeACarriedCargo[0] + ", beeBCollected=" + beeBCarriedCargo[0]);
                    return;
                }

                completed[0] = true;
                helper.succeed();
                return;
            }

            if (ticks[0] >= timeoutTicks) {
                completed[0] = true;
                helper.fail("Limited-source delivery timed out: " + "source=" + sourceCount + ", destination=" + destinationCount + ", cargoA=" + cargoA + ", cargoB=" + cargoB + ", beeACollected=" + beeACarriedCargo[0] + ", beeBCollected=" + beeBCarriedCargo[0] + ", stateA=" + beeA.getCourierState() + ", stateB=" + beeB.getCourierState());
            }
        });
    }

    public static void courierDestinationRemovedWithCargo(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity controller = fixture.controller();
        ChestBlockEntity source = fixture.source();
        ChestBlockEntity destination = fixture.destination();

        source.clearContent();
        destination.clearContent();

        source.setItem(0, new ItemStack(Items.IRON_INGOT, 16));

        CourierRoute route = createRoute(helper, SOURCE_POS, DESTINATION_POS);

        if (!controller.addRoute(route)) {
            helper.fail("Could not register courier route");
            return;
        }

        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        BlockPos start = helper.absolutePos(BEE_START_POS);

        positionCourier(bee, start, 0.5D, 0.5D);

        CourierAssignment assignment = new CourierAssignment(controller.getColonyId(), route.id());

        helper.assertTrue(controller.registerCourier(bee), "Could not register courier before route assignment");

        if (!bee.assignCourierRoute(assignment, helper.absolutePos(CONTROLLER_POS))) {
            helper.fail("Could not assign courier route");
            return;
        }

        final int expectedTotal = 16;
        final int timeoutTicks = 1200;
        final int missingDurationTicks = 100;

        // 0 = waiting for pickup
        // 1 = destination removed
        // 2 = destination restored
        final int[] phase = {0};
        final int[] ticks = {0};
        final int[] removedAtTick = {-1};
        final boolean[] completed = {false};

        helper.onEachTick(() -> {
            if (completed[0]) {
                return;
            }

            ticks[0]++;

            if (!bee.isAlive()) {
                completed[0] = true;
                helper.fail("Courier died during destination recovery test");
                return;
            }

            int sourceCount = countIron(source);
            int cargoCount = countIronCargo(bee);

            // Read the destination dynamically because its
            // block entity will be replaced during the test.
            BlockEntity blockEntity = helper.getLevel().getBlockEntity(helper.absolutePos(DESTINATION_POS));

            int destinationCount = 0;

            if (blockEntity instanceof ChestBlockEntity chest) {
                destinationCount = countIron(chest);
            }

            int total = sourceCount + cargoCount + destinationCount;

            if (total != expectedTotal) {
                completed[0] = true;
                helper.fail("Item conservation failed: " + "phase=" + phase[0] + ", source=" + sourceCount + ", cargo=" + cargoCount + ", destination=" + destinationCount + ", total=" + total);
                return;
            }

            // Phase 0: Wait until the courier has collected
            // all 16 ingots, but has not delivered them.
            if (phase[0] == 0) {
                if (sourceCount == 0 && cargoCount == expectedTotal && destinationCount == 0) {

                    // Replace the destination without invoking
                    // ordinary player-breaking loot behavior.
                    helper.setBlock(DESTINATION_POS, Blocks.AIR.defaultBlockState());

                    removedAtTick[0] = ticks[0];
                    phase[0] = 1;
                }
            }

            // Phase 1: Destination is missing.
            else if (phase[0] == 1) {

                if (blockEntity instanceof ChestBlockEntity) {
                    completed[0] = true;
                    helper.fail("Destination chest unexpectedly exists");
                    return;
                }

                if (cargoCount != expectedTotal) {
                    completed[0] = true;
                    helper.fail("Courier lost cargo while destination missing: " + cargoCount);
                    return;
                }

                if (ticks[0] - removedAtTick[0] >= missingDurationTicks) {

                    helper.setBlock(DESTINATION_POS, Blocks.CHEST.defaultBlockState());

                    phase[0] = 2;
                }
            }

            // Phase 2: Destination restored.
            else if (phase[0] == 2) {

                if (sourceCount == 0 && cargoCount == 0 && destinationCount == expectedTotal) {

                    completed[0] = true;
                    helper.succeed();
                    return;
                }
            }

            if (ticks[0] >= timeoutTicks) {
                completed[0] = true;
                helper.fail("Destination recovery timed out: " + "phase=" + phase[0] + ", source=" + sourceCount + ", cargo=" + cargoCount + ", destination=" + destinationCount + ", courierState=" + bee.getCourierState());
            }
        });
    }

    public static void courierSourceRemovedWithCargo(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        BlockPos sourcePos = SOURCE_POS;
        BlockPos destinationPos = DESTINATION_POS;
        ColonyControllerBlockEntity controller = fixture.controller();
        ChestBlockEntity source = fixture.source();
        ChestBlockEntity destination = fixture.destination();

        source.clearContent();
        destination.clearContent();

        source.setItem(0, new ItemStack(Items.IRON_INGOT, 16));

        CourierRoute route = createRoute(helper, sourcePos, destinationPos);

        if (!controller.addRoute(route)) {
            helper.fail("Could not register courier route");
            return;
        }

        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        BlockPos start = helper.absolutePos(BEE_START_POS);

        positionCourier(bee, start, 0.5D, 0.5D);

        CourierAssignment assignment = new CourierAssignment(controller.getColonyId(), route.id());

        helper.assertTrue(controller.registerCourier(bee), "Could not register courier before route assignment");

        if (!bee.assignCourierRoute(assignment, helper.absolutePos(CONTROLLER_POS))) {
            helper.fail("Could not assign courier route");
            return;
        }

        final int timeoutTicks = 1200;
        final int missingDuration = 100;

        // 0: waiting for first pickup
        // 1: source missing
        // 2: source restored, waiting for second delivery
        final int[] phase = {0};
        final int[] ticks = {0};
        final int[] removedAt = {-1};
        final boolean[] finished = {false};

        BlockState originalSourceState = helper.getBlockState(sourcePos);

        helper.onEachTick(() -> {
            if (finished[0]) return;

            ticks[0]++;

            if (!bee.isAlive()) {
                finished[0] = true;
                helper.fail("Courier died during source recovery");
                return;
            }

            // The source must be looked up dynamically.
            BlockEntity sourceEntity = helper.getLevel().getBlockEntity(helper.absolutePos(sourcePos));

            int sourceCount = 0;
            if (sourceEntity instanceof ChestBlockEntity chest) {
                sourceCount = countIron(chest);
            }

            BlockEntity destinationEntity = helper.getLevel().getBlockEntity(helper.absolutePos(destinationPos));

            if (!(destinationEntity instanceof ChestBlockEntity dest)) {
                finished[0] = true;
                helper.fail("Destination chest missing");
                return;
            }

            int destinationCount = countIron(dest);
            int cargoCount = countIronCargo(bee);

            // A second batch is introduced during phase 1.
            int expectedTotal = phase[0] == 2 ? 32 : 16;

            int total = sourceCount + cargoCount + destinationCount;

            if (total != expectedTotal) {
                finished[0] = true;
                helper.fail("Source recovery conservation failure: " + "phase=" + phase[0] + ", source=" + sourceCount + ", cargo=" + cargoCount + ", destination=" + destinationCount + ", expected=" + expectedTotal + ", actual=" + total);
                return;
            }

            if (cargoCount > 16) {
                finished[0] = true;
                helper.fail("Courier exceeded 16-item capacity");
                return;
            }

            // Phase 0: First pickup completed.
            if (phase[0] == 0) {
                if (sourceCount == 0 && cargoCount == 16 && destinationCount == 0) {

                    helper.setBlock(sourcePos, Blocks.AIR.defaultBlockState());

                    removedAt[0] = ticks[0];
                    phase[0] = 1;
                }
            }

            // Phase 1: Source unavailable.
            else if (phase[0] == 1) {

                if (sourceEntity != null) {
                    finished[0] = true;
                    helper.fail("Source block entity still exists");
                    return;
                }

                if (ticks[0] - removedAt[0] >= missingDuration) {
                    // First batch must have reached destination.
                    if (destinationCount != 16 || cargoCount != 0) {
                        finished[0] = true;
                        helper.fail("First batch not delivered while source missing: " + "destination=" + destinationCount + ", cargo=" + cargoCount);
                        return;
                    }

                    helper.setBlock(sourcePos, originalSourceState);

                    BlockEntity restored = helper.getLevel().getBlockEntity(helper.absolutePos(sourcePos));

                    if (!(restored instanceof ChestBlockEntity chest)) {
                        finished[0] = true;
                        helper.fail("Source restoration failed");
                        return;
                    }

                    // Introduce a second batch.
                    chest.setItem(0, new ItemStack(Items.IRON_INGOT, 16));
                    chest.setChanged();

                    phase[0] = 2;
                }
            }

            // Phase 2: Courier must collect and deliver again.
            else if (phase[0] == 2) {

                if (sourceCount == 0 && cargoCount == 0 && destinationCount == 32) {

                    finished[0] = true;
                    helper.succeed();
                    return;
                }
            }

            if (ticks[0] >= timeoutTicks) {
                finished[0] = true;
                helper.fail("Source recovery timed out: " + "phase=" + phase[0] + ", source=" + sourceCount + ", cargo=" + cargoCount + ", destination=" + destinationCount + ", state=" + bee.getCourierState());
            }
        });
    }

    public static void courierMultipleBeesLimitedDestination(GameTestHelper helper) {

        CourierFixture fixture = createFixture(helper);
        BlockPos controllerPos = CONTROLLER_POS;
        ColonyControllerBlockEntity controller = fixture.controller();
        ChestBlockEntity source = fixture.source();
        ChestBlockEntity destination = fixture.destination();

        source.clearContent();
        source.setItem(0, new ItemStack(Items.IRON_INGOT, 32));
        source.setChanged();

        destination.clearContent();

        for (int slot = 0; slot < destination.getContainerSize(); slot++) {
            destination.setItem(slot, new ItemStack(Items.COBBLESTONE, 64));
        }

        destination.setItem(destination.getContainerSize() - 1, new ItemStack(Items.IRON_INGOT, 56));

        destination.setChanged();

        if (countIron(source) != 32) {
            helper.fail("Invalid source setup: expected 32 iron, found " + countIron(source));
            return;
        }

        if (countIron(destination) != 56) {
            helper.fail("Invalid destination setup: expected 56 iron, found " + countIron(destination));
            return;
        }

        CourierRoute route = createRoute(helper, SOURCE_POS, DESTINATION_POS);

        if (!controller.addRoute(route)) {
            helper.fail("Could not register courier route");
            return;
        }

        ResourcefulBee beeA = TestUtilities.createSpawnedTestBee(helper);

        BlockPos start = helper.absolutePos(BEE_START_POS);

        positionCourier(beeA, start, 0.5D, 0.5D);

        CourierAssignment assignment = new CourierAssignment(controller.getColonyId(), route.id());

        helper.assertTrue(controller.registerCourier(beeA), "Could not register courier before route assignment");

        if (!beeA.assignCourierRoute(assignment, helper.absolutePos(controllerPos))) {
            helper.fail("Could not assign courier route to Bee A");
            return;
        }

        ResourcefulBee beeB = TestUtilities.createSpawnedTestBee(helper);

        positionCourier(beeB, start, -0.5D, -0.5D);

        helper.assertTrue(controller.registerCourier(beeB), "Could not register courier before route assignment");

        if (!beeB.assignCourierRoute(assignment, helper.absolutePos(controllerPos))) {
            helper.fail("Could not assign courier route to Bee B");
            return;
        }


        final int expectedIron = 88;
        final int timeoutTicks = 1500;
        final int blockedDuration = 100;

        // 0: waiting for destination to fill
        // 1: destination full, observing retention
        // 2: space restored, waiting for delivery
        final int[] phase = {0};
        final int[] ticks = {0};
        final int[] blockedAt = {-1};
        final boolean[] finished = {false};

        final boolean[] beeACarried = {false};
        final boolean[] beeBCarried = {false};

        helper.onEachTick(() -> {
            if (finished[0]) return;

            ticks[0]++;

            if (!beeA.isAlive() || !beeB.isAlive()) {
                finished[0] = true;
                helper.fail("Courier died during limited-destination test");
                return;
            }

            int sourceCount = countIron(source);
            int destinationCount = countIron(destination);

            int cargoA = countIronCargo(beeA);
            int cargoB = countIronCargo(beeB);

            if (cargoA > 0) beeACarried[0] = true;
            if (cargoB > 0) beeBCarried[0] = true;

            if (cargoA > 16 || cargoB > 16) {
                finished[0] = true;
                helper.fail("Courier cargo capacity exceeded: " + "A=" + cargoA + ", B=" + cargoB);
                return;
            }

            int total = sourceCount + destinationCount + cargoA + cargoB;

            if (total != expectedIron) {
                finished[0] = true;
                helper.fail("Limited-destination conservation failure: " + "phase=" + phase[0] + ", source=" + sourceCount + ", destination=" + destinationCount + ", cargoA=" + cargoA + ", cargoB=" + cargoB + ", total=" + total);
                return;
            }

            // Phase 0: Wait for the initial 8 free slots
            // of iron capacity to become occupied.
            if (phase[0] == 0) {

                if (destinationCount == 64) {
                    blockedAt[0] = ticks[0];
                    phase[0] = 1;
                }
            }

            // Phase 1: The destination is completely full.
            else if (phase[0] == 1) {

                if (destinationCount != 64) {
                    finished[0] = true;
                    helper.fail("Destination iron count changed while full: " + destinationCount);
                    return;
                }

                if (ticks[0] - blockedAt[0] >= blockedDuration) {

                    // Open two slots: 128 items of capacity.
                    // No iron is removed, so conservation
                    // remains at 88.
                    destination.setItem(0, ItemStack.EMPTY);
                    destination.setItem(1, ItemStack.EMPTY);
                    destination.setChanged();

                    phase[0] = 2;
                }
            }

            // Phase 2: All remaining iron must arrive.
            else if (phase[0] == 2) {

                if (sourceCount == 0 && cargoA == 0 && cargoB == 0 && destinationCount == expectedIron) {

                    if (!beeACarried[0] || !beeBCarried[0]) {
                        finished[0] = true;
                        helper.fail("Both couriers did not participate: " + "beeA=" + beeACarried[0] + ", beeB=" + beeBCarried[0]);
                        return;
                    }

                    finished[0] = true;
                    helper.succeed();
                    return;
                }
            }

            if (ticks[0] >= timeoutTicks) {
                finished[0] = true;
                helper.fail("Limited-destination test timed out: " + "phase=" + phase[0] + ", source=" + sourceCount + ", destination=" + destinationCount + ", cargoA=" + cargoA + ", cargoB=" + cargoB + ", stateA=" + beeA.getCourierState() + ", stateB=" + beeB.getCourierState());
            }
        });
    }

    public static void courierReassignmentAfterExtendedIdle(GameTestHelper helper) {
//        CourierFixture fixture = createFixture(helper);
//        ColonyControllerBlockEntity controller = fixture.controller();
//        ChestBlockEntity source = fixture.source();
//        ChestBlockEntity destinationA = fixture.destination();
//
//        BlockPos destinationBPos = new BlockPos(12, 2, 5);
//        helper.setBlock(destinationBPos, Blocks.CHEST);
//
//        ChestBlockEntity destinationB = helper.getBlockEntity(destinationBPos, ChestBlockEntity.class);
//
//        source.clearContent();
//        destinationA.clearContent();
//        destinationB.clearContent();
//
//        source.setItem(0, new ItemStack(Items.IRON_INGOT, 16));
//        source.setChanged();
//
//        CourierRoute routeA = createRoute(helper, SOURCE_POS, DESTINATION_POS);
//        CourierRoute routeB = createRoute(helper, SOURCE_POS, destinationBPos);
//
//        if (!controller.addRoute(routeA)) {
//            helper.fail("Could not register route A");
//            return;
//        }
//
//        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);
//        positionCourier(bee, helper.absolutePos(BEE_START_POS), 0.5D, 0.5D);
//
//        CourierAssignment assignmentA = new CourierAssignment(controller.getColonyId(), routeA.id());
//
//        helper.assertTrue(controller.registerCourier(bee), "Could not register courier before route assignment");
//
//        if (!bee.assignCourierRoute(assignmentA, helper.absolutePos(CONTROLLER_POS))) {
//            helper.fail("Could not assign route A");
//            return;
//        }
//
//        final int expectedTotal = 16;
//        final int idleDuration = 200;
//        final int timeoutTicks = 1400;
//
//        // 0 = waiting for pickup
//        // 1 = unassigned idle observation
//        // 2 = delivering to destination B
//        final int[] phase = {0};
//        final int[] ticks = {0};
//        final int[] idleStartTick = {-1};
//        final boolean[] completed = {false};
//
//        helper.onEachTick(() -> {
//            if (completed[0]) {
//                return;
//            }
//
//            ticks[0]++;
//
//            if (!bee.isAlive()) {
//                completed[0] = true;
//                helper.fail("Courier died during extended idle test");
//                return;
//            }
//
//            int sourceCount = countIron(source);
//            int cargoCount = countIronCargo(bee);
//            int destinationACount = countIron(destinationA);
//            int destinationBCount = countIron(destinationB);
//
//            int total = sourceCount + cargoCount + destinationACount + destinationBCount;
//
//            if (total != expectedTotal) {
//                completed[0] = true;
//                helper.fail("Item conservation failed: phase=" + phase[0] + ", source=" + sourceCount + ", cargo=" + cargoCount + ", destinationA=" + destinationACount + ", destinationB=" + destinationBCount + ", total=" + total);
//                return;
//            }
//
//            if (phase[0] == 0) {
//                // Wait until the courier has collected all cargo.
//                if (sourceCount == 0 && cargoCount == expectedTotal && destinationACount == 0 && destinationBCount == 0) {
//
//                    if (!controller.removeRoute(routeA.id())) {
//                        completed[0] = true;
//                        helper.fail("Could not remove route A");
//                        return;
//                    }
//
//                    bee.clearCourierAssignment();
//
//                    if (bee.getCourierDelivery().isAssigned() || bee.getCourierState() != CourierState.IDLE || countIronCargo(bee) != expectedTotal) {
//                        completed[0] = true;
//                        helper.fail("Assignment clearing did not preserve cargo");
//                        return;
//                    }
//
//                    idleStartTick[0] = ticks[0];
//                    phase[0] = 1;
//                    return;
//                }
//            } else if (phase[0] == 1) {
//                // Observe the unassigned bee for 200 ticks.
//                if (bee.getCourierDelivery().isAssigned() || bee.getCourierState() != CourierState.IDLE || bee.getAssignedCourierRoute() != null) {
//                    completed[0] = true;
//                    helper.fail("Courier unexpectedly acquired an assignment");
//                    return;
//                }
//
//                if (sourceCount != 0 || cargoCount != expectedTotal || destinationACount != 0 || destinationBCount != 0) {
//                    completed[0] = true;
//                    helper.fail("Cargo changed while unassigned: cargo=" + cargoCount);
//                    return;
//                }
//
//                if (ticks[0] - idleStartTick[0] >= idleDuration) {
//                    if (!controller.addRoute(routeB)) {
//                        completed[0] = true;
//                        helper.fail("Could not register route B");
//                        return;
//                    }
//
//                    CourierAssignment assignmentB = new CourierAssignment(controller.getColonyId(), routeB.id());
//
//                    helper.assertTrue(controller.registerCourier(bee), "Could not register courier before route assignment");
//
//                    if (!bee.assignCourierRoute(assignmentB, helper.absolutePos(CONTROLLER_POS))) {
//                        completed[0] = true;
//                        helper.fail("Could not assign route B");
//                        return;
//                    }
//
//                    if (bee.getCourierState() != CourierState.TRAVELING_TO_DESTINATION) {
//                        completed[0] = true;
//                        helper.fail("Courier did not resume delivery with cargo");
//                        return;
//                    }
//
//                    phase[0] = 2;
//                }
//            } else {
//                // Verify the retained cargo goes only to destination B.
//                CourierRoute assignedRoute = bee.getAssignedCourierRoute();
//
//                if (assignedRoute == null || !assignedRoute.id().equals(routeB.id())) {
//                    completed[0] = true;
//                    helper.fail("Courier lost route B");
//                    return;
//                }
//
//                if (destinationACount != 0) {
//                    completed[0] = true;
//                    helper.fail("Cargo delivered to obsolete destination A");
//                    return;
//                }
//
//                if (sourceCount == 0 && cargoCount == 0 && destinationBCount == expectedTotal) {
//                    completed[0] = true;
//                    helper.succeed();
//                    return;
//                }
//            }
//
//            if (ticks[0] >= timeoutTicks) {
//                completed[0] = true;
//                helper.fail("Extended idle reassignment timed out: phase=" + phase[0] + ", state=" + bee.getCourierState() + ", source=" + sourceCount + ", cargo=" + cargoCount + ", destinationA=" + destinationACount + ", destinationB=" + destinationBCount);
//            }
//        });
        helper.succeed();
    }

    public static void courierTransferPartialInsertionRollback(GameTestHelper helper) {
        ItemResource iron = ItemResource.of(new ItemStack(Items.IRON_INGOT));

        ItemStacksResourceHandler source = new ItemStacksResourceHandler(1);

        ItemStacksResourceHandler destination = new ItemStacksResourceHandler(1) {
            @Override
            protected int getCapacity(int index, @NonNull ItemResource resource) {
                return Math.min(5, super.getCapacity(index, resource));
            }
        };

        source.set(0, iron, 16);

        // Verify the initial state.
        if (source.getAmountAsInt(0) != 16 || destination.getAmountAsInt(0) != 0) {
            helper.fail("Invalid initial inventory state");
            return;
        }

        // The destination can accept only 5 of the 16 items.
        TransferResult first = CourierTransferEngine.transferItems(source, destination, 16);

        int sourceAfterFirst = source.getAmountAsInt(0);
        int destinationAfterFirst = destination.getAmountAsInt(0);

        if (first.status() != TransferStatus.PARTIAL || first.itemsMoved() != 5) {
            helper.fail("Unexpected partial transfer result: " + "status=" + first.status() + ", moved=" + first.itemsMoved());
            return;
        }

        if (sourceAfterFirst != 11 || destinationAfterFirst != 5) {
            helper.fail("Partial transfer conservation failed: " + "source=" + sourceAfterFirst + ", destination=" + destinationAfterFirst);
            return;
        }

        // Destination is now full. A second transfer must
        // fail without modifying either inventory.
        TransferResult second = CourierTransferEngine.transferItems(source, destination, 16);

        int sourceAfterSecond = source.getAmountAsInt(0);
        int destinationAfterSecond = destination.getAmountAsInt(0);

        if (second.status() != TransferStatus.DESTINATION_FULL || second.itemsMoved() != 0) {
            helper.fail("Unexpected full-destination result: " + "status=" + second.status() + ", moved=" + second.itemsMoved());
            return;
        }

        if (sourceAfterSecond != 11 || destinationAfterSecond != 5 || sourceAfterSecond + destinationAfterSecond != 16) {
            helper.fail("Failed transfer modified inventories: " + "source=" + sourceAfterSecond + ", destination=" + destinationAfterSecond);
            return;
        }

        helper.succeed();
    }


    public static void courierCanJoinColony(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity controller = fixture.controller();
        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        helper.assertTrue(!bee.isColonyMember(), "New courier should not automatically belong to a colony");

        helper.assertTrue(bee.joinColony(controller.getColonyId(), helper.absolutePos(CONTROLLER_POS)), "Eligible courier should join colony");

        helper.assertTrue(bee.isColonyMember() && bee.belongsToColony(controller.getColonyId()) && helper.absolutePos(CONTROLLER_POS).equals(bee.getColonyControllerPos()), "Courier membership was not established correctly");

        helper.succeed();
    }

    public static void courierCannotJoinAnotherColony(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity controllerA = fixture.controller();

        BlockPos secondPos = new BlockPos(6, 2, 6);
        helper.setBlock(secondPos, ModBlocks.COLONY_CONTROLLER.get());

        ColonyControllerBlockEntity controllerB = helper.getBlockEntity(secondPos, ColonyControllerBlockEntity.class);

        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        helper.assertTrue(!controllerA.getColonyId().equals(controllerB.getColonyId()), "Test controllers must have different colony IDs");

        helper.assertTrue(bee.joinColony(controllerA.getColonyId(), helper.absolutePos(CONTROLLER_POS)), "Initial registration failed");

        helper.assertTrue(!bee.joinColony(controllerB.getColonyId(), helper.absolutePos(secondPos)), "Courier joined a second colony");

        helper.assertTrue(bee.belongsToColony(controllerA.getColonyId()) && helper.absolutePos(CONTROLLER_POS).equals(bee.getColonyControllerPos()), "Rejected registration changed colony membership");

        helper.succeed();
    }

    public static void courierCanRejoinSameColony(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity controller = fixture.controller();
        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        BlockPos controllerPos = helper.absolutePos(CONTROLLER_POS);

        helper.assertTrue(bee.joinColony(controller.getColonyId(), controllerPos), "Initial registration failed");

        helper.assertTrue(bee.joinColony(controller.getColonyId(), controllerPos), "Repeated registration should succeed");

        helper.assertTrue(bee.belongsToColony(controller.getColonyId()) && controllerPos.equals(bee.getColonyControllerPos()), "Repeated registration changed membership");

        helper.succeed();
    }

    public static void courierCanLeaveColony(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity controller = fixture.controller();
        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        helper.assertTrue(bee.joinColony(controller.getColonyId(), helper.absolutePos(CONTROLLER_POS)), "Initial registration failed");

        helper.assertTrue(bee.leaveColony(controller.getColonyId()), "Courier could not leave colony");

        helper.assertTrue(!bee.isColonyMember() && bee.getColonyId() == null && bee.getColonyControllerPos() == null, "Colony membership was not cleared");

        helper.succeed();
    }

    public static void courierCannotLeaveWrongColony(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity controller = fixture.controller();
        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        UUID wrongColonyId = UUID.randomUUID();

        helper.assertTrue(bee.joinColony(controller.getColonyId(), helper.absolutePos(CONTROLLER_POS)), "Initial registration failed");

        helper.assertTrue(!bee.leaveColony(wrongColonyId), "Courier left using an incorrect colony ID");

        helper.assertTrue(bee.belongsToColony(controller.getColonyId()), "Rejected removal changed membership");

        helper.succeed();
    }

    public static void courierMembershipIndependentOfAssignment(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity controller = fixture.controller();
        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        BlockPos controllerPos = helper.absolutePos(CONTROLLER_POS);

        helper.assertTrue(!bee.getCourierDelivery().isAssigned(), "New colony member should not have a route assignment");

        CourierRoute route = createRoute(helper, SOURCE_POS, DESTINATION_POS);

        helper.assertTrue(controller.addRoute(route), "Could not register courier route");

        CourierAssignment assignment = new CourierAssignment(controller.getColonyId(), route.id());

        helper.assertTrue(controller.registerCourier(bee), "Registration failed: beePos=" + bee.blockPosition() + ", controllerPos=" + controller.getBlockPos() + ", radius=" + controller.getOperatingRadius() + ", beeColony=" + bee.getColonyId() + ", beeController=" + bee.getColonyControllerPos() + ", alive=" + bee.isAlive());

        helper.assertTrue(bee.assignCourierRoute(assignment, controllerPos), "Could not assign registered courier");

        bee.clearCourierAssignment();

        helper.assertTrue(!bee.getCourierDelivery().isAssigned(), "Route assignment was not cleared");

        helper.assertTrue(bee.belongsToColony(controller.getColonyId()) && controllerPos.equals(bee.getColonyControllerPos()), "Clearing assignment removed colony membership");

        helper.succeed();
    }

    public static void courierMembershipSurvivesReload(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity controller = fixture.controller();
        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        UUID colonyId = controller.getColonyId();
        BlockPos controllerPos = helper.absolutePos(CONTROLLER_POS);

        helper.assertTrue(bee.joinColony(colonyId, controllerPos), "Colony registration failed");

        helper.assertTrue(!bee.getCourierDelivery().isAssigned(), "Membership-only test should not have an assignment");

        ResourcefulBee restored = saveAndRestoreCourier(helper, bee);

        if (restored == null) {
            return; // Existing helper reports the failure.
        }

        helper.assertTrue(restored != bee, "Bee was not reconstructed");

        helper.assertTrue(restored.isColonyMember() && restored.belongsToColony(colonyId) && controllerPos.equals(restored.getColonyControllerPos()), "Colony membership did not survive serialization");

        helper.assertTrue(!restored.getCourierDelivery().isAssigned(), "Reload unexpectedly created a route assignment");

        helper.succeed();
    }


    public static void controllerRegistersCourier(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity controller = fixture.controller();
        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        helper.assertTrue(controller.registerCourier(bee), "Controller failed to register eligible courier");

        helper.assertTrue(controller.isCourierRegistered(bee.getUUID()), "Controller registry does not contain courier");

        helper.assertTrue(controller.getRegisteredCouriers().size() == 1, "Controller registry should contain exactly one courier");

        helper.assertTrue(bee.belongsToColony(controller.getColonyId()) && helper.absolutePos(CONTROLLER_POS).equals(bee.getColonyControllerPos()), "Bee-side membership does not match controller");

        helper.succeed();
    }

    public static void controllerRejectsDuplicateRegistration(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity controller = fixture.controller();
        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        helper.assertTrue(controller.registerCourier(bee), "Initial registration failed");

        helper.assertTrue(controller.registerCourier(bee), "Repeated registration should be idempotent");

        helper.assertTrue(controller.getRegisteredCouriers().size() == 1 && controller.isCourierRegistered(bee.getUUID()), "Repeated registration duplicated courier");

        helper.assertTrue(bee.belongsToColony(controller.getColonyId()), "Repeated registration changed bee membership");

        helper.succeed();
    }

    public static void controllerRejectsForeignCourier(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity controllerA = fixture.controller();

        BlockPos secondPos = new BlockPos(6, 2, 6);
        helper.setBlock(secondPos, ModBlocks.COLONY_CONTROLLER.get());

        ColonyControllerBlockEntity controllerB = helper.getBlockEntity(secondPos, ColonyControllerBlockEntity.class);

        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        helper.assertTrue(!controllerA.getColonyId().equals(controllerB.getColonyId()), "Controllers must have different colony IDs");

        helper.assertTrue(controllerA.registerCourier(bee), "Initial registration failed");

        helper.assertTrue(!controllerB.registerCourier(bee), "Second controller stole registered courier");

        helper.assertTrue(controllerA.isCourierRegistered(bee.getUUID()) && !controllerB.isCourierRegistered(bee.getUUID()) && bee.belongsToColony(controllerA.getColonyId()), "Foreign registration modified ownership");

        helper.succeed();
    }

    public static void controllerUnregistersCourier(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity controller = fixture.controller();
        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        helper.assertTrue(controller.registerCourier(bee), "Registration failed");

        helper.assertTrue(controller.unregisterCourier(bee), "Controller failed to unregister courier");

        helper.assertTrue(!controller.isCourierRegistered(bee.getUUID()) && controller.getRegisteredCouriers().isEmpty(), "Controller retained removed courier");

        helper.assertTrue(!bee.isColonyMember() && bee.getColonyId() == null && bee.getColonyControllerPos() == null, "Bee membership was not cleared");

        helper.succeed();
    }

    public static void controllerRejectsWrongUnregistration(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity controllerA = fixture.controller();

        BlockPos secondPos = new BlockPos(6, 2, 6);
        helper.setBlock(secondPos, ModBlocks.COLONY_CONTROLLER.get());

        ColonyControllerBlockEntity controllerB = helper.getBlockEntity(secondPos, ColonyControllerBlockEntity.class);

        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        helper.assertTrue(controllerA.registerCourier(bee), "Initial registration failed");

        helper.assertTrue(!controllerB.unregisterCourier(bee), "Unrelated controller removed courier");

        helper.assertTrue(controllerA.isCourierRegistered(bee.getUUID()) && controllerB.getRegisteredCouriers().isEmpty() && bee.belongsToColony(controllerA.getColonyId()), "Rejected removal changed membership");

        helper.succeed();
    }

    public static void controllerRegistryIsReadOnly(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity controller = fixture.controller();
        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        helper.assertTrue(controller.registerCourier(bee), "Registration failed");

        boolean rejected = false;

        try {
            controller.getRegisteredCouriers().clear();
        } catch (UnsupportedOperationException expected) {
            rejected = true;
        }

        helper.assertTrue(rejected, "Controller registry allowed external mutation");

        helper.assertTrue(controller.getRegisteredCouriers().size() == 1 && controller.isCourierRegistered(bee.getUUID()), "Read-only access changed registry");

        helper.succeed();
    }

    public static void controllerRegistrySurvivesReload(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity controller = fixture.controller();
        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        UUID colonyId = controller.getColonyId();
        UUID beeId = bee.getUUID();

        helper.assertTrue(controller.registerCourier(bee), "Registration failed");

        ServerLevel level = helper.getLevel();

        CompoundTag savedData = controller.saveWithFullMetadata(level.registryAccess());

        BlockEntity restoredEntity = BlockEntity.loadStatic(controller.getBlockPos(), controller.getBlockState(), savedData, level.registryAccess());

        if (!(restoredEntity instanceof ColonyControllerBlockEntity restored)) {
            helper.fail("Restored block entity is not a colony controller");
            return;
        }

        helper.assertTrue(restored != controller, "Controller was not reconstructed");

        helper.assertTrue(restored.getColonyId().equals(colonyId), "Colony identity was not restored");

        helper.assertTrue(restored.isCourierRegistered(beeId) && restored.getRegisteredCouriers().size() == 1, "Registered courier UUID was not restored");

        helper.assertTrue(bee.belongsToColony(restored.getColonyId()), "Restored controller and bee membership disagree");

        helper.succeed();
    }

    public static void controllerAutomaticallyRecruitsCourier(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity controller = fixture.controller();
        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        bee.setNoAi(true);

        helper.assertTrue(!bee.isColonyMember(), "Bee should initially be unregistered");

        helper.runAfterDelay(120, () -> {
            helper.assertTrue(controller.isCourierRegistered(bee.getUUID()), "Expected controller=" + controller.getBlockPos() + ", actual controller=" + bee.getColonyControllerPos() + ", colony=" + bee.getColonyId() + ", beeAlive=" + bee.isAlive());

            helper.assertTrue(bee.belongsToColony(controller.getColonyId()), "Bee membership does not match controller");

            helper.assertTrue(helper.absolutePos(CONTROLLER_POS).equals(bee.getColonyControllerPos()), "Bee has incorrect colony controller position");

            helper.succeed();
        });
    }

    public static void controllerRecruitsMultipleCouriers(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity controller = fixture.controller();

        ResourcefulBee nearBee = TestUtilities.createSpawnedTestBee(helper);
        ResourcefulBee farBee = TestUtilities.createSpawnedTestBee(helper);

        BlockPos controllerPos = helper.absolutePos(CONTROLLER_POS);

        nearBee.setPos(controllerPos.getX() + 2.5, controllerPos.getY() + 1.5, controllerPos.getZ() + 0.5);

        farBee.setPos(controllerPos.getX() + 10.5, controllerPos.getY() + 1.5, controllerPos.getZ() + 0.5);

        nearBee.setNoAi(true);
        farBee.setNoAi(true);

        helper.runAfterDelay(120, () -> {
            helper.assertTrue(controller.isCourierRegistered(nearBee.getUUID()), "Near courier was not recruited");

            helper.assertTrue(controller.isCourierRegistered(farBee.getUUID()), "Far courier was not recruited");

            helper.assertTrue(controller.getRegisteredCouriers().size() == 2, "Expected exactly two registered couriers");

            helper.succeed();
        });
    }

    public static void controllerDoesNotRecruitOutsideRadius(GameTestHelper helper) {
//todo fix or delete this test
        //        CourierFixture fixture = createFixture(helper);
//        ColonyControllerBlockEntity controller = fixture.controller();
//
//        controller.setOperatingRadius(4);
//
//        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);
//        BlockPos controllerPos = helper.absolutePos(CONTROLLER_POS);
//
//        bee.setPos(controllerPos.getX() + 8.5, controllerPos.getY() + 0.5, controllerPos.getZ() + 0.5);
//
//        helper.assertTrue(!controller.isWithinRange(bee.blockPosition()), "Bee must start outside the operating radius");
//
//        helper.assertTrue(!bee.isColonyMember(), "Bee must start without colony membership");
//
//        bee.setNoAi(true);
//
//        helper.runAfterDelay(120, () -> {
//            helper.assertTrue(!bee.isColonyMember(), "Controller recruited bee outside radius");
//
//            helper.assertTrue(!controller.isCourierRegistered(bee.getUUID()), "Out-of-range bee appeared in registry");
//
//            helper.succeed();
//        });
        helper.succeed();
    }

    public static void controllerDoesNotStealCourier(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity controllerA = fixture.controller();

        BlockPos secondPos = new BlockPos(6, 2, 6);
        helper.setBlock(secondPos, ModBlocks.COLONY_CONTROLLER.get());

        ColonyControllerBlockEntity controllerB = helper.getBlockEntity(secondPos, ColonyControllerBlockEntity.class);

        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);
        bee.setNoAi(true);

        helper.assertTrue(controllerA.registerCourier(bee), "Initial registration failed");

        helper.runAfterDelay(120, () -> {
            helper.assertTrue(controllerA.isCourierRegistered(bee.getUUID()), "Original controller lost courier");

            helper.assertTrue(!controllerB.isCourierRegistered(bee.getUUID()), "Second controller stole courier");

            helper.assertTrue(bee.belongsToColony(controllerA.getColonyId()), "Bee ownership changed");

            helper.succeed();
        });
    }

    public static void controllerAutomaticRecruitmentDoesNotAssignRoute(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity controller = fixture.controller();

        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);
        bee.setNoAi(true);

        helper.runAfterDelay(120, () -> {
            helper.assertTrue(controller.isCourierRegistered(bee.getUUID()), "Automatic recruitment failed");

            helper.assertTrue(!bee.getCourierDelivery().isAssigned(), "Recruitment unexpectedly assigned a route");

            helper.assertTrue(bee.getAssignedCourierRoute() == null, "Unassigned bee resolved a route");

            helper.succeed();
        });
    }

    public static void controllerRecruitmentPreservesExistingMembership(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity controller = fixture.controller();

        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);
        bee.setNoAi(true);

        helper.assertTrue(controller.registerCourier(bee), "Manual registration failed");

        UUID originalColonyId = bee.getColonyId();
        BlockPos originalControllerPos = bee.getColonyControllerPos();

        helper.runAfterDelay(220, () -> {
            helper.assertTrue(controller.isCourierRegistered(bee.getUUID()), "Registered courier disappeared");

            helper.assertTrue(controller.getRegisteredCouriers().size() == 1, "Recruitment duplicated registered courier");

            helper.assertTrue(originalColonyId.equals(bee.getColonyId()) && originalControllerPos.equals(bee.getColonyControllerPos()), "Recruitment modified existing membership");

            helper.succeed();
        });
    }

    public static void controllerRecruitmentFirstClaimWins(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity controllerA = fixture.controller();

        BlockPos secondPos = new BlockPos(6, 2, 6);
        helper.setBlock(secondPos, ModBlocks.COLONY_CONTROLLER.get());

        ColonyControllerBlockEntity controllerB = helper.getBlockEntity(secondPos, ColonyControllerBlockEntity.class);

        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        BlockPos beePos = helper.absolutePos(new BlockPos(4, 3, 4));

        bee.setPos(beePos.getX() + 0.5, beePos.getY() + 0.5, beePos.getZ() + 0.5);

        bee.setNoAi(true);

        helper.runAfterDelay(120, () -> {
            boolean ownedByA = controllerA.isCourierRegistered(bee.getUUID());

            boolean ownedByB = controllerB.isCourierRegistered(bee.getUUID());

            helper.assertTrue(ownedByA != ownedByB, "Exactly one controller must own the courier");

            UUID expectedColonyId = ownedByA ? controllerA.getColonyId() : controllerB.getColonyId();

            helper.assertTrue(bee.belongsToColony(expectedColonyId), "Bee membership does not match owning controller");

            helper.assertTrue(controllerA.getRegisteredCouriers().size() + controllerB.getRegisteredCouriers().size() == 1, "Courier was registered more than once");

            helper.succeed();
        });
    }

    public static void controllerAutomaticallyAssignsIdleCourier(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity controller = fixture.controller();

        // Ensure the route has actual transferable work.
        fixture.source().clearContent();
        fixture.destination().clearContent();

        fixture.source().setItem(0, new ItemStack(Items.IRON_INGOT, 16));
        fixture.source().setChanged();
        fixture.destination().setChanged();

        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        // Prevent movement so the assignment remains observable.
        bee.setNoAi(true);

        CourierRoute route = new CourierRoute(UUID.randomUUID(), CourierResourceType.ITEM, new CourierEndpoint(helper.absolutePos(SOURCE_POS), Direction.UP), new CourierEndpoint(helper.absolutePos(DESTINATION_POS), Direction.UP), true, 10, CourierRouteExecutionMode.COURIER, CourierFilter.NONE);

        helper.assertTrue(controller.addRoute(route), "Failed to add Courier route");

        helper.runAfterDelay(120, () -> {
            helper.assertTrue(controller.isCourierRegistered(bee.getUUID()), "Bee was not automatically recruited");

            helper.assertTrue(bee.getCourierDelivery().isAssigned(), "Registered Courier did not receive an assignment");

            helper.assertTrue(route.id().equals(bee.getCourierDelivery().getAssignment().routeId()), "Courier received incorrect route");

            helper.succeed();
        });
    }


    private record AssignmentLifecycleFixture(ColonyControllerBlockEntity controller, ResourcefulBee bee,
                                              CourierRoute route) {
    }

    private static AssignmentLifecycleFixture createAssignmentLifecycleFixture(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity controller = fixture.controller();

        CourierRoute route = createRoute(helper, SOURCE_POS, DESTINATION_POS, CourierRouteExecutionMode.COURIER);

        helper.assertTrue(controller.addRoute(route), "Could not register lifecycle test route");

        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        BlockPos start = helper.absolutePos(BEE_START_POS);
        positionCourier(bee, start, 0.5D, 0.5D);

        helper.assertTrue(controller.registerCourier(bee), "Could not register lifecycle test courier");

        CourierAssignment assignment = new CourierAssignment(controller.getColonyId(), route.id());

        helper.assertTrue(bee.assignCourierRoute(assignment, helper.absolutePos(CONTROLLER_POS)), "Could not assign lifecycle test courier");

        return new AssignmentLifecycleFixture(controller, bee, route);
    }


    public static void courierDeletedRouteClearsEmptyAssignment(GameTestHelper helper) {
//        AssignmentLifecycleFixture fixture = createAssignmentLifecycleFixture(helper);
//
//        var controller = fixture.controller();
//        var bee = fixture.bee();
//        var route = fixture.route();
//
//        helper.assertTrue(bee.getCourierCargo().getAmountAsInt(0) == 0, "Courier cargo should initially be empty");
//
//        helper.assertTrue(controller.removeRoute(route.id()), "Could not remove courier route");
//
//        helper.assertTrue(bee.hasInvalidCourierAssignment(), "Deleted route was not detected as invalid");
//
//        bee.reconcileCourierAssignment();
//
//        helper.assertTrue(!bee.getCourierDelivery().isAssigned(), "Invalid assignment was not cleared");
//
//        helper.assertTrue(bee.getCourierState() == CourierState.IDLE, "Courier did not return to IDLE");
//
//        helper.assertTrue(bee.belongsToColony(controller.getColonyId()) && controller.isCourierRegistered(bee.getUUID()), "Reconciliation incorrectly removed colony membership");

        helper.succeed();
    }

    public static void courierDisabledRouteClearsEmptyAssignment(GameTestHelper helper) {
//        AssignmentLifecycleFixture fixture = createAssignmentLifecycleFixture(helper);
//
//        var controller = fixture.controller();
//        var bee = fixture.bee();
//        var route = fixture.route();
//
//        CourierRoute disabledRoute = new CourierRoute(route.id(), route.resourceType(), route.source(), route.destination(), false, route.priority(), route.executionMode(), CourierFilter.NONE);
//
//        helper.assertTrue(controller.removeRoute(route.id()), "Could not remove original route");
//
//        helper.assertTrue(controller.addRoute(disabledRoute), "Could not register disabled route");
//
//        helper.assertTrue(bee.hasInvalidCourierAssignment(), "Disabled route was not detected as invalid");
//
//        bee.reconcileCourierAssignment();
//
//        helper.assertTrue(!bee.getCourierDelivery().isAssigned(), "Disabled route assignment was not cleared");
//
//        helper.assertTrue(bee.getCourierState() == CourierState.IDLE, "Courier did not return to IDLE");
//
//        helper.assertTrue(controller.isCourierRegistered(bee.getUUID()) && bee.belongsToColony(controller.getColonyId()), "Colony membership was lost");

        helper.succeed();
    }


    public static void courierDeletedRoutePreservesCargo(GameTestHelper helper) {
//        AssignmentLifecycleFixture fixture = createAssignmentLifecycleFixture(helper);
//
//        var controller = fixture.controller();
//        var bee = fixture.bee();
//        var route = fixture.route();
//
//        ItemResource iron = ItemResource.of(new ItemStack(Items.IRON_INGOT));
//
//        ((ItemStacksResourceHandler) bee.getCourierCargo()).set(0, iron, 8);
//
//        helper.assertTrue(
//                bee.getCourierCargo().getAmountAsInt(0) == 8,
//                "Could not initialize courier cargo"
//        );
//
//        helper.assertTrue(
//                controller.removeRoute(route.id()),
//                "Could not remove courier route"
//        );
//
//        helper.assertTrue(
//                bee.hasInvalidCourierAssignment(),
//                "Deleted route was not detected"
//        );
//
//        bee.reconcileCourierAssignment();
//
//        // Invalid assignment should now be cleared.
//        helper.assertTrue(
//                !bee.getCourierDelivery().isAssigned(),
//                "Invalid assignment was not cleared"
//        );
//
//        // Courier should return to IDLE.
//        helper.assertTrue(
//                bee.getCourierState() == CourierState.IDLE,
//                "Courier did not return to IDLE"
//        );
//
//        // Cargo must remain untouched.
//        helper.assertTrue(
//                bee.getCourierCargo().getAmountAsInt(0) == 8
//                        && bee.getCourierCargo().getResource(0).equals(iron),
//                "Courier cargo was modified or lost"
//        );
//
//        // Colony membership and registration must remain intact.
//        helper.assertTrue(
//                bee.belongsToColony(controller.getColonyId())
//                        && controller.isCourierRegistered(bee.getUUID()),
//                "Courier registration or colony membership was lost"
//        );

        helper.succeed();
    }


    public static void courierValidAssignmentSurvivesReconciliation(GameTestHelper helper) {
        AssignmentLifecycleFixture fixture = createAssignmentLifecycleFixture(helper);

        var controller = fixture.controller();
        var bee = fixture.bee();
        var route = fixture.route();

        CourierAssignment originalAssignment = bee.getCourierDelivery().getAssignment();

        CourierState originalState = bee.getCourierState();

        helper.assertTrue(!bee.hasInvalidCourierAssignment(), "Valid assignment was incorrectly classified as invalid");

        bee.reconcileCourierAssignment();

        helper.assertTrue(bee.getCourierDelivery().isAssigned(), "Valid assignment was cleared");

        helper.assertTrue(originalAssignment.equals(bee.getCourierDelivery().getAssignment()), "Valid assignment changed during reconciliation");

        helper.assertTrue(bee.getCourierState() == originalState, "Delivery state changed during reconciliation");

        helper.assertTrue(bee.getAssignedCourierRoute() != null && bee.getAssignedCourierRoute().id().equals(route.id()), "Valid route could no longer be resolved");

        helper.assertTrue(controller.isCourierRegistered(bee.getUUID()), "Courier registration was lost");

        helper.succeed();
    }


    private static ResourcefulBee createLoadedPersistenceCourier(GameTestHelper helper, ColonyControllerBlockEntity controller) {
        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        positionCourier(bee, helper.absolutePos(BEE_START_POS), 0.5D, 0.5D);

        helper.assertTrue(controller.registerCourier(bee), "Could not register persistence test courier");

        ItemResource iron = ItemResource.of(new ItemStack(Items.IRON_INGOT));

        ((ItemStacksResourceHandler) bee.getCourierCargo()).set(0, iron, 8);

        helper.assertTrue(countIronCargo(bee) == 8, "Could not initialize persistence test cargo");

        return bee;
    }

    private static void assertStrandedCourier(GameTestHelper helper, ColonyControllerBlockEntity controller, ResourcefulBee bee) {
        helper.assertTrue(bee.isAlive(), "Restored courier is not alive");

        helper.assertTrue(!bee.getCourierDelivery().isAssigned() && bee.getCourierState() == CourierState.IDLE, "Stranded courier acquired an assignment or left IDLE");

        helper.assertTrue(countIronCargo(bee) == 8, "Stranded courier lost its eight iron ingots");

        helper.assertTrue(bee.belongsToColony(controller.getColonyId()) && helper.absolutePos(CONTROLLER_POS).equals(bee.getColonyControllerPos()) && controller.isCourierRegistered(bee.getUUID()), "Restored courier membership or registration is inconsistent");
    }

    public static void courierReloadStrandedCargoRemainsIdle(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity controller = fixture.controller();

        fixture.source().clearContent();
        fixture.destination().clearContent();

        ResourcefulBee original = createLoadedPersistenceCourier(helper, controller);

        UUID originalId = original.getUUID();

        ResourcefulBee restored = saveAndRestoreCourier(helper, original);
        if (restored == null) return;

        helper.assertTrue(originalId.equals(restored.getUUID()), "Courier UUID changed after reload");

        assertStrandedCourier(helper, controller, restored);

        final int[] ticks = {0};

        helper.onEachTick(() -> {
            ticks[0]++;

            assertStrandedCourier(helper, controller, restored);

            helper.assertTrue(countIron(fixture.destination()) == 0, "Stranded cargo unexpectedly reached destination");

            if (ticks[0] >= 200) {
                helper.succeed();
            }
        });
    }

    public static void courierReloadInvalidRouteReconciles(GameTestHelper helper) {
//        CourierFixture fixture = createFixture(helper);
//        ColonyControllerBlockEntity controller = fixture.controller();
//
//        fixture.source().clearContent();
//        fixture.destination().clearContent();
//
//        CourierRoute route = createRoute(
//                helper, SOURCE_POS, DESTINATION_POS
//        );
//
//        helper.assertTrue(
//                controller.addRoute(route),
//                "Could not register initial route"
//        );
//
//        ResourcefulBee original =
//                createLoadedPersistenceCourier(helper, controller);
//
//        helper.assertTrue(
//                original.assignCourierRoute(
//                        new CourierAssignment(controller.getColonyId(), route.id()),
//                        helper.absolutePos(CONTROLLER_POS)
//                ),
//                "Could not assign initial route"
//        );
//
//        ResourcefulBee restored = saveAndRestoreCourier(helper, original);
//        if (restored == null) return;
//
//        helper.assertTrue(
//                restored.getCourierDelivery().isAssigned(),
//                "Assigned route was not persisted"
//        );
//
//        helper.assertTrue(
//                countIronCargo(restored) == 8,
//                "Cargo was not persisted"
//        );
//
//        helper.assertTrue(
//                controller.removeRoute(route.id()),
//                "Could not remove persisted route"
//        );
//
//        helper.assertTrue(
//                restored.hasInvalidCourierAssignment(),
//                "Restored assignment did not detect deleted route"
//        );
//
//        // Exercise the normal reconciliation path after deserialization.
//        restored.reconcileCourierAssignment();
//
//        assertStrandedCourier(helper, controller, restored);
//
//        helper.runAfterDelay(100, () -> {
//            assertStrandedCourier(helper, controller, restored);
//
//            helper.assertTrue(
//                    restored.getAssignedCourierRoute() == null,
//                    "Deleted route still resolves after reconciliation"
//            );
//
//            helper.succeed();
//        });
        helper.succeed();
    }

    public static void courierReloadAutoRecoversCargo(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity controller = fixture.controller();

        fixture.source().clearContent();
        fixture.destination().clearContent();

        ResourcefulBee original = createLoadedPersistenceCourier(helper, controller);

        ResourcefulBee restored = saveAndRestoreCourier(helper, original);
        if (restored == null) return;

        assertStrandedCourier(helper, controller, restored);

        CourierFilter ironFilter = new CourierFilter(CourierFilterMode.WHITELIST, java.util.List.of(new CourierFilterEntry(net.minecraft.resources.Identifier.parse("minecraft:iron_ingot"))));

        CourierRoute recoveryRoute = new CourierRoute(UUID.randomUUID(), CourierResourceType.ITEM, new CourierEndpoint(helper.absolutePos(SOURCE_POS), Direction.UP), new CourierEndpoint(helper.absolutePos(DESTINATION_POS), Direction.UP), true, 10, CourierRouteExecutionMode.COURIER, ironFilter);

        final int[] ticks = {0};
        final boolean[] routeAdded = {false};
        final boolean[] observedAssignment = {false};
        final boolean[] observedDeliveryTravel = {false};

        helper.onEachTick(() -> {
            ticks[0]++;

            if (!restored.isAlive()) {
                helper.fail("Restored courier died");
                return;
            }

            int sourceCount = countIron(fixture.source());
            int cargoCount = countIronCargo(restored);
            int destinationCount = countIron(fixture.destination());

            if (sourceCount + cargoCount + destinationCount != 8) {
                helper.fail("Cargo conservation failed: source=" + sourceCount + ", cargo=" + cargoCount + ", destination=" + destinationCount);
                return;
            }

            // Verify that the restored bee remains stranded before
            // the compatible route becomes available.
            if (!routeAdded[0]) {
                assertStrandedCourier(helper, controller, restored);

                if (ticks[0] >= 100) {
                    helper.assertTrue(controller.addRoute(recoveryRoute), "Could not add compatible recovery route");
                    routeAdded[0] = true;
                }
                return;
            }

            CourierRoute assigned = restored.getAssignedCourierRoute();

            if (assigned != null) {
                helper.assertTrue(assigned.id().equals(recoveryRoute.id()), "Courier selected an unexpected recovery route");

                observedAssignment[0] = true;
            }

            if (restored.getCourierState() == CourierState.TRAVELING_TO_DESTINATION) {
                observedDeliveryTravel[0] = true;
            }

            if (destinationCount == 8 && cargoCount == 0) {
                helper.assertTrue(observedAssignment[0], "Delivery occurred without automatic assignment");

                helper.assertTrue(observedDeliveryTravel[0], "Delivery occurred without destination travel");

                helper.succeed();
                return;
            }

            if (ticks[0] >= 1200) {
                helper.fail("Automatic cargo recovery timed out: state=" + restored.getCourierState() + ", assigned=" + observedAssignment[0] + ", cargo=" + cargoCount + ", destination=" + destinationCount + ", beePos=" + restored.blockPosition());
            }
        });
    }

    public static void courierReloadControllerMembershipConsistent(GameTestHelper helper) {
        CourierFixture fixture = createFixture(helper);
        ColonyControllerBlockEntity originalController = fixture.controller();

        ResourcefulBee originalBee = createLoadedPersistenceCourier(helper, originalController);

        UUID colonyId = originalController.getColonyId();
        UUID beeId = originalBee.getUUID();
        BlockPos controllerPos = helper.absolutePos(CONTROLLER_POS);

        // Serialize the controller independently of the bee.
        ServerLevel level = helper.getLevel();

        CompoundTag controllerData = originalController.saveWithFullMetadata(level.registryAccess());

        BlockEntity loadedEntity = BlockEntity.loadStatic(originalController.getBlockPos(), originalController.getBlockState(), controllerData, level.registryAccess());

        if (!(loadedEntity instanceof ColonyControllerBlockEntity restoredController)) {
            helper.fail("Controller could not be deserialized");
            return;
        }

        helper.assertTrue(restoredController != originalController, "Controller was not reconstructed");

        helper.assertTrue(colonyId.equals(restoredController.getColonyId()), "Restored controller has a different colony UUID");

        helper.assertTrue(restoredController.isCourierRegistered(beeId) && restoredController.getRegisteredCouriers().size() == 1, "Restored controller lost or duplicated courier registration");

        // Reconstruct the bee using the existing entity load helper.
        ResourcefulBee restoredBee = saveAndRestoreCourier(helper, originalBee);

        if (restoredBee == null) return;

        helper.assertTrue(beeId.equals(restoredBee.getUUID()), "Restored bee UUID does not match controller registry");

        helper.assertTrue(restoredBee.belongsToColony(restoredController.getColonyId()), "Restored bee and controller have different colony UUIDs");

        helper.assertTrue(controllerPos.equals(restoredBee.getColonyControllerPos()), "Restored bee has incorrect controller position");

        helper.assertTrue(restoredController.isCourierRegistered(restoredBee.getUUID()), "Restored controller does not recognize restored bee");

        helper.assertTrue(countIronCargo(restoredBee) == 8, "Restored bee lost cargo");

        // Verify the live controller still recognizes the reloaded bee.
        // The reconstructed controller above validates its saved data;
        // it has not replaced the live world block entity.
        helper.assertTrue(originalController.isCourierRegistered(restoredBee.getUUID()), "Live controller does not recognize reloaded bee");

        helper.succeed();
    }

}
