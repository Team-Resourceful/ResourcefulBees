package com.teamresourceful.resourcefulbees.common.network;

import com.teamresourceful.resourcefulbees.centrifuge.common.network.client.*;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.network.packets.client.*;
import com.teamresourceful.resourcefulbees.centrifuge.common.network.server.CommandResponsePacket;
import com.teamresourceful.resourcefulbees.common.network.packets.server.SyncGuiPacket;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefullib.common.networking.base.NetworkDirection;

import static com.teamresourceful.resourcefulbees.common.networking.NetworkHandler.CHANNEL;

public final class ForgeNetworkHandler {

    private ForgeNetworkHandler() {
        throw new UtilityClassError();
    }

    public static void init() {
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, LockBeePacket.ID, LockBeePacket.HANDLER, LockBeePacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, BeeconChangePacket.ID, BeeconChangePacket.HANDLER, BeeconChangePacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, SelectFluidPacket.ID, SelectFluidPacket.HANDLER, SelectFluidPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, CommandPacket.ID, CommandPacket.HANDLER, CommandPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, LockBeePacket.ID, LockBeePacket.HANDLER, LockBeePacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, OutputLocationSelectionPacket.ID, OutputLocationSelectionPacket.HANDLER, OutputLocationSelectionPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, VoidExcessPacket.ID, VoidExcessPacket.HANDLER, VoidExcessPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, PurgeContentsPacket.ID, PurgeContentsPacket.HANDLER, PurgeContentsPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, SetFilterSlotPacket.ID, SetFilterSlotPacket.HANDLER, SetFilterSlotPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, SwitchGuiPacket.ID, SwitchGuiPacket.HANDLER, SwitchGuiPacket.class);

        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, SyncGuiPacket.ID, SyncGuiPacket.HANDLER, SyncGuiPacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, CommandResponsePacket.ID, CommandResponsePacket.HANDLER, CommandResponsePacket.class);
        NetworkHandler.init();
    }
}
