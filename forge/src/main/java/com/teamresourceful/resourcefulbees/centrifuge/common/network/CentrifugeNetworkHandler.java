package com.teamresourceful.resourcefulbees.centrifuge.common.network;

import com.teamresourceful.resourcefulbees.centrifuge.common.network.client.*;
import com.teamresourceful.resourcefulbees.centrifuge.common.network.server.CommandResponsePacket;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.networking.NetworkChannel;
import com.teamresourceful.resourcefullib.common.networking.base.NetworkDirection;

public final class CentrifugeNetworkHandler {

    private CentrifugeNetworkHandler() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final NetworkChannel CHANNEL = new NetworkChannel(ModConstants.MOD_ID, 0, "centrifuge");

    public static void init() {
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, CommandPacket.ID, CommandPacket.HANDLER, CommandPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, OutputLocationSelectionPacket.ID, OutputLocationSelectionPacket.HANDLER, OutputLocationSelectionPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, VoidExcessPacket.ID, VoidExcessPacket.HANDLER, VoidExcessPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, PurgeContentsPacket.ID, PurgeContentsPacket.HANDLER, PurgeContentsPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, SetFilterSlotPacket.ID, SetFilterSlotPacket.HANDLER, SetFilterSlotPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, SwitchGuiPacket.ID, SwitchGuiPacket.HANDLER, SwitchGuiPacket.class);

        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, CommandResponsePacket.ID, CommandResponsePacket.HANDLER, CommandResponsePacket.class);
    }
}
