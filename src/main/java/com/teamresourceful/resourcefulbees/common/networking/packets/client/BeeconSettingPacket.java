package com.teamresourceful.resourcefulbees.common.networking.packets.client;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefulbees.common.blockentities.EnderBeeconBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
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

public record BeeconSettingPacket(
        BeeconPacketOption option,
        int value,
        BlockPos pos
) implements Packet<BeeconSettingPacket> {

    public static final ServerboundPacketType<BeeconSettingPacket> PACKET_TYPE = new Type();

    @Override
    public PacketType<BeeconSettingPacket> type() {
        return PACKET_TYPE;
    }

    private static class Type extends CodecPacketType<BeeconSettingPacket> implements ServerboundPacketType<BeeconSettingPacket> {

        public Type() {
            super(
                    ModIdentifier.of("beecon_setting"),
                    ObjectByteCodec.create(
                            ByteCodec.ofEnum(BeeconPacketOption.class).fieldOf(BeeconSettingPacket::option),
                            ByteCodec.VAR_INT.fieldOf(BeeconSettingPacket::value),
                            ExtraByteCodecs.BLOCK_POS.fieldOf(BeeconSettingPacket::pos),
                            BeeconSettingPacket::new
                    )
            );
        }

        @Override
        public Consumer<Player> handle(BeeconSettingPacket message) {
            return player -> {
                if (WorldUtils.getTileEntity(player.level(), message.pos()) instanceof EnderBeeconBlockEntity beecon) {
                    beecon.handleBeeconUpdate(message.option(), null, message.value());
                }
            };
        }
    }
}
