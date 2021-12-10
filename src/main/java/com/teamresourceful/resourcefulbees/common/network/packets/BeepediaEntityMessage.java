package com.teamresourceful.resourcefulbees.common.network.packets;

import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record BeepediaEntityMessage(ResourceLocation id, BlockPos currentPos, BlockPos hivePos, BlockPos flowerPos, float health, boolean hasPollen, Component name) {

    public static void encode(BeepediaEntityMessage message, FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(message.id);
        buffer.writeBlockPos(message.currentPos);
        buffer.writeBlockPos(message.hivePos);
        buffer.writeBlockPos(message.flowerPos);
        buffer.writeFloat(message.health);
        buffer.writeBoolean(message.hasPollen);
        buffer.writeComponent(message.name);
    }

    public static BeepediaEntityMessage decode(FriendlyByteBuf buffer) {
        return new BeepediaEntityMessage(buffer.readRegistryId(),
                buffer.readBlockPos(),
                buffer.readBlockPos(),
                buffer.readBlockPos(),
                buffer.readFloat(),
                buffer.readBoolean(),
                buffer.readComponent());
    }

    public static void handle(BeepediaEntityMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && player.level.isLoaded(message.currentPos)) {
                BeepediaScreen.receiveBeeMessage(message);
            }
        });
        context.get().setPacketHandled(true);
    }
}
