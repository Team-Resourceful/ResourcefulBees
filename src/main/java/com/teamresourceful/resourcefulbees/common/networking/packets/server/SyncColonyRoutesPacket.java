package com.teamresourceful.resourcefulbees.common.networking.packets.server;

import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.menus.ColonyControllerMenu;
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs;
import com.teamresourceful.resourcefullib.common.bytecodecs.StreamCodecByteCodec;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;

public record SyncColonyRoutesPacket(
        BlockPos pos,
        int containerId,
        CompoundTag data
) implements Packet<SyncColonyRoutesPacket> {

    public static final ClientboundPacketType<SyncColonyRoutesPacket> PACKET_TYPE = new Type();

    @Override
    public PacketType<SyncColonyRoutesPacket> type() {
        return PACKET_TYPE;
    }

    private static class Type extends CodecPacketType<SyncColonyRoutesPacket> implements ClientboundPacketType<SyncColonyRoutesPacket> {

        public Type() {
            super(
                    ModIdentifier.of("sync_colony_routes"),
                    ObjectByteCodec.create(
                            ExtraByteCodecs.BLOCK_POS.fieldOf(SyncColonyRoutesPacket::pos),
                            StreamCodecByteCodec.of(ByteBufCodecs.VAR_INT).fieldOf(SyncColonyRoutesPacket::containerId),
                            ExtraByteCodecs.NONNULL_COMPOUND_TAG.fieldOf(SyncColonyRoutesPacket::data),
                            SyncColonyRoutesPacket::new
                    )
            );
        }

        @Override
        public Runnable handle(SyncColonyRoutesPacket message) {
            return () -> {
                var player = Minecraft.getInstance().player;

                if (player == null
                        || !(player.containerMenu instanceof ColonyControllerMenu menu)
                        || menu.containerId != message.containerId()
                        || menu.getEntity() == null
                        || !menu.getEntity().getBlockPos().equals(message.pos())) {
                    return;
                }

                menu.receiveRouteData(message.data());
            };
        }
    }
}