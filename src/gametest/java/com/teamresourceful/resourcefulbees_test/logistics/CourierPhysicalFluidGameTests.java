package com.teamresourceful.resourcefulbees_test.logistics;

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
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.UUID;

/**
 * Phase 3B.6C: physical fluid cargo, transactional transfer, and bee lifecycle.
 */
public final class CourierPhysicalFluidGameTests {
    private static final FluidResource WATER = FluidResource.of(Fluids.WATER);
    private static final FluidResource LAVA = FluidResource.of(Fluids.LAVA);
    private static final BlockPos CONTROLLER = new BlockPos(2, 2, 2);
    private static final BlockPos SOURCE = new BlockPos(4, 2, 2);
    private static final BlockPos DEST = new BlockPos(12, 2, 2);

    private CourierPhysicalFluidGameTests() {
    }

    private static final class Tank extends FluidStacksResourceHandler {
        Tank(int capacity) {
            super(1, capacity);
        }
    }

    private static void fill(FluidStacksResourceHandler handler, FluidResource fluid, int amount) {
        try (Transaction tx = Transaction.openRoot()) {
            int inserted = handler.insert(fluid, amount, tx);
            if (inserted != amount) throw new AssertionError("Fixture fill failed: " + inserted + "/" + amount);
            tx.commit();
        }
    }

    private static void amount(GameTestHelper h, FluidStacksResourceHandler tank, int expected) {
        h.assertTrue(tank.getAmountAsInt(0) == expected, "Expected " + expected + " fluid units, got " + tank.getAmountAsInt(0));
    }

    private static void result(GameTestHelper h, TransferResult actual, TransferStatus status, int moved) {
        h.assertTrue(actual.status() == status && actual.amountMoved() == moved, "Expected " + status + "/" + moved + ", got " + actual.status() + "/" + actual.amountMoved());
    }

    private static CourierRoute route(GameTestHelper h, CourierResourceType type) {
        return new CourierRoute(UUID.randomUUID(), type, new CourierEndpoint(h.absolutePos(SOURCE), Direction.UP), new CourierEndpoint(h.absolutePos(DEST), Direction.UP), true, 0, CourierRouteExecutionMode.COURIER, CourierFilter.NONE);
    }

    private record Fixture(ColonyControllerBlockEntity controller, ResourcefulBee bee, CourierRoute route) {
    }

    private static Fixture fixture(GameTestHelper h) {
        h.setBlock(CONTROLLER, ModBlocks.COLONY_CONTROLLER.get());
        ColonyControllerBlockEntity controller = h.getBlockEntity(CONTROLLER, ColonyControllerBlockEntity.class);
        CourierRoute route = route(h, CourierResourceType.FLUID);
        h.assertTrue(controller.addRoute(route), "Could not add fluid courier route");
        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(h);
        BlockPos start = h.absolutePos(new BlockPos(8, 3, 6));
        bee.setPos(start.getX() + .5, start.getY() + .5, start.getZ() + .5);
        bee.setNoAi(true);
        h.assertTrue(controller.registerCourier(bee), "Could not register fluid courier");
        return new Fixture(controller, bee, route);
    }

    public static void fluidCargoCapacity(GameTestHelper h) {
        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(h);
        var ability = bee.getCourierAbility();
        h.assertTrue(ability != null, "Test bee lacks CourierAbility");
        int limit = ability.fluidTransferLimit();
        h.assertTrue(limit > 0, "Invalid courier fluid capacity");
        try (Transaction tx = Transaction.openRoot()) {
            int inserted = bee.getCourierFluidCargo().insert(WATER, limit + 100, tx);
            h.assertTrue(inserted == limit, "Bee exceeded fluid ability capacity: " + inserted + "/" + limit);
            tx.commit();
        }
        h.assertTrue(bee.getCourierFluidCargo().getAmountAsInt(0) == limit, "Fluid capacity not retained");
        h.succeed();
    }

    public static void fluidCargoTypeIsolation(GameTestHelper h) {
        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(h);
        ((FluidStacksResourceHandler) bee.getCourierFluidCargo()).set(0, WATER, 100);
        h.assertTrue(bee.hasCourierCargo(CourierResourceType.FLUID), "Fluid cargo not detected");
        h.assertTrue(bee.hasCourierCargoOtherThan(CourierResourceType.ITEM), "Fluid cargo not protected from item routes");
        h.assertTrue(!bee.hasCourierCargoOtherThan(CourierResourceType.FLUID), "Fluid mistaken for item cargo");
        h.assertTrue(!bee.hasCourierCargo(CourierResourceType.ITEM), "Item cargo unexpectedly present");
        h.succeed();
    }

    public static void fluidPickupConservation(GameTestHelper h) {
        Tank source = new Tank(3000), cargo = new Tank(1000);
        fill(source, WATER, 1800);
        result(h, CourierFluidTransferEngine.transferFluids(source, cargo, 1000), TransferStatus.SUCCESS, 1000);
        amount(h, source, 800);
        amount(h, cargo, 1000);
        h.succeed();
    }

    public static void fluidPartialDelivery(GameTestHelper h) {
        Tank cargo = new Tank(1000), dest = new Tank(1000);
        fill(cargo, WATER, 900);
        fill(dest, WATER, 750);
        result(h, CourierFluidTransferEngine.transferFluids(cargo, dest, 900), TransferStatus.PARTIAL, 250);
        amount(h, cargo, 650);
        amount(h, dest, 1000);
        h.succeed();
    }

    public static void fluidBlockedDeliveryRetainsCargo(GameTestHelper h) {
        Tank cargo = new Tank(1000), dest = new Tank(1000);
        fill(cargo, WATER, 600);
        fill(dest, LAVA, 1000);
        result(h, CourierFluidTransferEngine.transferFluids(cargo, dest, 600), TransferStatus.DESTINATION_FULL, 0);
        amount(h, cargo, 600);
        h.assertTrue(cargo.getResource(0).equals(WATER) && dest.getResource(0).equals(LAVA), "Rejected delivery changed fluid types");
        h.succeed();
    }

    public static void fluidAssignmentResumesCargo(GameTestHelper h) {
        Fixture f = fixture(h);
        ((FluidStacksResourceHandler) f.bee().getCourierFluidCargo()).set(0, WATER, 100);
        h.assertTrue(f.bee().assignCourierRoute(new CourierAssignment(f.controller().getColonyId(), f.route().id()), h.absolutePos(CONTROLLER)), "Could not assign loaded fluid courier");
        h.assertTrue(f.bee().getCourierState() == CourierState.TRAVELING_TO_DESTINATION, "Loaded fluid cargo did not resume delivery");
        h.assertTrue(f.bee().getCourierFluidCargo().getAmountAsInt(0) == 100, "Assignment changed fluid cargo");
        h.succeed();
    }

    public static void fluidCannotAssignItemRoute(GameTestHelper h) {
        Fixture f = fixture(h);
        ((FluidStacksResourceHandler) f.bee().getCourierFluidCargo()).set(0, WATER, 100);
        CourierRoute item = route(h, CourierResourceType.ITEM);
        h.assertTrue(f.controller().addRoute(item), "Could not add item route");
        h.assertTrue(!f.bee().assignCourierRoute(new CourierAssignment(f.controller().getColonyId(), item.id()), h.absolutePos(CONTROLLER)), "Fluid-bearing bee accepted an item route");
        h.assertTrue(f.bee().getCourierFluidCargo().getAmountAsInt(0) == 100, "Rejected assignment changed cargo");
        h.succeed();
    }

    public static void fluidSurvivesLeavingColony(GameTestHelper h) {
        Fixture f = fixture(h);
        ((FluidStacksResourceHandler) f.bee().getCourierFluidCargo()).set(0, WATER, 100);
        h.assertTrue(f.bee().leaveColony(f.controller().getColonyId()), "Could not leave colony");
        h.assertTrue(!f.bee().isColonyMember(), "Bee remains a colony member");
        h.assertTrue(f.bee().getCourierFluidCargo().getAmountAsInt(0) == 100, "Leaving colony destroyed fluid cargo");
        h.succeed();
    }

    public static void fluidDeathIsLossy(GameTestHelper h) {
        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(h);
        ((FluidStacksResourceHandler) bee.getCourierFluidCargo()).set(0, WATER, 100);
        h.assertTrue(bee.hasFluidCourierCargo(), "Death fixture has no fluid");
        // Intentional policy: fluid has no death-drop representation; entity removal destroys it.
        bee.discard();
        h.assertTrue(bee.isRemoved(), "Bee was not destroyed");
        h.succeed();
    }
}
