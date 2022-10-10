package com.teamresourceful.resourcefulbees.common.network.packets.server;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.blockentity.base.ISyncableGUI;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record SyncGuiPacket(BlockPos pos, CompoundTag tag) implements Packet<SyncGuiPacket> {

    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "sync_gui");
    public static final Handler HANDLER = new Handler();

    public SyncGuiPacket(ISyncableGUI syncedBlockEntity) {
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

    private static class Handler implements PacketHandler<SyncGuiPacket> {

        @Override
        public void encode(SyncGuiPacket message, FriendlyByteBuf buffer) {
            buffer.writeBlockPos(message.pos);
            buffer.writeNbt(message.tag);
        }

        @Override
        public SyncGuiPacket decode(FriendlyByteBuf buffer) {
            return new SyncGuiPacket(buffer.readBlockPos(), buffer.readNbt());
        }

        @Override
        public PacketContext handle(SyncGuiPacket message) {
            return (player, level) -> {
                if (level.isLoaded(message.pos)) {
                    if (level.getBlockEntity(message.pos) instanceof ISyncableGUI syncedBlockEntity) {
                        syncedBlockEntity.readSyncData(message.tag);
                    }
                }
            };
        }
    }
}
