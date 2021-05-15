package com.teamresourceful.resourcefulbees.network.packets;

import com.teamresourceful.resourcefulbees.tileentity.multiblocks.apiary.ApiaryTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateClientApiaryMessage {
    private final BlockPos pos;
    private final CompoundTag data;

    public UpdateClientApiaryMessage(BlockPos pos, CompoundTag data){
        this.pos = pos;
        this.data = data;
    }

    public static void encode(UpdateClientApiaryMessage message, FriendlyByteBuf buffer){
        buffer.writeBlockPos(message.pos);
        buffer.writeNbt(message.data);
    }

    public static UpdateClientApiaryMessage decode(FriendlyByteBuf buffer){
        return new UpdateClientApiaryMessage(buffer.readBlockPos(), buffer.readNbt());
    }

    public static void handle(UpdateClientApiaryMessage message, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> {
            AbstractClientPlayer player = Minecraft.getInstance().player;
            if (player != null && player.level.isLoaded(message.pos)) {
                BlockEntity tileEntity = player.level.getBlockEntity(message.pos);
                if (tileEntity instanceof ApiaryTileEntity) {
                    ApiaryTileEntity apiaryTileEntity = (ApiaryTileEntity) tileEntity;
                    apiaryTileEntity.bees.clear();
                    apiaryTileEntity.loadFromNBT(message.data);
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}


