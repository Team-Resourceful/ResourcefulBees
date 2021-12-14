package com.teamresourceful.resourcefulbees.common.tileentity;

import com.teamresourceful.resourcefulbees.common.inventory.containers.ListeningMenu;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.SyncBlockEntityMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class SyncedBlockEntity extends BlockEntity {

    private final List<ServerPlayer> listeners = new ArrayList<>();

    public SyncedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
     * Sends {@link SyncedBlockEntity#getData()} to the player specified.
     * @param player The player in which you want to send the data to.
     */
    public void sendToPlayer(ServerPlayer player) {
        NetPacketHandler.sendToPlayer(new SyncBlockEntityMessage(this), player);
    }

    /**
     * Sends {@link SyncedBlockEntity#getData()} to all players tracking that chunk.
     */
    public void sendToPlayersTrackingChunk(){
        if (getLevel() == null) return;
        NetPacketHandler.sendToAllLoaded(new SyncBlockEntityMessage(this), getLevel(), getBlockPos());
    }

    /**
     * Sends {@link SyncedBlockEntity#getData()} to all players within a range specified.
     * @param range the range in which to get players to send the data to.
     */
    public void sendToPlayersInRange(double range){
        if (getLevel() == null) return;
        NetPacketHandler.sendToPlayersInRange(new SyncBlockEntityMessage(this), getLevel(), getBlockPos(), range);
    }

    /**
     * Sends {@link SyncedBlockEntity#getData()} to all players listening to the block.
     * This will only work if {@link SyncedBlockEntity#addListeningPlayer(ServerPlayer)} has been called some where to add players listening.
     * @implNote See {@link ListeningMenu#addListener()} for where listeners are added.
     */
    public void sendToListeningPlayers() {
        NetPacketHandler.sendToPlayers(new SyncBlockEntityMessage(this), listeners);
    }

    public void addListeningPlayer(@NotNull ServerPlayer player) {
        listeners.add(player);
    }

    public void removeListeningPlayer(@NotNull ServerPlayer player) {
        listeners.remove(player);
    }

    @NotNull
    public abstract CompoundTag getData();
    public abstract void updateData(@NotNull CompoundTag tag);

}
