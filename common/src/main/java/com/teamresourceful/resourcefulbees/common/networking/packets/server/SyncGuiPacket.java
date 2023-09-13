package com.teamresourceful.resourcefulbees.common.networking.packets.server;

import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefulbees.common.blockentities.base.SyncableGUI;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs;
import com.teamresourceful.resourcefullib.common.networking.base.CodecPacketHandler;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record SyncGuiPacket(BlockPos pos, @NotNull CompoundTag tag) implements Packet<SyncGuiPacket> {

    public static final ResourceLocation ID = new ResourceLocation(ModConstants.MOD_ID, "sync_gui");
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
    }
}
