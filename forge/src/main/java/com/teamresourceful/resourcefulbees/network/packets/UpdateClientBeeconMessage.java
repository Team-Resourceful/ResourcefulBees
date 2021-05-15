package com.teamresourceful.resourcefulbees.network.packets;

import com.teamresourceful.resourcefulbees.tileentity.EnderBeeconTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateClientBeeconMessage {
    private final BlockPos pos;
    private final CompoundTag data;

    public UpdateClientBeeconMessage(BlockPos pos, CompoundTag data){
        this.pos = pos;
        this.data = data;
    }

    public static void encode(UpdateClientBeeconMessage message, FriendlyByteBuf buffer){
        buffer.writeBlockPos(message.pos);
        buffer.writeNbt(message.data);
    }

    public static UpdateClientBeeconMessage decode(FriendlyByteBuf buffer){
        return new UpdateClientBeeconMessage(buffer.readBlockPos(), buffer.readNbt());
    }

    public static void handle(UpdateClientBeeconMessage message, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> {
            AbstractClientPlayer player = Minecraft.getInstance().player;
            if (player != null && player.level.isLoaded(message.pos)) {
                BlockEntity tileEntity = player.level.getBlockEntity(message.pos);
                if (tileEntity instanceof EnderBeeconTileEntity) {
                    EnderBeeconTileEntity beeconTileEntity = (EnderBeeconTileEntity) tileEntity;
                    beeconTileEntity.readNBT(message.data);
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}


