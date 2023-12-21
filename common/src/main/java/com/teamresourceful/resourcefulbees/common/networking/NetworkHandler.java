package com.teamresourceful.resourcefulbees.common.networking;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.networking.packets.client.BeeconChangePacket;
import com.teamresourceful.resourcefulbees.common.networking.packets.client.FindBeePacket;
import com.teamresourceful.resourcefulbees.common.networking.packets.client.LockBeePacket;
import com.teamresourceful.resourcefulbees.common.networking.packets.client.SelectFluidPacket;
import com.teamresourceful.resourcefulbees.common.networking.packets.server.DimensionalBeesPacket;
import com.teamresourceful.resourcefulbees.common.networking.packets.server.SyncBeepediaPacket;
import com.teamresourceful.resourcefulbees.common.networking.packets.server.SyncGuiPacket;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.networking.NetworkChannel;
import com.teamresourceful.resourcefullib.common.networking.base.NetworkDirection;

public final class NetworkHandler {

    private NetworkHandler() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final NetworkChannel CHANNEL = new NetworkChannel(ModConstants.MOD_ID, 0, "main");

    public static void init() {
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, BeeconChangePacket.ID, BeeconChangePacket.HANDLER, BeeconChangePacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, SelectFluidPacket.ID, SelectFluidPacket.HANDLER, SelectFluidPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, FindBeePacket.ID, FindBeePacket.HANDLER, FindBeePacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, LockBeePacket.ID, LockBeePacket.HANDLER, LockBeePacket.class);

        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, DimensionalBeesPacket.ID, DimensionalBeesPacket.HANDLER, DimensionalBeesPacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, SyncBeepediaPacket.ID, SyncBeepediaPacket.HANDLER, SyncBeepediaPacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, SyncGuiPacket.ID, SyncGuiPacket.HANDLER, SyncGuiPacket.class);
    }

}
