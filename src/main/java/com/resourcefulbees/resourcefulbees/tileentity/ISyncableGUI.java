package com.resourcefulbees.resourcefulbees.tileentity;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;

public interface ISyncableGUI extends INamedContainerProvider {
    void sendGUINetworkPacket(IContainerListener player);
    void handleGUINetworkPacket(PacketBuffer buffer);
    default void sendInitGUIPacket(ServerPlayerEntity player) {}
    default void handleInitGUIPacket(PacketBuffer buffer) {}
}