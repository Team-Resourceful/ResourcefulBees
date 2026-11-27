
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

public record SyncColonyCouriersPacket(
        BlockPos pos,
        int containerId,
        CompoundTag data
) implements Packet<SyncColonyCouriersPacket> {

    public static final ClientboundPacketType<SyncColonyCouriersPacket> PACKET_TYPE =
            new Type();

    @Override
    public PacketType<SyncColonyCouriersPacket> type() {
        return PACKET_TYPE;
    }

    private static class Type extends CodecPacketType<SyncColonyCouriersPacket>
            implements ClientboundPacketType<SyncColonyCouriersPacket> {

        public Type() {
            super(
                    ModIdentifier.of("sync_colony_couriers"),
                    ObjectByteCodec.create(
                            ExtraByteCodecs.BLOCK_POS.fieldOf(SyncColonyCouriersPacket::pos),
                            StreamCodecByteCodec.of(ByteBufCodecs.VAR_INT)
                                    .fieldOf(SyncColonyCouriersPacket::containerId),
                            ExtraByteCodecs.NONNULL_COMPOUND_TAG
                                    .fieldOf(SyncColonyCouriersPacket::data),
                            SyncColonyCouriersPacket::new
                    )
            );
        }

        @Override
        public Runnable handle(SyncColonyCouriersPacket message) {
            return () -> {
                var player = Minecraft.getInstance().player;

                if (player == null
                        || !(player.containerMenu instanceof ColonyControllerMenu menu)
                        || menu.containerId != message.containerId()
                        || menu.getEntity() == null
                        || !menu.getEntity().getBlockPos().equals(message.pos())) {
                    return;
                }

                menu.receiveCourierData(message.data());
            };
        }
    }
}
