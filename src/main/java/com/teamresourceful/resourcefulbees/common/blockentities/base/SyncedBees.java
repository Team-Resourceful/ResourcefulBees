package com.teamresourceful.resourcefulbees.common.blockentities.base;

import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefulbees.common.networking.packets.server.SyncBeesPacket;
import com.teamresourceful.resourcefulbees.common.networking.packets.server.SyncGuiPacket;
import net.minecraft.server.level.ServerPlayer;

public interface SyncedBees extends SyncedGUI {
    @Override
    default void sendToPlayersTrackingChunk() {
        if (getLevel() == null || getLevel().isClientSide()) return;
        NetworkHandler.NETWORK.sendToAllLoaded(new SyncBeesPacket(this), getLevel(), getBlockPos());
    }

    @Override
    default void sendToPlayer(ServerPlayer player) {
        if (getLevel() == null || getLevel().isClientSide()) return;
        NetworkHandler.NETWORK.sendToPlayer(new SyncBeesPacket(this), player);
    }
}
