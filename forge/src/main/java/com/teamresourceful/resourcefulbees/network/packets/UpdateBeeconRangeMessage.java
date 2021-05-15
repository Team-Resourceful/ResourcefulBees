package com.teamresourceful.resourcefulbees.network.packets;

import com.teamresourceful.resourcefulbees.tileentity.EnderBeeconTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateBeeconRangeMessage {

    private final BlockPos blockPos;
    private final int range;

    public UpdateBeeconRangeMessage(int range, BlockPos blockPos) {
        this.range = range;
        this.blockPos = blockPos;
    }

    public static void encode(UpdateBeeconRangeMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.range);
        buffer.writeBlockPos(message.blockPos);
    }

    public static UpdateBeeconRangeMessage decode(FriendlyByteBuf buffer) {
        return new UpdateBeeconRangeMessage(buffer.readInt(), buffer.readBlockPos());
    }

    public static void handle(UpdateBeeconRangeMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null && player.level.isLoaded(message.blockPos)) {
                BlockEntity tileEntity = player.level.getBlockEntity(message.blockPos);
                if (tileEntity instanceof EnderBeeconTileEntity) {
                    EnderBeeconTileEntity beecon = (EnderBeeconTileEntity) tileEntity;
                    beecon.setRange(message.range);
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
