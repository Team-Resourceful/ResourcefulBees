package com.teamresourceful.resourcefulbees.network.packets;

import com.teamresourceful.resourcefulbees.tileentity.EnderBeeconTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateBeeconMessage {

    private final ResourceLocation effectLocation;
    private final boolean active;
    private final BlockPos blockPos;

    public UpdateBeeconMessage(ResourceLocation effectName, boolean active, BlockPos blockPos) {
        this.effectLocation = effectName;
        this.active = active;
        this.blockPos = blockPos;
    }

    public static void encode(UpdateBeeconMessage message, FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(message.effectLocation);
        buffer.writeBoolean(message.active);
        buffer.writeBlockPos(message.blockPos);
    }

    public static UpdateBeeconMessage decode(FriendlyByteBuf buffer) {
        return new UpdateBeeconMessage(buffer.readResourceLocation(), buffer.readBoolean(), buffer.readBlockPos());
    }

    public static void handle(UpdateBeeconMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null && player.level.isLoaded(message.blockPos)){
                BlockEntity tileEntity = player.level.getBlockEntity(message.blockPos);
                if (tileEntity instanceof EnderBeeconTileEntity) {
                    EnderBeeconTileEntity beecon = (EnderBeeconTileEntity) tileEntity;
                    beecon.updateBeeconEffect(message.effectLocation, message.active);
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
