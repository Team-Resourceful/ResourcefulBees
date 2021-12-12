package com.teamresourceful.resourcefulbees.common.network.packets.centrifuge;

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
            Minecraft minecraft = Minecraft.getInstance();
            /* TODO UNCOMMENT TO WORK ON CENTRIFUGE
            if (minecraft.screen instanceof CentrifugeTerminalScreen) {
                ((CentrifugeTerminalScreen) minecraft.screen).sendResponse(message.response);
            }
             */
        });
        context.get().setPacketHandled(true);
    }
}
