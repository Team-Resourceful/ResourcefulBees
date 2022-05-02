package com.teamresourceful.resourcefulbees.common.network.packets.centrifuge;

import com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge.CentrifugeTerminalScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record CommandResponseMessage(Component response) {

    public static void encode(CommandResponseMessage message, FriendlyByteBuf buffer) {
        buffer.writeComponent(message.response);
    }

    public static CommandResponseMessage decode(FriendlyByteBuf buffer) {
        return new CommandResponseMessage(buffer.readComponent());
    }

    public static void handle(CommandResponseMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (Minecraft.getInstance().screen instanceof CentrifugeTerminalScreen terminal) {
                terminal.sendResponse(message.response);
            }
        });
        context.get().setPacketHandled(true);
    }
}
