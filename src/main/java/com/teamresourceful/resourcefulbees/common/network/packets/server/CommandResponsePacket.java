package com.teamresourceful.resourcefulbees.common.network.packets.server;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge.CentrifugeTerminalScreen;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public record CommandResponsePacket(Component response) implements Packet<CommandResponsePacket> {

    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "centrifuge_command_response");
    public static final Handler HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<CommandResponsePacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<CommandResponsePacket> {

        @Override
        public void encode(CommandResponsePacket message, FriendlyByteBuf buffer) {
            buffer.writeComponent(message.response);
        }

        @Override
        public CommandResponsePacket decode(FriendlyByteBuf buffer) {
            return new CommandResponsePacket(buffer.readComponent());
        }

        @Override
        public PacketContext handle(CommandResponsePacket message) {
            return (player, level) -> {
                if (Minecraft.getInstance().screen instanceof CentrifugeTerminalScreen terminal) {
                    terminal.sendResponse(message.response);
                }
            };
        }
    }
}
