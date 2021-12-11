package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.commands;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.CentrifugeController;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.centrifuge.CommandResponseMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;

public record CentrifugeCommandSource(CentrifugeController controller, ServerPlayer player) {

    public CentrifugeController getController() {
        return controller;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public void sendError(String message) {
        sendError(new TextComponent(message));
    }

    public void sendMessage(String message) {
        sendMessage(new TextComponent(message));
    }

    public void sendError(Component component) {
        sendMessage(component.copy().withStyle(ChatFormatting.RED));
    }

    public void sendMessage(Component component) {
        NetPacketHandler.sendToPlayer(new CommandResponseMessage(component), this.player);
    }
}
