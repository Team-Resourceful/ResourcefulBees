package com.resourcefulbees.resourcefulbees.network.packets;

import com.resourcefulbees.resourcefulbees.block.EnderBeecon;
import com.resourcefulbees.resourcefulbees.tileentity.EnderBeeconTileEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
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

    public static void encode(UpdateBeeconMessage message, PacketBuffer buffer) {
        buffer.writeResourceLocation(message.effectLocation);
        buffer.writeBoolean(message.active);
        buffer.writeBlockPos(message.blockPos);
    }

    public static UpdateBeeconMessage decode(PacketBuffer buffer) {
        return new UpdateBeeconMessage(buffer.readResourceLocation(), buffer.readBoolean(), buffer.readBlockPos());
    }

    public static void handle(UpdateBeeconMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayerEntity player = context.get().getSender();
            if (player != null) {
                if (player.world.isBlockLoaded(message.blockPos)){
                    TileEntity tileEntity = player.world.getTileEntity(message.blockPos);
                    if (tileEntity instanceof EnderBeeconTileEntity) {
                        EnderBeeconTileEntity beecon = (EnderBeeconTileEntity) tileEntity;
                        beecon.updateBeeconEffect(message.effectLocation, message.active);
                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
