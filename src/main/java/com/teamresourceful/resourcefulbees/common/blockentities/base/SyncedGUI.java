package com.teamresourceful.resourcefulbees.common.blockentities.base;

import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefulbees.common.networking.packets.server.SyncBlockDataPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface SyncedGUI extends MenuProvider {

    BlockPos getBlockPos();

    @Nullable Level getLevel();

    ProblemReporter.PathElement problemPath();

    List<ServerPlayer> getListeners();

    DataComponentPatch getSyncData();

    <Data> void setSyncData(DataComponentType<Data> type, Optional<Data> data);

    default void setSyncData(DataComponentPatch patch) {
        for (var entry : patch.entrySet()) {
            applyPatch(entry.getKey(), patch);
        }
    }

    private <Data> void applyPatch(DataComponentType<@NotNull Data> type, DataComponentPatch patch) {
        var data = patch.getPatch(type);
        if (data == null) return;
        setSyncData(type, data);
    }

    /**
     * Sends {@link SyncedGUI#getSyncData()} to the player specified.
     * @param player The player in which you want to send the data to.
     */
    default void sendToPlayer(ServerPlayer player) {
        if (getLevel() == null || getLevel().isClientSide()) return;
        NetworkHandler.NETWORK.sendToPlayer(new SyncBlockDataPacket(this), player);
    }

    /**
     * Sends {@link SyncedGUI#getSyncData()} to all players tracking that chunk.
     */
    default void sendToPlayersTrackingChunk(){
        if (getLevel() == null || getLevel().isClientSide()) return;
        NetworkHandler.NETWORK.sendToAllLoaded(new SyncBlockDataPacket(this), getLevel(), getBlockPos());
    }

    /**
     * Sends {@link SyncedGUI#getSyncData()} to all players within a range specified.
     * @param range the range in which to get players to send the data to.
     */
    default void sendToPlayersInRange(double range){
        if (getLevel() == null || getLevel().isClientSide()) return;
        NetworkHandler.NETWORK.sendToPlayersInRange(new SyncBlockDataPacket(this), getLevel(), getBlockPos(), range);
    }

    /**
     * Sends {@link SyncedGUI#getSyncData()} to all players listening to the block.
     * This will only work if {@link GUISyncedBlockEntity#addListeningPlayer(ServerPlayer)} has been called somewhere to add players listening.
     */
    default void sendToListeningPlayers() {
        if (getLevel() == null || getLevel().isClientSide()) return;
        NetworkHandler.NETWORK.sendToPlayers(new SyncBlockDataPacket(this), getListeners());
    }

    default void addListeningPlayer(@NotNull ServerPlayer player) {
        getListeners().add(player);
    }

    default void removeListeningPlayer(@NotNull ServerPlayer player) {
        getListeners().remove(player);
    }
}
