package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.commands;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.CentrifugeController;
import com.teamresourceful.resourcefulbees.common.network.packets.server.CommandResponsePacket;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public record CentrifugeCommandSource(CentrifugeController controller, ServerPlayer player) {

    public CentrifugeController getController() {
        return controller;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public void sendError(String message) {
        sendError(Component.literal(message));
    }

    public void sendMessage(String message) {
        sendMessage(Component.literal(message));
    }

    public void sendError(Component component) {
        sendMessage(component.copy().withStyle(ChatFormatting.RED));
    }

    public void sendMessage(Component component) {
        NetworkHandler.CHANNEL.sendToPlayer(new CommandResponsePacket(component), this.player);
    }
}
