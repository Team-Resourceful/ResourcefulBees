package com.teamresourceful.resourcefulbees.common.tileentity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.ContainerListener;

public interface ISyncableGUI extends MenuProvider {
    void sendGUINetworkPacket(ContainerListener player);
    void handleGUINetworkPacket(FriendlyByteBuf buffer);
    default void sendInitGUIPacket(ServerPlayer player) {}
    default void handleInitGUIPacket(FriendlyByteBuf buffer) {}
}
