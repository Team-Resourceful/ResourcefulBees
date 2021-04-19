package com.resourcefulbees.resourcefulbees.network.packets;

import com.resourcefulbees.resourcefulbees.tileentity.CentrifugeTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateRedstoneReqMessage {
    private final BlockPos pos;


    public UpdateRedstoneReqMessage(BlockPos pos){
        this.pos = pos;
    }

    public static void encode(UpdateRedstoneReqMessage message, FriendlyByteBuf buffer){
        buffer.writeBlockPos(message.pos);
    }

    public static UpdateRedstoneReqMessage decode(FriendlyByteBuf buffer){
        return new UpdateRedstoneReqMessage(buffer.readBlockPos());
    }

    public static void handle(UpdateRedstoneReqMessage message, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null && player.level.isLoaded(message.pos)) {
                BlockEntity tileEntity = player.level.getBlockEntity(message.pos);
                if (tileEntity instanceof CentrifugeTileEntity) {
                    ((CentrifugeTileEntity) tileEntity).updateRequiresRedstone();
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
