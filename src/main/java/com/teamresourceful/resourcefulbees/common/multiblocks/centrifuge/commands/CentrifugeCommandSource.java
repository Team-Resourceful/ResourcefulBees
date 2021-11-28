package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.commands;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.CentrifugeController;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.centrifuge.CommandResponseMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class CentrifugeCommandSource {

    private final CentrifugeController controller;
    private final ServerPlayerEntity player;

    public CentrifugeCommandSource(CentrifugeController controller, ServerPlayerEntity player) {
        this.controller = controller;
        this.player = player;
    }

    public CentrifugeController getController() {
        return controller;
    }

    public ServerPlayerEntity getPlayer() {
        return player;
    }

    public void sendError(String message) {
        sendError(new StringTextComponent(message));
    }

    public void sendMessage(String message) {
        sendMessage(new StringTextComponent(message));
    }

    public void sendError(ITextComponent component) {
        sendMessage(component.copy().withStyle(TextFormatting.RED));
    }

    public void sendMessage(ITextComponent component) {
        NetPacketHandler.sendToPlayer(new CommandResponseMessage(component), this.player);
    }
}
