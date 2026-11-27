package com.teamresourceful.resourcefulbees.common.logistics;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.VisibleForDebug;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/** Selects at most one eligible route per interval; execution is delegated. */
public final class CourierRouteScheduler {
    public static final int DEFAULT_INTERVAL = 20;
    public static final int DEFAULT_TRANSFER_LIMIT = 16;

    private int interval = DEFAULT_INTERVAL;
    private int transferLimit = DEFAULT_TRANSFER_LIMIT;
    private int fluidTransferLimit = CourierFluidTransferEngine.DEFAULT_TRANSFER_LIMIT;
    private long nextExecutionTick = 0;
    private UUID lastExecutedRouteId;
    private Integer lastExecutedPriority;

    /**
     * Generic scheduling entry point. The eligibility predicate must reject routes
     * whose executor is not yet implemented or whose required resources are unavailable.
     * The executor is called at most once per eligible scheduling interval.
     */
    @VisibleForDebug
    public void tick(Map<UUID, CourierRoute> routes, long gameTime, Predicate<CourierRoute> canExecute, Consumer<CourierRoute> execute) {
        Objects.requireNonNull(routes, "routes");
        Objects.requireNonNull(canExecute, "canExecute");
        Objects.requireNonNull(execute, "execute");

        if (gameTime < nextExecutionTick) return;
        nextExecutionTick = gameTime + interval;

        List<CourierRoute> eligible = new ArrayList<>();
        routes.values().stream()
                .filter(CourierRoute::enabled)
                .filter(canExecute)
                .forEach(eligible::add);

        if (eligible.isEmpty()) {
            lastExecutedRouteId = null;
            lastExecutedPriority = null;
            return;
        }

        eligible.sort(Comparator.comparingInt(CourierRoute::priority)
                .reversed().thenComparing(CourierRoute::id));
        int highestPriority = eligible.getFirst().priority();
        List<CourierRoute> candidates = eligible.stream()
                .filter(route -> route.priority() == highestPriority)
                .toList();

        int selectedIndex = 0;
        if (lastExecutedRouteId != null
                && lastExecutedPriority != null
                && lastExecutedPriority == highestPriority) {
            for (int i = 0; i < candidates.size(); i++) {
                if (candidates.get(i).id().compareTo(lastExecutedRouteId) > 0) {
                    selectedIndex = i;
                    break;
                }
            }
        }

        CourierRoute selected = candidates.get(selectedIndex);
        // Advance the cursor before invoking the executor; a failed operation
        // must not cause the scheduler to repeatedly select the same route.
        lastExecutedRouteId = selected.id();
        lastExecutedPriority = selected.priority();
        execute.accept(selected);
    }

    /** Backward-compatible entry point for existing item instant-transfer tests. */
    @VisibleForDebug
    public void tick(ServerLevel level, Map<UUID, CourierRoute> routes, long gameTime) {
        tick(routes, gameTime,
                route -> route.executionMode() == CourierRouteExecutionMode.INSTANT
                        && (route.resourceType() == CourierResourceType.ITEM
                        || route.resourceType() == CourierResourceType.FLUID),
                route -> {
                    switch (route.resourceType()) {
                        case ITEM -> CourierTransferEngine.transfer(level, route, transferLimit);
                        case FLUID -> CourierFluidTransferEngine.transfer(level, route, fluidTransferLimit);
                        case ENERGY -> CourierEnergyTransferEngine.transfer(level, route, CourierEnergyTransferEngine.DEFAULT_TRANSFER_LIMIT);
                    }
                });
    }

    public void tick(ServerLevel level, Map<UUID, CourierRoute> routes) {
        tick(level, routes, level.getGameTime());
    }

    public int getInterval() { return interval; }
    public void setInterval(int interval) { this.interval = Math.max(1, interval); }
    public int getTransferLimit() { return transferLimit; }
    public void setTransferLimit(int transferLimit) { this.transferLimit = Math.max(1, transferLimit); }

    public void reset() {
        nextExecutionTick = 0;
        lastExecutedRouteId = null;
        lastExecutedPriority = null;
    }

    public int getFluidTransferLimit() {
        return fluidTransferLimit;
    }
    public void setFluidTransferLimit(int limit) {
        this.fluidTransferLimit = Math.max(1, limit);
    }
}
