package com.teamresourceful.resourcefulbees.common.network.packets.client;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.blockentity.ApiaryBlockEntity;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record LockBeePacket(BlockPos pos, int bee) implements Packet<LockBeePacket> {

    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "lock_bee");
    public static final Handler HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<LockBeePacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<LockBeePacket> {

        @Override
        public void encode(LockBeePacket message, FriendlyByteBuf buffer) {
            buffer.writeBlockPos(message.pos);
            buffer.writeVarInt(message.bee);
        }

        @Override
        public LockBeePacket decode(FriendlyByteBuf buffer) {
            return new LockBeePacket(buffer.readBlockPos(), buffer.readVarInt());
        }

        @Override
        public PacketContext handle(LockBeePacket message) {
            return (player, level) -> {
                if (level.isLoaded(message.pos)) {
                    if (level.getBlockEntity(message.pos) instanceof ApiaryBlockEntity apiaryBlockEntity) {
                        apiaryBlockEntity.lockOrUnlockBee(message.bee);
                    }
                }
            };
        }
    }
}
