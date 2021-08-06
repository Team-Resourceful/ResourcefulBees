package com.teamresourceful.resourcefulbees.common.tileentity;

import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;

public interface ISyncableGUI extends INamedContainerProvider {
    void sendGUINetworkPacket(IContainerListener player);
    void handleGUINetworkPacket(PacketBuffer buffer);
}
