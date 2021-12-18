package com.teamresourceful.resourcefulbees.common.blockentity;

import com.teamresourceful.resourcefulbees.common.inventory.menus.ListeningMenu;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.SyncGUIMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ISyncableGUI extends MenuProvider {

    BlockPos getBlockPos();

    @Nullable Level getLevel();

    List<ServerPlayer> getListeners();

    CompoundTag getSyncData();

    void readSyncData(@NotNull CompoundTag tag);

    /**
     * Sends {@link GUISyncedBlockEntity#getSyncData()} to the player specified.
     * @param player The player in which you want to send the data to.
     */
    default void sendToPlayer(ServerPlayer player) {
        if (getLevel() == null || getLevel().isClientSide) return;
        NetPacketHandler.sendToPlayer(new SyncGUIMessage(this), player);
    }

    /**
     * Sends {@link GUISyncedBlockEntity#getSyncData()} to all players tracking that chunk.
     */
    default void sendToPlayersTrackingChunk(){
        if (getLevel() == null || getLevel().isClientSide) return;
        NetPacketHandler.sendToAllLoaded(new SyncGUIMessage(this), getLevel(), getBlockPos());
    }

    /**
     * Sends {@link GUISyncedBlockEntity#getSyncData()} to all players within a range specified.
     * @param range the range in which to get players to send the data to.
     */
    default void sendToPlayersInRange(double range){
        if (getLevel() == null || getLevel().isClientSide) return;
        NetPacketHandler.sendToPlayersInRange(new SyncGUIMessage(this), getLevel(), getBlockPos(), range);
    }

    /**
     * Sends {@link GUISyncedBlockEntity#getSyncData()} to all players listening to the block.
     * This will only work if {@link GUISyncedBlockEntity#addListeningPlayer(ServerPlayer)} has been called some where to add players listening.
     * @implNote See {@link ListeningMenu#addListener()} for where listeners are added.
     */
    default void sendToListeningPlayers() {
        if (getLevel() == null || getLevel().isClientSide) return;
        NetPacketHandler.sendToPlayers(new SyncGUIMessage(this), getListeners());
    }

    default void addListeningPlayer(@NotNull ServerPlayer player) {
        getListeners().add(player);
    }

    default void removeListeningPlayer(@NotNull ServerPlayer player) {
        getListeners().remove(player);
    }
}
