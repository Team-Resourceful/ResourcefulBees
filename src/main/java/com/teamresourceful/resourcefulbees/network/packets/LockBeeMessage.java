package com.teamresourceful.resourcefulbees.network.packets;

import com.teamresourceful.resourcefulbees.tileentity.multiblocks.apiary.ApiaryTileEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class LockBeeMessage {

    private final BlockPos pos;
    private final String beeType;

    public LockBeeMessage(BlockPos pos, String beeType){
        this.pos = pos;
        this.beeType = beeType;
    }

    public static void encode(LockBeeMessage message, PacketBuffer buffer){
        buffer.writeBlockPos(message.pos);
        buffer.writeUtf(message.beeType);
    }

    public static LockBeeMessage decode(PacketBuffer buffer){
        return new LockBeeMessage(buffer.readBlockPos(), buffer.readUtf(100));
    }

    public static void handle(LockBeeMessage message, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> {
            ServerPlayerEntity player = context.get().getSender();
            if (player != null && player.level.isLoaded(message.pos)) {
                TileEntity tileEntity = player.level.getBlockEntity(message.pos);
                if (tileEntity instanceof ApiaryTileEntity) {
                    ApiaryTileEntity apiaryTileEntity = (ApiaryTileEntity) tileEntity;
                    apiaryTileEntity.lockOrUnlockBee(message.beeType);
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
