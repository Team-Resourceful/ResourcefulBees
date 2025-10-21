package com.teamresourceful.resourcefulbees.common.networking.packets.server;

import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefulbees.common.blockentities.base.SyncableGUI;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record SyncGuiPacket(BlockPos pos, @NotNull CompoundTag tag) implements Packet<SyncGuiPacket> {

    public static final ClientboundPacketType<SyncGuiPacket> PACKET_TYPE = new Type();

    @Override
    public PacketType<SyncGuiPacket> type() {
        return PACKET_TYPE;
    }

    private static class Type extends CodecPacketType<SyncGuiPacket> implements ClientboundPacketType<SyncGuiPacket> {

        public Type() {
            super(
                    ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "sync_gui"),
                    ObjectByteCodec.create(
                            ExtraByteCodecs.BLOCK_POS.fieldOf(SyncGuiPacket::pos),
                            ExtraByteCodecs.NONNULL_COMPOUND_TAG.fieldOf(SyncGuiPacket::tag),
                            SyncGuiPacket::new
                    )
            );
        }

        @Override
        public Runnable handle(SyncGuiPacket message) {
            return () -> {
                assert Minecraft.getInstance().level != null;
                if (Minecraft.getInstance().level.getBlockEntity(message.pos) instanceof SyncableGUI syncedBlockEntity) {
                    syncedBlockEntity.readSyncData(message.tag);
                }
            };
        }
    }





    /*public static final ResourceLocation ID = new ResourceLocation(ModConstants.MOD_ID, "sync_gui");
    public static final Handler HANDLER = new Handler();

    public SyncGuiPacket(SyncableGUI syncedBlockEntity) {
        this(syncedBlockEntity.getBlkPos(), syncedBlockEntity.getSyncData());
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<SyncGuiPacket> getHandler() {
        return HANDLER;
    }

    @SuppressWarnings("UnstableApiUsage")
    private static class Handler extends CodecPacketHandler<SyncGuiPacket> {

        public Handler() {
            super(ObjectByteCodec.create(
                ExtraByteCodecs.BLOCK_POS.fieldOf(SyncGuiPacket::pos),
                ExtraByteCodecs.NONNULL_COMPOUND_TAG.fieldOf(SyncGuiPacket::tag),
                SyncGuiPacket::new
            ));
        }

        @Override
        public PacketContext handle(SyncGuiPacket message) {
            return (player, level) -> {
                if (level.isLoaded(message.pos) && level.getBlockEntity(message.pos) instanceof SyncableGUI syncedBlockEntity) {
                    syncedBlockEntity.readSyncData(message.tag);
                }
            };
        }
    }*/
}
