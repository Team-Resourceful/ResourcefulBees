package com.teamresourceful.resourcefulbees.common.network;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.network.packets.client.*;
import com.teamresourceful.resourcefulbees.common.network.packets.server.CommandResponsePacket;
import com.teamresourceful.resourcefulbees.common.network.packets.server.SyncCapabilityPacket;
import com.teamresourceful.resourcefulbees.common.network.packets.server.SyncGuiPacket;
import com.teamresourceful.resourcefullib.common.networking.NetworkChannel;
import com.teamresourceful.resourcefullib.common.networking.base.NetworkDirection;

public final class NetPacketHandler {

    private NetPacketHandler() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final NetworkChannel CHANNEL = new NetworkChannel(ResourcefulBees.MOD_ID, 0, "main");

    public static void init() {
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, LockBeePacket.ID, LockBeePacket.HANDLER, LockBeePacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, BeeconChangePacket.ID, BeeconChangePacket.HANDLER, BeeconChangePacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, SelectFluidPacket.ID, SelectFluidPacket.HANDLER, SelectFluidPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, CommandPacket.ID, CommandPacket.HANDLER, CommandPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, LockBeePacket.ID, LockBeePacket.HANDLER, LockBeePacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, FindBeePacket.ID, FindBeePacket.HANDLER, FindBeePacket.class);

        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, SyncGuiPacket.ID, SyncGuiPacket.HANDLER, SyncGuiPacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, CommandResponsePacket.ID, CommandResponsePacket.HANDLER, CommandResponsePacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, SyncCapabilityPacket.ID, SyncCapabilityPacket.HANDLER, SyncCapabilityPacket.class);
    }
}
