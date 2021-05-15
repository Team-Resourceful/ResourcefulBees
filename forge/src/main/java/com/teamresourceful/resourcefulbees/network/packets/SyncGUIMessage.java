package com.teamresourceful.resourcefulbees.network.packets;

import com.teamresourceful.resourcefulbees.tileentity.CentrifugeTileEntity;
import com.teamresourceful.resourcefulbees.tileentity.HoneyCongealerTileEntity;
import com.teamresourceful.resourcefulbees.tileentity.HoneyGeneratorTileEntity;
import com.teamresourceful.resourcefulbees.tileentity.HoneyTankTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncGUIMessage {
    private final BlockPos pos;
    private final FriendlyByteBuf buffer;

    public SyncGUIMessage(BlockPos pos, FriendlyByteBuf buffer){
        this.pos = pos;
        this.buffer = buffer;
    }

    public static void encode(SyncGUIMessage message, FriendlyByteBuf buffer){
        buffer.writeBlockPos(message.pos);
        buffer.writeBytes(message.buffer);
    }

    public static SyncGUIMessage decode(FriendlyByteBuf buffer){
        return new SyncGUIMessage(buffer.readBlockPos(), buffer);
    }

    public static void handle(SyncGUIMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            AbstractClientPlayer player = Minecraft.getInstance().player;
            if (player != null && player.level.isLoaded(message.pos)) {
                BlockEntity tileEntity = player.level.getBlockEntity(message.pos);
                if (tileEntity instanceof CentrifugeTileEntity) {
                    ((CentrifugeTileEntity) tileEntity).handleGUINetworkPacket(message.buffer);
                }
                if (tileEntity instanceof HoneyGeneratorTileEntity) {
                    ((HoneyGeneratorTileEntity) tileEntity).handleGUINetworkPacket(message.buffer);
                }
                if (tileEntity instanceof HoneyCongealerTileEntity) {
                    ((HoneyCongealerTileEntity) tileEntity).handleGUINetworkPacket(message.buffer);
                }
                if (tileEntity instanceof HoneyTankTileEntity) {
                    ((HoneyTankTileEntity) tileEntity).handleGUINetworkPacket(message.buffer);
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
