package com.teamresourceful.resourcefulbees_test.logistics;

import com.teamresourceful.resourcefulbees.common.blockentities.ColonyControllerBlockEntity;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.logistics.*;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierAssignment;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierDeliveryExecutor;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierState;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees_test.util.TestUtilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public final class CourierDeliveryExecutorGameTests {

    private static final BlockPos SOURCE = new BlockPos(1, 2, 1);
    private static final BlockPos DESTINATION = new BlockPos(3, 2, 1);

    private static final ItemResource IRON = ItemResource.of(Items.IRON_INGOT);

    private CourierDeliveryExecutorGameTests() {
    }

    private static ItemStacksResourceHandler cargo() {
        return new ItemStacksResourceHandler(1) {
            @Override
            protected int getCapacity(int index, @NonNull ItemResource resource) {
                return Math.min(16, super.getCapacity(index, resource));
            }
        };
    }

    private static CourierRoute route(GameTestHelper helper) {
        return new CourierRoute(UUID.randomUUID(), CourierResourceType.ITEM, new CourierEndpoint(helper.absolutePos(SOURCE), Direction.UP), new CourierEndpoint(helper.absolutePos(DESTINATION), Direction.UP), true, 0, CourierRouteExecutionMode.COURIER, CourierFilter.NONE);
    }

    private static void setup(GameTestHelper helper) {
        helper.setBlock(SOURCE, Blocks.CHEST);
        helper.setBlock(DESTINATION, Blocks.CHEST);
    }

    private static int count(ChestBlockEntity chest) {
        int total = 0;

        for (int slot = 0; slot < chest.getContainerSize(); slot++) {
            total += chest.getItem(slot).getCount();
        }

        return total;
    }

    private static void fill(ItemStacksResourceHandler cargo, int amount) {
        try (Transaction tx = Transaction.openRoot()) {
            int inserted = cargo.insert(IRON, amount, tx);

            if (inserted != amount) {
                throw new IllegalStateException("Could not initialize courier cargo");
            }

            tx.commit();
        }
    }

    public static void courierPickup(GameTestHelper helper) {
        setup(helper);

        ChestBlockEntity source = helper.getBlockEntity(SOURCE, ChestBlockEntity.class);

        source.setItem(0, new net.minecraft.world.item.ItemStack(Items.IRON_INGOT, 32));

        ItemStacksResourceHandler cargo = cargo();

        var result = CourierDeliveryExecutor.pickup(helper.getLevel(), route(helper), cargo, 16);

        if (result.amountMoved() != 16 || result.status() != TransferStatus.SUCCESS || count(source) != 16 || cargo.getAmountAsInt(0) != 16) {
            helper.fail("Courier pickup failed");
            return;
        }

        helper.succeed();
    }

    public static void courierDelivery(GameTestHelper helper) {
        setup(helper);

        ChestBlockEntity destination = helper.getBlockEntity(DESTINATION, ChestBlockEntity.class);

        ItemStacksResourceHandler cargo = cargo();
        fill(cargo, 16);

        var result = CourierDeliveryExecutor.deliver(helper.getLevel(), route(helper), cargo, 16);

        if (result.amountMoved() != 16 || result.status() != TransferStatus.SUCCESS || cargo.getAmountAsInt(0) != 0 || count(destination) != 16) {
            helper.fail("Courier delivery failed");
            return;
        }

        helper.succeed();
    }

    public static void courierRejectsOccupiedPickup(GameTestHelper helper) {
        setup(helper);

        ChestBlockEntity source = helper.getBlockEntity(SOURCE, ChestBlockEntity.class);

        source.setItem(0, new net.minecraft.world.item.ItemStack(Items.IRON_INGOT, 32));

        ItemStacksResourceHandler cargo = cargo();
        fill(cargo, 8);

        var result = CourierDeliveryExecutor.pickup(helper.getLevel(), route(helper), cargo, 16);

        if (result.amountMoved() != 0 || count(source) != 32 || cargo.getAmountAsInt(0) != 8) {
            helper.fail("Occupied courier picked up more items");
            return;
        }

        helper.succeed();
    }

    public static void courierUnavailableDestination(GameTestHelper helper) {
        setup(helper);

        // Remove the destination inventory capability.
        helper.setBlock(DESTINATION, Blocks.STONE);

        ItemStacksResourceHandler cargo = cargo();
        fill(cargo, 12);

        var result = CourierDeliveryExecutor.deliver(helper.getLevel(), route(helper), cargo, 16);

        if (result.status() != TransferStatus.DESTINATION_UNAVAILABLE || result.amountMoved() != 0 || cargo.getAmountAsInt(0) != 12 || !cargo.getResource(0).equals(IRON)) {
            helper.fail("Courier lost cargo when destination was unavailable");
            return;
        }

        helper.succeed();
    }


    public static void courierRejectsRemotePickup(GameTestHelper helper) {
        setup(helper);

        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        // Position the bee far from the source.
        BlockPos farAway = helper.absolutePos(new BlockPos(10, 2, 10));

        bee.setPos(farAway.getX() + 0.5D, farAway.getY() + 0.5D, farAway.getZ() + 0.5D);

        ChestBlockEntity source = helper.getBlockEntity(SOURCE, ChestBlockEntity.class);

        source.setItem(0, new ItemStack(Items.IRON_INGOT, 16));

        var result = CourierDeliveryExecutor.pickup(bee, route(helper), 16);

        if (result.amountMoved() != 0 || count(source) != 16 || bee.getCourierCargo().getAmountAsInt(0) != 0) {
            helper.fail("Courier picked up items remotely");
            return;
        }

        helper.succeed();
    }

    public static void courierRejectsRemoteDelivery(GameTestHelper helper) {
        setup(helper);

        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        BlockPos farAway = helper.absolutePos(new BlockPos(10, 2, 10));

        bee.setPos(farAway.getX() + 0.5D, farAway.getY() + 0.5D, farAway.getZ() + 0.5D);

        ItemResource iron = ItemResource.of(Items.IRON_INGOT);

        try (Transaction tx = Transaction.openRoot()) {
            int inserted = bee.getCourierCargo().insert(iron, 16, tx);

            if (inserted != 16) {
                helper.fail("Failed to initialize cargo");
                return;
            }

            tx.commit();
        }

        ChestBlockEntity destination = helper.getBlockEntity(DESTINATION, ChestBlockEntity.class);

        var result = CourierDeliveryExecutor.deliver(bee, route(helper), 16);

        if (result.amountMoved() != 0 || bee.getCourierCargo().getAmountAsInt(0) != 16 || count(destination) != 0) {
            helper.fail("Courier delivered items remotely");
            return;
        }

        helper.succeed();
    }

    public static void courierDeathDropsCargo(GameTestHelper helper) {
        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        ItemResource diamonds = ItemResource.of(Items.DIAMOND);

        try (Transaction transaction = Transaction.openRoot()) {
            int inserted = bee.getCourierCargo().insert(diamonds, 16, transaction);

            if (inserted != 16) {
                helper.fail("Failed to initialize courier cargo");
                return;
            }

            transaction.commit();
        }

        AABB dropArea = bee.getBoundingBox().inflate(3.0D);

        bee.hurtServer(helper.getLevel(), helper.getLevel().damageSources().genericKill(), Float.MAX_VALUE);

        helper.runAfterDelay(2, () -> {
            int dropped = helper.getLevel().getEntitiesOfClass(ItemEntity.class, dropArea).stream().filter(item -> item.getItem().is(Items.DIAMOND)).mapToInt(item -> item.getItem().getCount()).sum();

            if (dropped != 16) {
                helper.fail("Expected 16 diamonds dropped on death, found " + dropped);
                return;
            }

            if (bee.getCourierCargo().getAmountAsInt(0) != 0) {
                helper.fail("Courier cargo was not emptied after death");
                return;
            }

            helper.succeed();
        });
    }

    public static void courierEntityPersistence(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();

        // 1. Create a real colony controller.
        BlockPos controllerPos = new BlockPos(1, 2, 1);
        BlockPos sourcePos = new BlockPos(2, 2, 1);
        BlockPos destinationPos = new BlockPos(3, 2, 1);

        helper.setBlock(controllerPos, ModBlocks.COLONY_CONTROLLER.get());

        ColonyControllerBlockEntity controller = helper.getBlockEntity(controllerPos, ColonyControllerBlockEntity.class);

        // 2. Register a courier-managed route.
        CourierRoute route = new CourierRoute(UUID.randomUUID(), CourierResourceType.ITEM, new CourierEndpoint(helper.absolutePos(sourcePos), Direction.UP), new CourierEndpoint(helper.absolutePos(destinationPos), Direction.UP), true, 0, CourierRouteExecutionMode.COURIER, CourierFilter.NONE);

        if (!controller.addRoute(route)) {
            helper.fail("Could not register courier route");
            return;
        }

        CourierAssignment assignment = new CourierAssignment(controller.getColonyId(), route.id());

        // 3. Create and assign the original bee.
        ResourcefulBee original = TestUtilities.createSpawnedTestBee(helper);

        helper.assertTrue(controller.registerCourier(original), "Registration failed: beePos=" + original.blockPosition() + ", controllerPos=" + controller.getBlockPos() + ", radius=" + controller.getOperatingRadius() + ", beeColony=" + original.getColonyId() + ", beeController=" + original.getColonyControllerPos() + ", alive=" + original.isAlive());

        helper.assertTrue(original.assignCourierRoute(assignment, controller.getBlockPos()), "Could not assign courier route");

        // 4. Load 16 diamonds into its cargo.
        ItemResource diamonds = ItemResource.of(Items.DIAMOND);

        try (Transaction transaction = Transaction.openRoot()) {
            int inserted = original.getCourierCargo().insert(diamonds, 16, transaction);

            if (inserted != 16) {
                helper.fail("Could not initialize courier cargo");
                return;
            }

            transaction.commit();
        }

        // 5. Serialize the original bee.
        // Use the same registry access as your existing
        // TagValueOutput/TagValueInput GameTests.
        RegistryAccess registryAccess = level.registryAccess();

        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registryAccess);

        original.addAdditionalSaveData(output);

        CompoundTag savedData = output.buildResult();

        // 6. Deserialize into a separate bee instance.
        ResourcefulBee restored = TestUtilities.createSpawnedTestBee(helper);

        TagValueInput input = (TagValueInput) TagValueInput.create(ProblemReporter.DISCARDING, registryAccess, savedData);

        restored.readAdditionalSaveData(input);

        // 7. Verify the assignment.
        CourierAssignment restoredAssignment = restored.getCourierDelivery().getAssignment();

        if (restoredAssignment == null) {
            helper.fail("Courier assignment was not restored");
            return;
        }

        if (!restoredAssignment.colonyId().equals(assignment.colonyId())) {
            helper.fail("Courier colony UUID changed after reload");
            return;
        }

        if (!restoredAssignment.routeId().equals(assignment.routeId())) {
            helper.fail("Courier route UUID changed after reload");
            return;
        }

        // 8. Verify cargo contents.
        var cargo = restored.getCourierCargo();

        if (cargo.getAmountAsInt(0) != 16) {
            helper.fail("Courier cargo amount was not restored");
            return;
        }

        if (!cargo.getResource(0).equals(diamonds)) {
            helper.fail("Courier cargo item was not restored");
            return;
        }

        // 9. Verify recovery state.
        if (restored.getCourierState() != CourierState.TRAVELING_TO_DESTINATION) {
            helper.fail("Courier did not resume delivery with cargo");
            return;
        }

        // 10. Verify the restored assignment resolves to
        // the correct controller and route.
        CourierRoute resolved = restored.getAssignedCourierRoute();

        if (resolved == null || !resolved.id().equals(route.id())) {
            helper.fail("Restored courier assignment could not resolve its route");
            return;
        }

        helper.succeed();
    }

}
