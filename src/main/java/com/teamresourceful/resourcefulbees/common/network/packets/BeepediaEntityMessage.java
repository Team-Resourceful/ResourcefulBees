package com.teamresourceful.resourcefulbees.common.network.packets;

import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class BeepediaEntityMessage {

    public final ResourceLocation id;
    public final BlockPos currentPos;
    public final BlockPos hivePos;
    public final BlockPos flowerPos;
    public final float health;
    public final boolean hasPollen;
    public final ITextComponent name;

    public BeepediaEntityMessage(ResourceLocation id, BlockPos currentPos, BlockPos hivePos, BlockPos flowerPos, float health, boolean hasPollen, ITextComponent name) {
        this.id = id;
        this.currentPos = currentPos;
        this.hivePos = hivePos;
        this.flowerPos = flowerPos;
        this.health = health;
        this.hasPollen = hasPollen;
        this.name = name;
    }

    public static void encode(BeepediaEntityMessage message, PacketBuffer buffer){
        buffer.writeResourceLocation(message.id);
        buffer.writeBlockPos(message.currentPos);
        buffer.writeBlockPos(message.hivePos);
        buffer.writeBlockPos(message.flowerPos);
        buffer.writeFloat(message.health);
        buffer.writeBoolean(message.hasPollen);
        buffer.writeComponent(message.name);
    }

    public static BeepediaEntityMessage decode(PacketBuffer buffer){
        return new BeepediaEntityMessage(buffer.readRegistryId(),
                buffer.readBlockPos(),
                buffer.readBlockPos(),
                buffer.readBlockPos(),
                buffer.readFloat(),
                buffer.readBoolean(),
                buffer.readComponent());
    }

    public static void handle(BeepediaEntityMessage message, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> {
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if (player != null && player.level.isLoaded(message.currentPos)) {
                BeepediaScreen.receiveBeeMessage(message);
            }
        });
        context.get().setPacketHandled(true);
    }
}
