package com.teamresourceful.resourcefulbees.common.networking;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.networking.packets.client.*;
import com.teamresourceful.resourcefulbees.common.networking.packets.server.*;
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
        NETWORK.register(FindBeePacket.PACKET_TYPE);
        NETWORK.register(LockBeePacket.PACKET_TYPE);
        NETWORK.register(ConfirmCourierRoutePacket.PACKET_TYPE);
        NETWORK.register(UpdateColonyRoutePacket.PACKET_TYPE);
        NETWORK.register(SetColonyControllerTabPacket.PACKET_TYPE);
        NETWORK.register(BeginCourierRouteEditPacket.PACKET_TYPE);
        NETWORK.register(SetCourierSchedulingPacket.PACKET_TYPE);

        //clientbound
        NETWORK.register(DimensionalBeesPacket.PACKET_TYPE);
        NETWORK.register(SyncBeepediaPacket.PACKET_TYPE);
        NETWORK.register(SyncBlockDataPacket.PACKET_TYPE);
        NETWORK.register(OpenCourierRouteSelectorPacket.PACKET_TYPE);
        NETWORK.register(SyncColonyRoutesPacket.PACKET_TYPE);
        NETWORK.register(SyncColonyCouriersPacket.PACKET_TYPE);
    }
}
