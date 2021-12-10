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

public record BuildApiaryMessage(BlockPos pos, int verticalOffset, int horizontalOffset) {

    public static void encode(BuildApiaryMessage message, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.pos);
        buffer.writeInt(message.verticalOffset);
        buffer.writeInt(message.horizontalOffset);
    }

    public static BuildApiaryMessage decode(FriendlyByteBuf buffer) {
        return new BuildApiaryMessage(buffer.readBlockPos(), buffer.readInt(), buffer.readInt());
    }

    public static void handle(BuildApiaryMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null && player.level.isLoaded(message.pos)) {
                BlockEntity tileEntity = player.level.getBlockEntity(message.pos);
                if (tileEntity instanceof ApiaryTileEntity apiaryTileEntity) {
                    apiaryTileEntity.setVerticalOffset(message.verticalOffset);
                    apiaryTileEntity.setHorizontalOffset(message.horizontalOffset);
                    apiaryTileEntity.runCreativeBuild(player);
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
