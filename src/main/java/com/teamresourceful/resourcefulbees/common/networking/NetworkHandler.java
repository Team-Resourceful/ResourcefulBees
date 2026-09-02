package com.teamresourceful.resourcefulbees.common.networking;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.networking.packets.client.BeeconEffectPacket;
import com.teamresourceful.resourcefulbees.common.networking.packets.client.BeeconSettingPacket;
import com.teamresourceful.resourcefulbees.common.networking.packets.client.FindBeePacket;
import com.teamresourceful.resourcefulbees.common.networking.packets.client.LockBeePacket;
import com.teamresourceful.resourcefulbees.common.networking.packets.server.DimensionalBeesPacket;
import com.teamresourceful.resourcefulbees.common.networking.packets.server.SyncBeepediaPacket;
import com.teamresourceful.resourcefulbees.common.networking.packets.server.SyncBlockDataPacket;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.network.Network;
import net.minecraft.resources.Identifier;

public final class NetworkHandler {

    private NetworkHandler() throws UtilityClassException {
        throw new UtilityClassException();
    }

    private static final Identifier MAIN_CHANNEL = ModIdentifier.of("main");
    public static final Network NETWORK = new Network(MAIN_CHANNEL, 0, true);

    public static void init() {
        // serverbound
        NETWORK.register(BeeconEffectPacket.PACKET_TYPE);
        NETWORK.register(BeeconSettingPacket.PACKET_TYPE);
        // todo NETWORK.register(SelectableFluidPacket.PACKET_TYPE);
        NETWORK.register(FindBeePacket.PACKET_TYPE);
        NETWORK.register(LockBeePacket.PACKET_TYPE);

        //clientbound
        NETWORK.register(DimensionalBeesPacket.PACKET_TYPE);
        NETWORK.register(SyncBeepediaPacket.PACKET_TYPE);
        NETWORK.register(SyncBlockDataPacket.PACKET_TYPE);
    }
}
