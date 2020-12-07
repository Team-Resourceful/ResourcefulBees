package com.resourcefulbees.resourcefulbees.network.packets;

import com.resourcefulbees.resourcefulbees.tileentity.CentrifugeTileEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateRedstoneReqMessage {
    private final BlockPos pos;


    public UpdateRedstoneReqMessage(BlockPos pos){
        this.pos = pos;
    }

    public static void encode(UpdateRedstoneReqMessage message, PacketBuffer buffer){
        buffer.writeBlockPos(message.pos);
    }

    public static UpdateRedstoneReqMessage decode(PacketBuffer buffer){
        return new UpdateRedstoneReqMessage(buffer.readBlockPos());
    }

    public static void handle(UpdateRedstoneReqMessage message, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> {
            ServerPlayerEntity player = context.get().getSender();
            if (player != null) {
                if (player.world.isBlockPresent(message.pos)) {
                    TileEntity tileEntity = player.world.getTileEntity(message.pos);
                    if (tileEntity instanceof CentrifugeTileEntity) {
                        ((CentrifugeTileEntity) tileEntity).updateRequiresRedstone();
                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
