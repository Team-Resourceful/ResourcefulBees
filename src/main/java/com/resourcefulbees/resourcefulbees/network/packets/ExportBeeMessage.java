package com.resourcefulbees.resourcefulbees.network.packets;

import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary.ApiaryTileEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ExportBeeMessage {

    private final BlockPos pos;
    private final String beeType;

    public ExportBeeMessage(BlockPos pos, String beeType){
        this.pos = pos;
        this.beeType = beeType;
    }

    public static void encode(ExportBeeMessage message, PacketBuffer buffer){
        buffer.writeBlockPos(message.pos);
        buffer.writeString(message.beeType);
    }

    public static ExportBeeMessage decode(PacketBuffer buffer){
        return new ExportBeeMessage(buffer.readBlockPos(), buffer.readString(100));
    }

    public static void handle(ExportBeeMessage message, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> {
            ServerPlayerEntity player = context.get().getSender();
            if (player != null) {
                if (player.world.isBlockPresent(message.pos)) {
                    TileEntity tileEntity = player.world.getTileEntity(message.pos);
                    if (tileEntity instanceof ApiaryTileEntity) {
                        ApiaryTileEntity apiaryTileEntity = (ApiaryTileEntity) tileEntity;
                        apiaryTileEntity.exportBee(player, message.beeType);
                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
