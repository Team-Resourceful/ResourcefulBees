package com.teamresourceful.resourcefulbees.client.screen;

import com.teamresourceful.resourcefulbees.common.logistics.CourierEndpoint;
import com.teamresourceful.resourcefulbees.common.networking.packets.server.OpenCourierRouteSelectorPacket;
import net.minecraft.client.Minecraft;

import java.util.EnumSet;

public final class OpenCourierRouteSelectorClientHandler {

    private OpenCourierRouteSelectorClientHandler() {
    }

    public static void handle(OpenCourierRouteSelectorPacket message) {
        Minecraft.getInstance().gui.setScreen(
                new HoneyDipperRouteTypeScreen(
                        new CourierEndpoint(
                                message.destinationPos(),
                                message.destinationSide()
                        ),
                        EnumSet.copyOf(message.availableTypes()),
                        message.hand()
                )
        );
    }
}