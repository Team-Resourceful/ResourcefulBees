package com.teamresourceful.resourcefulbees.common.logistics;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.VisibleForDebug;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.Objects;

/** Transactional, instantaneous energy transfer for colony routes. */
public final class CourierEnergyTransferEngine {

    public static final int DEFAULT_TRANSFER_LIMIT = 1_000;

    private CourierEnergyTransferEngine() {
    }

    public static TransferResult transfer(ServerLevel level, CourierRoute route, int maxEnergy) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(route, "route");

        var invalid = validate(route, maxEnergy);
        if (invalid != null) return invalid;

        if (route.source().pos().equals(route.destination().pos())) {
            return result(TransferStatus.SOURCE_UNAVAILABLE, 0);
        }
        if (!level.hasChunkAt(route.source().pos())) {
            return result(TransferStatus.SOURCE_UNAVAILABLE, 0);
        }
        if (!level.hasChunkAt(route.destination().pos())) {
            return result(TransferStatus.DESTINATION_UNAVAILABLE, 0);
        }

        EnergyHandler source = level.getCapability(
                Capabilities.Energy.BLOCK, route.source().pos(), route.source().side());
        if (source == null) {
            return result(TransferStatus.SOURCE_UNAVAILABLE, 0);
        }
        EnergyHandler destination = level.getCapability(
                Capabilities.Energy.BLOCK, route.destination().pos(), route.destination().side());
        if (destination == null || destination == source) {
            return result(TransferStatus.DESTINATION_UNAVAILABLE, 0);
        }
        return transferEnergy(source, destination, maxEnergy);
    }


    @VisibleForDebug
    public static TransferResult transferEnergy(EnergyHandler source, EnergyHandler destination, int maxEnergy) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(destination, "destination");

        if (maxEnergy <= 0) {
            return result(
                    TransferStatus.INVALID_LIMIT, 0);
        }

        if (source == destination) {
            return result(
                    TransferStatus.DESTINATION_UNAVAILABLE, 0);
        }

        if (source.getAmountAsInt() <= 0) {
            return result(
                    TransferStatus.SOURCE_EMPTY, 0);
        }

        int requested = Math.min(maxEnergy, source.getAmountAsInt());

        // Probe the destination without committing any energy.
        int accepted;
        try (Transaction probe = Transaction.openRoot()) {
            accepted = destination.insert(requested, probe);
        }

        if (accepted <= 0) {
            return result(
                    TransferStatus.DESTINATION_FULL, 0);
        }

        int attempt = Math.min(requested, accepted);

        // First attempt, followed by a retry if the handlers
        // accepted different quantities.
        for (int retry = 0; retry < 2 && attempt > 0; retry++) {
            int extracted;
            int inserted;

            try (Transaction transaction = Transaction.openRoot()) {
                extracted = source.extract(attempt, transaction);

                if (extracted <= 0) {
                    return result(
                            TransferStatus.SOURCE_EMPTY, 0);
                }

                inserted = destination.insert(extracted, transaction);

                if (inserted == extracted) {
                    transaction.commit();

                    var status = inserted == maxEnergy
                            ? TransferStatus.SUCCESS
                            : TransferStatus.PARTIAL;

                    return result(status, inserted);
                }

                // No commit: both changes roll back.
            }

            // Retry only the quantity both handlers
            // demonstrated they could transfer.
            attempt = Math.min(extracted, inserted);
        }

        return result(
                TransferStatus.DESTINATION_FULL, 0);
    }

    private static TransferResult validate(CourierRoute route, int maxEnergy) {
        if (!route.enabled()) {
            return result(TransferStatus.DISABLED, 0);
        }
        if (route.resourceType() != CourierResourceType.ENERGY
                || route.executionMode() != CourierRouteExecutionMode.INSTANT) {
            return result(TransferStatus.UNSUPPORTED_RESOURCE, 0);
        }
        if (maxEnergy <= 0) {
            return result(TransferStatus.INVALID_LIMIT, 0);
        }
        return null;
    }

    private static TransferResult result(
            TransferStatus status, int moved
    ) {
        return new TransferResult(status, moved);
    }
}
