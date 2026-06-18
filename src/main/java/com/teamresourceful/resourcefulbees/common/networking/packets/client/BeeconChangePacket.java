package com.teamresourceful.resourcefulbees.common.networking.packets.client;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefulbees.common.blockentities.EnderBeeconBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.util.WorldUtils;
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public record BeeconChangePacket(
        Option option, int value, BlockPos pos
) implements Packet<BeeconChangePacket> {

    public static final ServerboundPacketType<BeeconChangePacket> PACKET_TYPE = new Type();

    @Override
    public PacketType<BeeconChangePacket> type() {
        return PACKET_TYPE;
    }

    private static class Type extends CodecPacketType<BeeconChangePacket> implements ServerboundPacketType<BeeconChangePacket> {

        public Type() {
            super(
                    Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "beecon_change"),
                    ObjectByteCodec.create(
                            ByteCodec.ofEnum(Option.class).fieldOf(BeeconChangePacket::option),
                            ByteCodec.VAR_INT.fieldOf(BeeconChangePacket::value),
                            ExtraByteCodecs.BLOCK_POS.fieldOf(BeeconChangePacket::pos),
                            BeeconChangePacket::new
                    )
            );
        }

        @Override
        public Consumer<Player> handle(BeeconChangePacket message) {
            return player -> {
                if (WorldUtils.getTileEntity(player.level(), message.pos()) instanceof EnderBeeconBlockEntity beecon) {
                    beecon.handleBeeconUpdate(message.option(), message.value());
                }
            };
        }
    }
/*

    public static final ResourceLocation ID = new ResourceLocation(ModConstants.MOD_ID, "beecon_change");
    public static final Handler HANDLER = new Handler();

    public BeeconChangePacket(Option option, boolean value, BlockPos pos) {
        this(option, value ? 1 : 0, pos);
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<BeeconChangePacket> getHandler() {
        return HANDLER;
    }

    @SuppressWarnings("UnstableApiUsage")
    private static class Handler extends CodecPacketHandler<BeeconChangePacket> {

        public Handler() {
            super(ObjectByteCodec.create(
                ByteCodec.ofEnum(Option.class).fieldOf(BeeconChangePacket::option),
                ByteCodec.VAR_INT.fieldOf(BeeconChangePacket::value),
                ExtraByteCodecs.BLOCK_POS.fieldOf(BeeconChangePacket::pos),
                BeeconChangePacket::new
            ));
        }

        @Override
        public PacketContext handle(BeeconChangePacket message) {
            return (player, level) -> {
                if (WorldUtils.getTileEntity(level, message.pos()) instanceof EnderBeeconBlockEntity beecon) {
                    beecon.handleBeeconUpdate(message.option(), message.value());
                }
            };
        }
    }*/

    public enum Option {
        EFFECT_ON,
        EFFECT_OFF,
        BEAM,
        SOUND,
        RANGE,
    }
}
