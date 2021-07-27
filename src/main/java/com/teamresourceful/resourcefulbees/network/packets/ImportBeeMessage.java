package com.teamresourceful.resourcefulbees.network.packets;

import com.teamresourceful.resourcefulbees.tileentity.multiblocks.apiary.ApiaryTileEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ImportBeeMessage {

    private final BlockPos pos;

    public ImportBeeMessage(BlockPos pos){
        this.pos = pos;
    }

    public static void encode(ImportBeeMessage message, PacketBuffer buffer){
        buffer.writeBlockPos(message.pos);
    }

    public static ImportBeeMessage decode(PacketBuffer buffer){
        return new ImportBeeMessage(buffer.readBlockPos());
    }

    public static void handle(ImportBeeMessage message, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> {
            ServerPlayerEntity player = context.get().getSender();
            if (player != null && player.level.isLoaded(message.pos)) {
                TileEntity tileEntity = player.level.getBlockEntity(message.pos);
                if (tileEntity instanceof ApiaryTileEntity) {
                    ApiaryTileEntity apiaryTileEntity = (ApiaryTileEntity) tileEntity;
                    apiaryTileEntity.importBee(player);
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
