package com.teamresourceful.resourcefulbees_test.entities;

import com.teamresourceful.resourcefulbees.common.blockentities.HoneyGeneratorBlockEntity;
import com.teamresourceful.resourcefulbees.common.components.Occupant;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.logistics.*;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierAssignment;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierDeliveryController;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierDeliveryExecutor;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierState;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees_test.util.TestUtilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityProcessor;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.UUID;

public final class CourierPhysicalEnergyGameTests {

    private CourierPhysicalEnergyGameTests() {
    }

    private static SimpleEnergyHandler battery(int capacity, int initial) {
        var handler = new SimpleEnergyHandler(capacity);

        try (Transaction tx = Transaction.openRoot()) {
            int inserted = handler.insert(initial, tx);
            if (inserted != initial) {
                throw new AssertionError("Energy fixture initialization failed");
            }
            tx.commit();
        }

        return handler;
    }

    private static void assertEnergy(GameTestHelper helper, EnergyHandler handler, int expected) {
        helper.assertTrue(handler.getAmountAsInt() == expected, "Expected " + expected + " FE, found " + handler.getAmountAsInt() + " FE");
    }

    private static void assertResult(GameTestHelper helper, TransferResult result, TransferStatus status, int moved) {
        helper.assertTrue(result.status() == status && result.amountMoved() == moved, "Expected " + status + "/" + moved + ", got " + result.status() + "/" + result.amountMoved());
    }

    private static CourierRoute energyRoute(BlockPos source, BlockPos destination) {
        return new CourierRoute(UUID.randomUUID(), CourierResourceType.ENERGY, new CourierEndpoint(source, Direction.UP), new CourierEndpoint(destination, Direction.UP), true, 0, CourierRouteExecutionMode.COURIER, CourierFilter.NONE);
    }

    private static void fill(EnergyHandler handler, int amount) {
        try (Transaction tx = Transaction.openRoot()) {
            if (handler.insert(amount, tx) != amount) {
                throw new AssertionError("Cannot fill energy cargo");
            }
            tx.commit();
        }
    }


    public static void energyCourierDelivery(GameTestHelper helper) {
        var cargo = battery(1000, 800);
        var destination = battery(2000, 0);

        var result = CourierEnergyTransferEngine.transferEnergy(cargo, destination, 800);

        assertResult(helper, result, TransferStatus.SUCCESS, 800);
        assertEnergy(helper, cargo, 0);
        assertEnergy(helper, destination, 800);

        helper.succeed();
    }

    public static void energyCourierPartialDelivery(GameTestHelper helper) {
        var cargo = battery(1000, 800);
        var destination = battery(1000, 750);

        var result = CourierEnergyTransferEngine.transferEnergy(cargo, destination, 800);

        assertResult(helper, result, TransferStatus.PARTIAL, 250);
        assertEnergy(helper, cargo, 550);
        assertEnergy(helper, destination, 1000);

        helper.succeed();
    }

    public static void energyCourierBlockedDelivery(GameTestHelper helper) {
        var cargo = battery(1000, 800);
        var destination = battery(1000, 1000);

        var blocked = CourierEnergyTransferEngine.transferEnergy(cargo, destination, 800);

        assertResult(helper, blocked, TransferStatus.DESTINATION_FULL, 0);
        assertEnergy(helper, cargo, 800);

        // Free some destination capacity.
        try (Transaction tx = Transaction.openRoot()) {
            int extracted = destination.extract(500, tx);
            helper.assertTrue(extracted == 500, "Failed to free destination capacity");
            tx.commit();
        }

        var retry = CourierEnergyTransferEngine.transferEnergy(cargo, destination, 800);

        assertResult(helper, retry, TransferStatus.PARTIAL, 500);
        assertEnergy(helper, cargo, 300);
        assertEnergy(helper, destination, 1000);

        helper.succeed();
    }


    public static void energyCourierResourceIsolation(GameTestHelper helper) {
        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        fill(bee.getCourierEnergyCargo(), 500);

        helper.assertTrue(bee.hasEnergyCourierCargo(), "Bee should contain energy");

        helper.assertTrue(bee.hasCourierCargo(CourierResourceType.ENERGY), "ENERGY cargo check failed");

        helper.assertTrue(bee.hasCourierCargoOtherThan(CourierResourceType.ITEM), "Energy should block item assignments");

        helper.assertTrue(bee.hasCourierCargoOtherThan(CourierResourceType.FLUID), "Energy should block fluid assignments");

        helper.assertTrue(!bee.hasCourierCargoOtherThan(CourierResourceType.ENERGY), "Energy should not block energy assignments");

        helper.succeed();
    }

    public static void energyCourierColonyDeparture(GameTestHelper helper) {
        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        fill(bee.getCourierEnergyCargo(), 600);

        //TODO
        // Leaving a colony must not destroy cargo.
        // The fixture should first register the bee with a real
        // ColonyControllerBlockEntity and obtain its colony UUID.
        //
        // UUID colonyId = controller.getColonyId();
        // bee.joinColony(colonyId, controller.getBlockPos());
        // helper.assertTrue(bee.leaveColony(colonyId),
        //         "Failed to leave colony");

        assertEnergy(helper, bee.getCourierEnergyCargo(), 600);

        helper.succeed();
    }


    public static void energyCourierPickup(GameTestHelper helper) {
        BlockPos sourcePos = new BlockPos(2, 2, 2);
        BlockPos destinationPos = new BlockPos(5, 2, 2);

        helper.setBlock(sourcePos, ModBlocks.HONEY_GENERATOR.get());

        HoneyGeneratorBlockEntity generator = helper.getBlockEntity(sourcePos, HoneyGeneratorBlockEntity.class);

        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        // The executor checks distance in absolute world coordinates.
        BlockPos sourceWorldPos = helper.absolutePos(sourcePos);
        bee.setPos(sourceWorldPos.getX() + 0.5, sourceWorldPos.getY() + 0.5, sourceWorldPos.getZ() + 0.5);

        fill(generator.battery(), 800);

        CourierRoute route = energyRoute(sourceWorldPos, helper.absolutePos(destinationPos));

        int sourceBefore = generator.battery().getAmountAsInt();
        int cargoBefore = bee.getCourierEnergyCargo().getAmountAsInt();

        var result = CourierDeliveryExecutor.pickup(bee, route, 500);

        int sourceAfter = generator.battery().getAmountAsInt();
        int cargoAfter = bee.getCourierEnergyCargo().getAmountAsInt();

        helper.assertTrue(result.amountMoved() > 0, "Courier did not pick up energy");

        helper.assertTrue(sourceBefore - sourceAfter == result.amountMoved(), "Source energy mismatch");

        helper.assertTrue(cargoAfter - cargoBefore == result.amountMoved(), "Bee cargo energy mismatch");

        helper.assertTrue(result.amountMoved() <= 500, "Courier exceeded pickup limit");

        helper.succeed();
    }

    public static void energyCourierCapacity(GameTestHelper helper) {
        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        var ability = bee.getCourierAbility();

        helper.assertTrue(ability != null, "Test bee requires CourierAbility");

        int limit = ability.energyTransferLimit();

        helper.assertTrue(limit > 0, "Courier energy capacity must be positive");

        EnergyHandler cargo = bee.getCourierEnergyCargo();

        int accepted;
        try (Transaction tx = Transaction.openRoot()) {
            accepted = cargo.insert(limit + 1, tx);
            tx.commit();
        }

        helper.assertTrue(accepted <= limit, "Energy handler exceeded CourierAbility capacity");

        helper.assertTrue(cargo.getAmountAsInt() <= limit, "Stored energy exceeds configured capacity");

        helper.succeed();
    }

    private static void assertResumingEnergyDelivery(GameTestHelper helper, CourierDeliveryController delivery) {
        CourierState state = delivery.getState();

        helper.assertTrue(state == CourierState.DELIVERING || state == CourierState.TRAVELING_TO_DESTINATION, "Expected delivery recovery, got " + state);
    }

    public static void energyCourierAssignmentRecovery(GameTestHelper helper) {
        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        fill(bee.getCourierEnergyCargo(), 600);

        var delivery = bee.getCourierDelivery();

        UUID colonyId = UUID.randomUUID();
        UUID routeId = UUID.randomUUID();

        var assignment = new CourierAssignment(colonyId, routeId);

        helper.assertTrue(delivery.assign(assignment), "Failed to assign courier");

        delivery.resumeDeliveryWithCargo();

        helper.assertTrue(delivery.isAssigned(), "Courier should remain assigned");

        assertResumingEnergyDelivery(helper, delivery);

        delivery.clearAssignment();

        assertEnergy(helper, bee.getCourierEnergyCargo(), 600);

        helper.assertTrue(!delivery.isAssigned(), "Assignment should be cleared");

        helper.assertTrue(delivery.assign(assignment), "Failed to reassign courier");

        delivery.resumeDeliveryWithCargo();

        assertResumingEnergyDelivery(helper, delivery);

        assertEnergy(helper, bee.getCourierEnergyCargo(), 600);

        helper.succeed();
    }

    public static void energyCourierDeathLoss(GameTestHelper helper) {
        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        fill(bee.getCourierEnergyCargo(), 600);

        helper.assertTrue(bee.getCourierEnergyCargo().getAmountAsInt() == 600, "Bee must have energy before death");

        // Record nearby item entities before the bee dies.
        AABB area = bee.getBoundingBox().inflate(3.0);

        int itemsBefore = bee.level().getEntitiesOfClass(ItemEntity.class, area).size();

        bee.kill((ServerLevel) bee.level());

        helper.runAfterDelay(2, () -> {
            int itemsAfter = bee.level().getEntitiesOfClass(ItemEntity.class, area).size();

            helper.assertTrue(!bee.isAlive(), "Bee should be dead");

            helper.assertTrue(itemsAfter == itemsBefore, "Energy cargo unexpectedly generated item drops");

            helper.succeed();
        });
    }

    public static void energyCourierPersistence(GameTestHelper helper) {
        ResourcefulBee original = TestUtilities.createSpawnedTestBee(helper);

        int energy = Math.min(600, original.getCourierAbility().energyTransferLimit());

        helper.assertTrue(energy > 0, "Test bee must have positive energy capacity");

        fill(original.getCourierEnergyCargo(), energy);

        helper.assertTrue(original.hasCourierCargo(CourierResourceType.ENERGY), "Original bee has no energy cargo");

        // Use the existing entity serialization utility.
        var saved = Occupant.saveEntity(original);

        // Create a completely new entity from saved NBT.
        var restoredEntity = EntityType.loadEntityRecursive(saved.type(), saved.copyTagWithoutId(), helper.getLevel(), EntitySpawnReason.LOAD, EntityProcessor.NOP);

        helper.assertTrue(restoredEntity instanceof ResourcefulBee, "Restored entity is not a ResourcefulBee");

        ResourcefulBee restored = (ResourcefulBee) restoredEntity;

        assertEnergy(helper, restored.getCourierEnergyCargo(), energy);

        helper.assertTrue(restored.hasCourierCargo(CourierResourceType.ENERGY), "Restored bee lost its energy cargo");

        helper.assertTrue(restored.hasCourierCargoOtherThan(CourierResourceType.ITEM), "Restored energy must block item cargo");

        helper.assertTrue(restored.hasCourierCargoOtherThan(CourierResourceType.FLUID), "Restored energy must block fluid cargo");

        helper.succeed();
    }

}
