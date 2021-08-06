package com.teamresourceful.resourcefulbees.common.network.packets;

import com.teamresourceful.resourcefulbees.common.tileentity.EnderBeeconTileEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateBeeconRangeMessage {

    private final BlockPos blockPos;
    private final int range;

    public UpdateBeeconRangeMessage(int range, BlockPos blockPos) {
        this.range = range;
        this.blockPos = blockPos;
    }

    public static void encode(UpdateBeeconRangeMessage message, PacketBuffer buffer) {
        buffer.writeInt(message.range);
        buffer.writeBlockPos(message.blockPos);
    }

    public static UpdateBeeconRangeMessage decode(PacketBuffer buffer) {
        return new UpdateBeeconRangeMessage(buffer.readInt(), buffer.readBlockPos());
    }

    public static void handle(UpdateBeeconRangeMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayerEntity player = context.get().getSender();
            if (player != null && player.level.isLoaded(message.blockPos)) {
                TileEntity tileEntity = player.level.getBlockEntity(message.blockPos);
                if (tileEntity instanceof EnderBeeconTileEntity) {
                    EnderBeeconTileEntity beecon = (EnderBeeconTileEntity) tileEntity;
                    beecon.setRange(message.range);
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
