package com.teamresourceful.resourcefulbees.common.network.packets;

import com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary.ApiaryTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ExportBeeMessage(BlockPos pos, String beeType) {

    public static void encode(ExportBeeMessage message, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.pos);
        buffer.writeUtf(message.beeType);
    }

    public static ExportBeeMessage decode(FriendlyByteBuf buffer) {
        return new ExportBeeMessage(buffer.readBlockPos(), buffer.readUtf(100));
    }

    public static void handle(ExportBeeMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null && player.level.isLoaded(message.pos)) {
                BlockEntity tileEntity = player.level.getBlockEntity(message.pos);
                if (tileEntity instanceof ApiaryTileEntity apiaryTileEntity) {
                    apiaryTileEntity.exportBee(player, message.beeType);
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
