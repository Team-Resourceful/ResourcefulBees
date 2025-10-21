package com.teamresourceful.resourcefulbees.common.networking.packets.client;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefulbees.common.blockentities.ApiaryBlockEntity;
import com.teamresourceful.resourcefulbees.common.blockentities.EnderBeeconBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.util.WorldUtils;
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public record LockBeePacket(BlockPos pos, int bee) implements Packet<LockBeePacket> {

    public static final ServerboundPacketType<LockBeePacket> PACKET_TYPE = new Type();

    @Override
    public PacketType<LockBeePacket> type() {
        return PACKET_TYPE;
    }

    private static class Type extends CodecPacketType<LockBeePacket> implements ServerboundPacketType<LockBeePacket> {

        public Type() {
            super(
                    ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "lock_bee"),
                    ObjectByteCodec.create(
                            ExtraByteCodecs.BLOCK_POS.fieldOf(LockBeePacket::pos),
                            ByteCodec.VAR_INT.fieldOf(LockBeePacket::bee),
                            LockBeePacket::new
                    )
            );
        }

        @Override
        public Consumer<Player> handle(LockBeePacket message) {
            return player -> {
                if (player.level().getBlockEntity(message.pos) instanceof  ApiaryBlockEntity apiaryBlockEntity ) {
                    apiaryBlockEntity.lockOrUnlockBee(message.bee);
                }
            };
        }
    }
    /*



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
    }*/
}
