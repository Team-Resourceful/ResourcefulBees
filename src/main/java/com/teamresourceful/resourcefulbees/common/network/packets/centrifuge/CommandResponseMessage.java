package com.teamresourceful.resourcefulbees.common.network.packets.centrifuge;

import com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge.CentrifugeTerminalScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CommandResponseMessage {

    private final ITextComponent response;

    public CommandResponseMessage(ITextComponent response) {
        this.response = response;
    }

    public static void encode(CommandResponseMessage message, PacketBuffer buffer){
        buffer.writeComponent(message.response);
    }

    public static CommandResponseMessage decode(PacketBuffer buffer){
        return new CommandResponseMessage(buffer.readComponent());
    }

    public static void handle(CommandResponseMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.screen instanceof CentrifugeTerminalScreen) {
                ((CentrifugeTerminalScreen) minecraft.screen).sendResponse(message.response);
            }
        });
        context.get().setPacketHandled(true);
    }
}
