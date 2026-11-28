package com.teamresourceful.resourcefulbees_test.logistics;

import com.teamresourceful.resourcefulbees.common.logistics.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Phase 3B.6B: transactional fluid engine and unified scheduling regression tests.
 */
public final class CourierFluidTransferGameTests {
    private static final FluidResource WATER = FluidResource.of(Fluids.WATER);
    private static final FluidResource LAVA = FluidResource.of(Fluids.LAVA);
    private static final long START = 1_000_000_000L;

    private CourierFluidTransferGameTests() {
    }

    private static class Tank extends FluidStacksResourceHandler {
        Tank(int capacity) {
            super(1, capacity);
        }
    }

    private static final class WaterOnlyTank extends Tank {
        WaterOnlyTank(int capacity) {
            super(capacity);
        }

        @Override
        public boolean isValid(int index, FluidResource resource) {
            return resource.equals(WATER);
        }
    }

    private static void fill(Tank tank, FluidResource resource, int amount) {
        try (Transaction tx = Transaction.openRoot()) {
            int inserted = tank.insert(resource, amount, tx);
            if (inserted != amount)
                throw new AssertionError("Test setup failed to fill tank: " + inserted + "/" + amount);
            tx.commit();
        }
    }

    private static void amounts(GameTestHelper helper, Tank source, Tank dest, int expectedSource, int expectedDest) {
        helper.assertTrue(source.getAmountAsInt(0) == expectedSource && dest.getAmountAsInt(0) == expectedDest, "Fluid conservation/capacity mismatch: source=" + source.getAmountAsInt(0) + ", destination=" + dest.getAmountAsInt(0) + ", expected=" + expectedSource + "/" + expectedDest);
    }

    private static void status(GameTestHelper helper, TransferResult result, TransferStatus expected, int moved) {
        helper.assertTrue(result.status() == expected && result.amountMoved() == moved, "Expected " + expected + "/" + moved + ", got " + result.status() + "/" + result.amountMoved());
    }

    private static CourierRoute route(long id, CourierResourceType type, int priority) {
        return new CourierRoute(new UUID(0, id), type, new CourierEndpoint(new BlockPos(4, 2, 2), Direction.UP), new CourierEndpoint(new BlockPos(6, 2, 2), Direction.UP), true, priority, CourierRouteExecutionMode.INSTANT, CourierFilter.NONE);
    }

    public static void fluidInstantTransfer(GameTestHelper helper) {
        Tank source = new Tank(4_000), dest = new Tank(4_000);
        fill(source, WATER, 2_000);
        var result = CourierFluidTransferEngine.transferFluids(source, dest, 1_000);
        status(helper, result, TransferStatus.SUCCESS, 1_000);
        amounts(helper, source, dest, 1_000, 1_000);
        helper.succeed();
    }

    public static void fluidTransferLimit(GameTestHelper helper) {
        Tank source = new Tank(4_000), dest = new Tank(4_000);
        fill(source, WATER, 2_000);
        status(helper, CourierFluidTransferEngine.transferFluids(source, dest, 250), TransferStatus.SUCCESS, 250);
        amounts(helper, source, dest, 1_750, 250);
        status(helper, CourierFluidTransferEngine.transferFluids(source, dest, 0), TransferStatus.INVALID_LIMIT, 0);
        amounts(helper, source, dest, 1_750, 250);
        helper.succeed();
    }

    public static void fluidDestinationFull(GameTestHelper helper) {
        Tank source = new Tank(2_000), dest = new Tank(1_000);
        fill(source, WATER, 1_500);
        fill(dest, WATER, 1_000);
        status(helper, CourierFluidTransferEngine.transferFluids(source, dest, 800), TransferStatus.DESTINATION_FULL, 0);
        amounts(helper, source, dest, 1_500, 1_000);
        helper.succeed();
    }

    public static void fluidIncompatibleDestination(GameTestHelper helper) {
        Tank source = new Tank(2_000);
        WaterOnlyTank dest = new WaterOnlyTank(2_000);
        fill(source, LAVA, 1_000);
        status(helper, CourierFluidTransferEngine.transferFluids(source, dest, 500), TransferStatus.DESTINATION_FULL, 0);
        amounts(helper, source, dest, 1_000, 0);
        helper.assertTrue(source.getResource(0).equals(LAVA), "Lava resource changed after rejected transfer");
        helper.succeed();
    }

    public static void fluidPartialTransfer(GameTestHelper helper) {
        Tank source = new Tank(2_000), dest = new Tank(1_000);
        fill(source, WATER, 1_500);
        fill(dest, WATER, 750);
        status(helper, CourierFluidTransferEngine.transferFluids(source, dest, 800), TransferStatus.PARTIAL, 250);
        amounts(helper, source, dest, 1_250, 1_000);
        helper.succeed();
    }

    public static void fluidRollbackOnRejection(GameTestHelper helper) {
        Tank source = new Tank(2_000), dest = new Tank(1_000);
        fill(source, WATER, 1_500);
        fill(dest, LAVA, 500);
        status(helper, CourierFluidTransferEngine.transferFluids(source, dest, 600), TransferStatus.DESTINATION_FULL, 0);
        amounts(helper, source, dest, 1_500, 500);
        helper.assertTrue(dest.getResource(0).equals(LAVA), "Destination fluid was replaced");
        helper.succeed();
    }

    public static void fluidSchedulerExecution(GameTestHelper helper) {
        Tank source = new Tank(2_000), dest = new Tank(2_000);
        fill(source, WATER, 1_000);
        CourierRoute fluid = route(1, CourierResourceType.FLUID, 5);
        CourierRoute item = route(2, CourierResourceType.ITEM, 5);
        Map<UUID, CourierRoute> routes = new LinkedHashMap<>();
        routes.put(item.id(), item); // Reverse insertion order tests UUID-based fairness.
        routes.put(fluid.id(), fluid);
        CourierRouteScheduler scheduler = new CourierRouteScheduler();
        scheduler.setInterval(5);
        int[] fluidCalls = {0};
        int[] itemCalls = {0};
        java.util.function.Consumer<CourierRoute> executor = selected -> {
            if (selected.resourceType() == CourierResourceType.FLUID) {
                fluidCalls[0]++;
                CourierFluidTransferEngine.transferFluids(source, dest, 100);
            } else {
                itemCalls[0]++;
            }
        };
        for (long time : new long[]{START, START + 1, START + 4, START + 5, START + 10}) {
            scheduler.tick(routes, time, r -> true, executor);
        }
        helper.assertTrue(fluidCalls[0] == 2 && itemCalls[0] == 1, "Expected fluid/item calls 2/1; got " + fluidCalls[0] + "/" + itemCalls[0]);
        amounts(helper, source, dest, 800, 200);
        helper.succeed();
    }
}
