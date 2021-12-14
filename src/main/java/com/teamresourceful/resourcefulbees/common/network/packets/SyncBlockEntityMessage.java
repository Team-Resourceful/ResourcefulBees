package com.teamresourceful.resourcefulbees.common.network.packets;

import com.teamresourceful.resourcefulbees.common.tileentity.SyncedBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SyncBlockEntityMessage(BlockPos pos, CompoundTag tag) {

    public SyncBlockEntityMessage(SyncedBlockEntity syncedBlockEntity) {
        this(syncedBlockEntity.getBlockPos(), syncedBlockEntity.getData());
    }

    public static void encode(SyncBlockEntityMessage message, FriendlyByteBuf buffer){
        buffer.writeBlockPos(message.pos);
        buffer.writeNbt(message.tag);
    }

    public static SyncBlockEntityMessage decode(FriendlyByteBuf buffer){
        return new SyncBlockEntityMessage(buffer.readBlockPos(), buffer.readNbt());
    }

    public static void handle(SyncBlockEntityMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && player.level.isLoaded(message.pos)) {
                BlockEntity tileEntity = player.level.getBlockEntity(message.pos);
                if (tileEntity instanceof SyncedBlockEntity syncedBlockEntity) {
                    syncedBlockEntity.updateData(message.tag);
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
