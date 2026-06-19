package com.teamresourceful.resourcefulbees.common.networking;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
//import com.teamresourceful.resourcefulbees.common.networking.packets.client.BeeconChangePacket;
//import com.teamresourceful.resourcefulbees.common.networking.packets.client.FindBeePacket;
import com.teamresourceful.resourcefulbees.common.networking.packets.client.LockBeePacket;
import com.teamresourceful.resourcefulbees.common.networking.packets.server.DimensionalBeesPacket;
import com.teamresourceful.resourcefulbees.common.networking.packets.server.SyncBeepediaPacket;
import com.teamresourceful.resourcefulbees.common.networking.packets.server.SyncGuiPacket;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.network.Network;
import net.minecraft.resources.Identifier;

public final class NetworkHandler {

    private NetworkHandler() throws UtilityClassException {
        throw new UtilityClassException();
    }

    private static final Identifier MAIN_CHANNEL = Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "main");
    public static final Network NETWORK = new Network(MAIN_CHANNEL, 0, true);

    public static void init() {
        // serverbound
        //NETWORK.register(BeeconChangePacket.PACKET_TYPE);
        // todo NETWORK.register(SelectableFluidPacket.PACKET_TYPE);
        //NETWORK.register(FindBeePacket.PACKET_TYPE);
        NETWORK.register(LockBeePacket.PACKET_TYPE);

        //clientbound
        NETWORK.register(DimensionalBeesPacket.PACKET_TYPE);
        NETWORK.register(SyncBeepediaPacket.PACKET_TYPE);
        NETWORK.register(SyncGuiPacket.PACKET_TYPE);
    }
}
