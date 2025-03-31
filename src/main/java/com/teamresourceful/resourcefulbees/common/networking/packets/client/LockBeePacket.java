package com.teamresourceful.resourcefulbees.common.networking.packets.client;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefulbees.common.blockentities.ApiaryBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs;
import com.teamresourceful.resourcefullib.common.networking.base.CodecPacketHandler;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public record LockBeePacket(BlockPos pos, int bee) implements Packet<LockBeePacket> {

    public static final ResourceLocation ID = new ResourceLocation(ModConstants.MOD_ID, "lock_bee");
    public static final Handler HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<LockBeePacket> getHandler() {
        return HANDLER;
    }

    @SuppressWarnings("UnstableApiUsage")
    private static class Handler extends CodecPacketHandler<LockBeePacket> {

        public Handler() {
            super(ObjectByteCodec.create(
                ExtraByteCodecs.BLOCK_POS.fieldOf(LockBeePacket::pos),
                ByteCodec.VAR_INT.fieldOf(LockBeePacket::bee),
                LockBeePacket::new
            ));
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
