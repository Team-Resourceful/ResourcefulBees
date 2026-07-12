package com.teamresourceful.resourcefulbees.common.networking.packets.server;

import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefulbees.common.blockentities.base.SyncedGUI;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs;
import com.teamresourceful.resourcefullib.common.bytecodecs.StreamCodecByteCodec;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.resources.Identifier;

public record SyncBlockDataPacket(
        BlockPos pos,
        DataComponentPatch patch
) implements Packet<SyncBlockDataPacket> {

    public static final ClientboundPacketType<SyncBlockDataPacket> PACKET_TYPE = new Type();

    public SyncBlockDataPacket(SyncedGUI syncedBlockEntity) {
        this(syncedBlockEntity.getBlockPos(), syncedBlockEntity.getSyncData());
    }

    @Override
    public PacketType<SyncBlockDataPacket> type() {
        return PACKET_TYPE;
    }

    private static class Type extends CodecPacketType<SyncBlockDataPacket> implements ClientboundPacketType<SyncBlockDataPacket> {

        public Type() {
            super(
                    Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "sync_block_data"),
                    ObjectByteCodec.create(
                            ExtraByteCodecs.BLOCK_POS.fieldOf(SyncBlockDataPacket::pos),
                            StreamCodecByteCodec.ofRegistry(DataComponentPatch.STREAM_CODEC).fieldOf(SyncBlockDataPacket::patch),
                            SyncBlockDataPacket::new
                    )
            );
        }

        @Override
        public Runnable handle(SyncBlockDataPacket message) {
            return () -> {
                assert Minecraft.getInstance().level != null;
                if (Minecraft.getInstance().level.getBlockEntity(message.pos) instanceof SyncedGUI block) {
                    block.setSyncData(message.patch);
                }
            };
        }
    }
}
