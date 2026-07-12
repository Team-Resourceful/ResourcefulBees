package com.teamresourceful.resourcefulbees.common.networking.packets.server;

import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefulbees.common.blockentities.base.SyncedGUI;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record SyncBeesPacket(BlockPos pos, @NotNull ListTag tag) implements Packet<SyncBeesPacket> {

    public static final ClientboundPacketType<SyncBeesPacket> PACKET_TYPE = new Type();

    public SyncBeesPacket(SyncedGUI syncedBlockEntity) {
        this(syncedBlockEntity.getBlockPos(), syncedBlockEntity.getSyncData(new ListTag(0)));
    }

    @Override
    public PacketType<SyncBeesPacket> type() {
        return PACKET_TYPE;
    }

    private static class Type extends CodecPacketType<SyncBeesPacket> implements ClientboundPacketType<SyncBeesPacket> {

        public Type() {
            super(
                    Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "sync_bees"),
                    ObjectByteCodec.create(
                            ExtraByteCodecs.BLOCK_POS.fieldOf(SyncBeesPacket::pos),
                            ExtraByteCodecs.NONNULL_COMPOUND_TAG.fieldOf(SyncBeesPacket::tag),
                            SyncBeesPacket::new
                    )
            );
        }

        @Override
        public Runnable handle(SyncBeesPacket message) {
            return () -> {
                assert Minecraft.getInstance().level != null;
                if (Minecraft.getInstance().level.getBlockEntity(message.pos) instanceof SyncedGUI syncedBlockEntity) {
                    syncedBlockEntity.readSyncData(message.tag);
                }
            };
        }
    }


// TagValueInput.create(reporter, registries, tag)
// TagValueOutput.createWithContext(reporter, registries);

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
