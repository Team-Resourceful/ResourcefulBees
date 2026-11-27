package com.teamresourceful.resourcefulbees_test.entities;

import com.teamresourceful.resourcefulbees.common.logistics.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Phase 3B.7A: instant energy transfer and unified scheduling regression tests.
 */
public final class CourierEnergyTransferGameTests {
    private CourierEnergyTransferGameTests() {
    }

    private static SimpleEnergyHandler battery(int capacity, int initial) {
        SimpleEnergyHandler handler = new SimpleEnergyHandler(capacity);
        try (Transaction tx = Transaction.openRoot()) {
            int inserted = handler.insert(initial, tx);
            if (inserted != initial) throw new AssertionError("Battery fixture fill failed: " + inserted);
            tx.commit();
        }
        return handler;
    }

    private static void amounts(GameTestHelper h, SimpleEnergyHandler source, SimpleEnergyHandler destination, int expectedSource, int expectedDestination) {
        h.assertTrue(source.getAmountAsInt() == expectedSource && destination.getAmountAsInt() == expectedDestination, "Energy mismatch: source=" + source.getAmountAsInt() + ", destination=" + destination.getAmountAsInt() + ", expected=" + expectedSource + "/" + expectedDestination);
    }

    private static void expect(GameTestHelper h, TransferResult actual, TransferStatus status, int moved) {
        h.assertTrue(actual.status() == status && actual.itemsMoved() == moved, "Expected " + status + "/" + moved + ", got " + actual.status() + "/" + actual.itemsMoved());
    }

    private static CourierRoute route(long id, CourierResourceType type, int priority, boolean enabled) {
        return new CourierRoute(new UUID(0, id), type, new CourierEndpoint(new BlockPos(2, 2, 2), Direction.UP), new CourierEndpoint(new BlockPos(4, 2, 2), Direction.UP), enabled, priority, CourierRouteExecutionMode.INSTANT, CourierFilter.NONE);
    }

    public static void energyInstantTransfer(GameTestHelper h) {
        var source = battery(2000, 1500);
        var destination = battery(2000, 0);
        expect(h, CourierEnergyTransferEngine.transferEnergy(source, destination, 1000), TransferStatus.SUCCESS, 1000);
        amounts(h, source, destination, 500, 1000);
        h.succeed();
    }

    public static void energyTransferLimit(GameTestHelper h) {
        var source = battery(2000, 1500);
        var destination = battery(2000, 0);
        expect(h, CourierEnergyTransferEngine.transferEnergy(source, destination, 250), TransferStatus.SUCCESS, 250);
        amounts(h, source, destination, 1250, 250);
        expect(h, CourierEnergyTransferEngine.transferEnergy(source, destination, 0), TransferStatus.INVALID_LIMIT, 0);
        amounts(h, source, destination, 1250, 250);
        h.succeed();
    }

    public static void energyPartialDestination(GameTestHelper h) {
        var source = battery(2000, 1500);
        var destination = battery(1000, 750);
        expect(h, CourierEnergyTransferEngine.transferEnergy(source, destination, 800), TransferStatus.PARTIAL, 250);
        amounts(h, source, destination, 1250, 1000);
        h.succeed();
    }

    public static void energyPartialSource(GameTestHelper h) {
        var source = battery(1000, 300);
        var destination = battery(2000, 0);
        expect(h, CourierEnergyTransferEngine.transferEnergy(source, destination, 1000), TransferStatus.PARTIAL, 300);
        amounts(h, source, destination, 0, 300);
        h.succeed();
    }

    public static void energyDestinationFull(GameTestHelper h) {
        var source = battery(2000, 1200);
        var destination = battery(1000, 1000);
        expect(h, CourierEnergyTransferEngine.transferEnergy(source, destination, 800), TransferStatus.DESTINATION_FULL, 0);
        amounts(h, source, destination, 1200, 1000);
        h.succeed();
    }

    public static void energyEmptyAndSelfTransfer(GameTestHelper h) {
        var source = battery(1000, 0);
        var destination = battery(1000, 0);
        expect(h, CourierEnergyTransferEngine.transferEnergy(source, destination, 500), TransferStatus.SOURCE_EMPTY, 0);
        expect(h, CourierEnergyTransferEngine.transferEnergy(source, source, 500), TransferStatus.DESTINATION_UNAVAILABLE, 0);
        amounts(h, source, destination, 0, 0);
        h.succeed();
    }

    public static void energySchedulerExecution(GameTestHelper h) {
        var scheduler = new CourierRouteScheduler();
        scheduler.setInterval(20);
        CourierRoute low = route(1, CourierResourceType.ENERGY, 1, true);
        CourierRoute highA = route(2, CourierResourceType.ENERGY, 5, true);
        CourierRoute highB = route(3, CourierResourceType.ENERGY, 5, true);
        CourierRoute disabled = route(4, CourierResourceType.ENERGY, 10, false);
        CourierRoute item = route(5, CourierResourceType.ITEM, 20, true);
        Map<UUID, CourierRoute> routes = new LinkedHashMap<>();
        for (CourierRoute r : new CourierRoute[]{low, highA, highB, disabled, item}) routes.put(r.id(), r);
        var source = battery(5000, 4000);
        var destination = battery(5000, 0);
        AtomicInteger executions = new AtomicInteger();
        java.util.List<UUID> selected = new java.util.ArrayList<>();
        for (long tick : new long[]{100, 101, 120, 140}) {
            scheduler.tick(routes, tick, r -> r.resourceType() == CourierResourceType.ENERGY && r.executionMode() == CourierRouteExecutionMode.INSTANT, r -> {
                selected.add(r.id());
                executions.incrementAndGet();
                var result = CourierEnergyTransferEngine.transferEnergy(source, destination, 200);
                expect(h, result, TransferStatus.SUCCESS, 200);
            });
        }
        h.assertTrue(executions.get() == 3, "Scheduler should execute once per interval, got " + executions.get());
        h.assertTrue(selected.equals(java.util.List.of(highA.id(), highB.id(), highA.id())), "Expected highest-priority UUID round-robin, got " + selected);
        amounts(h, source, destination, 3400, 600);
        h.succeed();
    }
}
