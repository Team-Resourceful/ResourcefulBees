package com.teamresourceful.resourcefulbees.common.network.packets;

import com.teamresourceful.resourcefulbees.common.blockentity.base.ISyncableGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SyncGUIMessage(BlockPos pos, CompoundTag tag) {

    public SyncGUIMessage(ISyncableGUI syncedBlockEntity) {
        this(syncedBlockEntity.getBlockPos(), syncedBlockEntity.getSyncData());
    }

    public static void encode(SyncGUIMessage message, FriendlyByteBuf buffer){
        buffer.writeBlockPos(message.pos);
        buffer.writeNbt(message.tag);
    }

    public static SyncGUIMessage decode(FriendlyByteBuf buffer){
        return new SyncGUIMessage(buffer.readBlockPos(), buffer.readNbt());
    }

    public static void handle(SyncGUIMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && player.level.isLoaded(message.pos)) {
                if (player.level.getBlockEntity(message.pos) instanceof ISyncableGUI syncedBlockEntity) {
                    syncedBlockEntity.readSyncData(message.tag);
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
