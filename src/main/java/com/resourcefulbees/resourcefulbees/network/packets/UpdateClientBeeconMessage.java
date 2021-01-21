package com.resourcefulbees.resourcefulbees.network.packets;

import com.resourcefulbees.resourcefulbees.tileentity.EnderBeeconTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateClientBeeconMessage {
    private final BlockPos pos;
    private final CompoundNBT data;

    public UpdateClientBeeconMessage(BlockPos pos, CompoundNBT data){
        this.pos = pos;
        this.data = data;
    }

    public static void encode(UpdateClientBeeconMessage message, PacketBuffer buffer){
        buffer.writeBlockPos(message.pos);
        buffer.writeCompoundTag(message.data);
    }

    public static UpdateClientBeeconMessage decode(PacketBuffer buffer){
        return new UpdateClientBeeconMessage(buffer.readBlockPos(), buffer.readCompoundTag());
    }

    public static void handle(UpdateClientBeeconMessage message, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> {
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if (player != null) {
                if (player.world.isBlockLoaded(message.pos)) {
                    TileEntity tileEntity = player.world.getTileEntity(message.pos);
                    if (tileEntity instanceof EnderBeeconTileEntity) {
                        EnderBeeconTileEntity beeconTileEntity = (EnderBeeconTileEntity) tileEntity;
                        beeconTileEntity.readNBT(message.data);
                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}


