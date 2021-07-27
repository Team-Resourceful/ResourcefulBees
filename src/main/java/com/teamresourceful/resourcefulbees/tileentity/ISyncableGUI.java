package com.teamresourceful.resourcefulbees.tileentity;

import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;

import java.awt.event.ContainerListener;

public interface ISyncableGUI extends INamedContainerProvider {
    void sendGUINetworkPacket(ContainerListener player);
    void handleGUINetworkPacket(PacketBuffer buffer);
}
