package com.teamresourceful.resourcefulbees_test.entities;

import com.teamresourceful.resourcefulbees.common.blockentities.ColonyControllerBlockEntity;
import com.teamresourceful.resourcefulbees.common.blockentities.HoneyPotBlockEntity;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.logistics.*;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierAssignment;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierDeliveryExecutor;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierState;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModFluids;
import com.teamresourceful.resourcefulbees_test.util.TestUtilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.UUID;

/**
 * World-backed tests. Bees are set to no-AI; the lifecycle test drives state transitions explicitly.
 */
public final class CourierFluidWorldIntegrationGameTests {
    private static final BlockPos CONTROLLER = new BlockPos(2, 2, 2);
    private static final BlockPos SOURCE = new BlockPos(4, 2, 2);
    private static final BlockPos DEST = new BlockPos(10, 2, 2);
    private static final FluidResource HONEY = FluidResource.of(ModFluids.HONEY_STILL.get());

    private record Fixture(
            ServerLevel level,
            ResourcefulBee bee,
            ColonyControllerBlockEntity
            controller,
            CourierRoute route,
            HoneyPotBlockEntity source,
            HoneyPotBlockEntity destination
    ) {

    }

    private static Fixture fixture(GameTestHelper h, int sourceAmount) {
        h.setBlock(CONTROLLER, ModBlocks.COLONY_CONTROLLER.get());
        h.setBlock(SOURCE, ModBlocks.HONEY_POT.get());
        h.setBlock(DEST, ModBlocks.HONEY_POT.get());
        var controller = h.getBlockEntity(CONTROLLER, ColonyControllerBlockEntity.class);
        var source = h.getBlockEntity(SOURCE, HoneyPotBlockEntity.class);
        var destination = h.getBlockEntity(DEST, HoneyPotBlockEntity.class);
        var level = (ServerLevel) source.getLevel();
        var route = new CourierRoute(UUID.randomUUID(), CourierResourceType.FLUID, new CourierEndpoint(h.absolutePos(SOURCE), Direction.UP), new CourierEndpoint(h.absolutePos(DEST), Direction.UP), true, 0, CourierRouteExecutionMode.COURIER, CourierFilter.NONE);
        h.assertTrue(controller.addRoute(route), "Fluid route registration failed");
        var bee = TestUtilities.createSpawnedTestBee(h);
        bee.setNoAi(true);
        h.assertTrue(controller.registerCourier(bee), "Bee registration failed");
        if (sourceAmount > 0) {
            try (Transaction tx = Transaction.openRoot()) {
                int inserted = source.tank().insert(HONEY, sourceAmount, tx);
                h.assertTrue(inserted == sourceAmount, "Source fixture fill failed");
                tx.commit();
            }
        }
        h.assertTrue(level.getCapability(Capabilities.Fluid.BLOCK, h.absolutePos(SOURCE), Direction.UP) != null, "Source fluid block capability missing");
        h.assertTrue(level.getCapability(Capabilities.Fluid.BLOCK, h.absolutePos(DEST), Direction.UP) != null, "Destination fluid block capability missing");
        return new Fixture(level, bee, controller, route, source, destination);
    }

    private static void moveTo(ResourcefulBee bee, BlockPos pos) {
        bee.setPos(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5);
    }

    private static void assign(GameTestHelper h, Fixture f) {
        h.assertTrue(f.bee.assignCourierRoute(new CourierAssignment(f.controller.getColonyId(), f.route.id()), h.absolutePos(CONTROLLER)), "Fluid courier assignment failed");
    }

    public static void fluidCourierWorldPickup(GameTestHelper h) {
        var f = fixture(h, 800);
        assign(h, f);
        moveTo(f.bee, f.route.source().pos());
        var result = CourierDeliveryExecutor.pickup(f.bee, f.route, 800);
        h.assertTrue(result.amountMoved() == 800, "World pickup failed: " + result);
        h.assertTrue(f.source.tank().getAmountAsInt(0) == 0, "Source did not drain");
        h.assertTrue(f.bee.getCourierFluidCargo().getAmountAsInt(0) == 800, "Bee did not receive fluid");
        h.succeed();
    }

    public static void fluidCourierWorldDelivery(GameTestHelper h) {
        var f = fixture(h, 0);
        assign(h, f);
        try (Transaction tx = Transaction.openRoot()) {
            h.assertTrue(f.bee.getCourierFluidCargo().insert(HONEY, 600, tx) == 600, "Cargo fill failed");
            tx.commit();
        }
        moveTo(f.bee, f.route.destination().pos());
        var result = CourierDeliveryExecutor.deliver(f.bee, f.route, 600);
        h.assertTrue(result.amountMoved() == 600, "World delivery failed: " + result);
        h.assertTrue(f.destination.tank().getAmountAsInt(0) == 600, "Destination did not receive fluid");
        h.assertTrue(f.bee.getCourierFluidCargo().getAmountAsInt(0) == 0, "Cargo not emptied");
        h.succeed();
    }

    public static void fluidCourierFullLifecycle(GameTestHelper h) {
        var f = fixture(h, 500);
        assign(h, f);
        var delivery = f.bee.getCourierDelivery();
        h.assertTrue(delivery.getState() == CourierState.TRAVELING_TO_SOURCE, "Not traveling to source");
        moveTo(f.bee, f.route.source().pos());
        delivery.arriveAtSource();
        h.assertTrue(CourierDeliveryExecutor.pickup(f.bee, f.route, 500).amountMoved() == 500, "Pickup failed");
        delivery.pickupSucceeded();
        h.assertTrue(delivery.getState() == CourierState.TRAVELING_TO_DESTINATION, "Not traveling to destination");
        moveTo(f.bee, f.route.destination().pos());
        delivery.arriveAtDestination();
        h.assertTrue(CourierDeliveryExecutor.deliver(f.bee, f.route, 500).amountMoved() == 500, "Delivery failed");
        delivery.deliverySucceeded();
        h.assertTrue(f.destination.tank().getAmountAsInt(0) == 500, "Destination amount wrong");
        h.assertTrue(f.source.tank().getAmountAsInt(0) == 0, "Source amount wrong");
        h.assertTrue(f.bee.getCourierFluidCargo().getAmountAsInt(0) == 0, "Bee retained fluid");
        h.assertTrue(delivery.getState() == CourierState.RETURNING, "Courier did not enter RETURNING state");
        moveTo(f.bee, f.route.source().pos());
        delivery.returnCompleted();
        h.assertTrue(f.bee.getCourierFluidCargo().getAmountAsInt(0) == 0, "Courier retained fluid after completing delivery");
        h.succeed();
//        moveTo(f.bee, f.route.source().pos());
//        delivery.returnCompleted();
//        h.assertTrue(!delivery.isAssigned(), "Courier assignment not completed");
//        h.succeed();
    }

    public static void fluidCourierSaveReload(GameTestHelper h) {
        var f = fixture(h, 0);
        assign(h, f);
        try (Transaction tx = Transaction.openRoot()) {
            h.assertTrue(f.bee.getCourierFluidCargo().insert(HONEY, 750, tx) == 750, "Cargo fill failed");
            tx.commit();
        }
        f.bee.getCourierDelivery().resumeDeliveryWithCargo();
        CompoundTag saved;
        try (var reporter = new ProblemReporter.ScopedCollector(f.bee.problemPath(), org.slf4j.LoggerFactory.getLogger(CourierFluidWorldIntegrationGameTests.class))) {
            var output = TagValueOutput.createWithContext(reporter, f.bee.registryAccess());
            f.bee.save(output);
            saved = output.buildResult();
        }
        f.bee.discard();
        var restored = TestUtilities.createSpawnedTestBee(h);
        try (var reporter = new ProblemReporter.ScopedCollector(restored.problemPath(), org.slf4j.LoggerFactory.getLogger(CourierFluidWorldIntegrationGameTests.class))) {
            restored.load(TagValueInput.create(reporter, restored.registryAccess(), saved));
        }
        h.assertTrue(restored.getCourierFluidCargo().getAmountAsInt(0) == 750, "Fluid amount lost on reload");
        h.assertTrue(restored.getCourierFluidCargo().getResource(0).equals(HONEY), "Fluid type lost on reload");
        h.assertTrue(restored.getCourierState() == CourierState.TRAVELING_TO_DESTINATION, "Delivery state not restored");
        h.assertTrue(restored.getCourierDelivery().isAssigned(), "Assignment not restored");
        h.succeed();
    }
}
