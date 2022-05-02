package com.teamresourceful.resourcefulbees.common.network.packets;

import com.teamresourceful.resourcefulbees.common.blockentity.ApiaryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record LockBeeMessage(BlockPos pos, int bee) {

    public static void encode(LockBeeMessage message, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.pos);
        buffer.writeInt(message.bee);
    }

    public static LockBeeMessage decode(FriendlyByteBuf buffer) {
        return new LockBeeMessage(buffer.readBlockPos(), buffer.readInt());
    }

    public static void handle(LockBeeMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null && player.level.isLoaded(message.pos)) {
                if (player.level.getBlockEntity(message.pos) instanceof ApiaryBlockEntity apiaryBlockEntity) {
                    apiaryBlockEntity.lockOrUnlockBee(message.bee);
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
