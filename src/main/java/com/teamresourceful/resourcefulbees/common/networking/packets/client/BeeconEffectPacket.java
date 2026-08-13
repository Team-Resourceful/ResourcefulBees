package com.teamresourceful.resourcefulbees.common.networking.packets.client;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefulbees.common.blockentities.EnderBeeconBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.enums.BeeconEffect;
import com.teamresourceful.resourcefulbees.common.lib.enums.BeeconPacketOption;
import com.teamresourceful.resourcefulbees.common.lib.util.WorldUtils;
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public record BeeconEffectPacket(
        BeeconPacketOption option,
        BeeconEffect effect,
        BlockPos pos
) implements Packet<BeeconEffectPacket> {

    public static final ServerboundPacketType<BeeconEffectPacket> PACKET_TYPE = new Type();

    @Override
    public PacketType<BeeconEffectPacket> type() {
        return PACKET_TYPE;
    }

    private static class Type extends CodecPacketType<BeeconEffectPacket> implements ServerboundPacketType<BeeconEffectPacket> {

        public Type() {
            super(
                    ModIdentifier.of("beecon_effect"),
                    ObjectByteCodec.create(
                            ByteCodec.ofEnum(BeeconPacketOption.class).fieldOf(BeeconEffectPacket::option),
                            ByteCodec.ofEnum(BeeconEffect.class).fieldOf(BeeconEffectPacket::effect),
                            ExtraByteCodecs.BLOCK_POS.fieldOf(BeeconEffectPacket::pos),
                            BeeconEffectPacket::new
                    )
            );
        }

        @Override
        public Consumer<Player> handle(BeeconEffectPacket message) {
            return player -> {
                if (WorldUtils.getTileEntity(player.level(), message.pos()) instanceof EnderBeeconBlockEntity beecon) {
                    beecon.handleBeeconUpdate(message.option(), message.effect(), 0);
                }
            };
        }
    }

}
