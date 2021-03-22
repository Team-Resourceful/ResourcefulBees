package com.resourcefulbees.resourcefulbees.network.packets;

import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary.ApiaryTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ImportBeeMessage {

    private final BlockPos pos;

    public ImportBeeMessage(BlockPos pos){
        this.pos = pos;
    }

    public static void encode(ImportBeeMessage message, FriendlyByteBuf buffer){
        buffer.writeBlockPos(message.pos);
    }

    public static ImportBeeMessage decode(FriendlyByteBuf buffer){
        return new ImportBeeMessage(buffer.readBlockPos());
    }

    public static void handle(ImportBeeMessage message, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null && player.level.isLoaded(message.pos)) {
                BlockEntity tileEntity = player.level.getBlockEntity(message.pos);
                if (tileEntity instanceof ApiaryTileEntity) {
                    ApiaryTileEntity apiaryTileEntity = (ApiaryTileEntity) tileEntity;
                    apiaryTileEntity.importBee(player);
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
