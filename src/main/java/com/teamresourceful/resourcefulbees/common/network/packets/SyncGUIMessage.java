package com.teamresourceful.resourcefulbees.common.network.packets;

import com.teamresourceful.resourcefulbees.common.tileentity.ISyncableGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SyncGUIMessage(BlockPos pos, FriendlyByteBuf buffer, boolean initPacket) {

    public SyncGUIMessage(BlockPos pos, FriendlyByteBuf buffer){
        this(pos, buffer, false);
    }

    public static void encode(SyncGUIMessage message, FriendlyByteBuf buffer){
        buffer.writeBlockPos(message.pos);
        buffer.writeBoolean(message.initPacket);
        buffer.writeBytes(message.buffer);
    }

    public static SyncGUIMessage decode(FriendlyByteBuf buffer){
        return new SyncGUIMessage(buffer.readBlockPos(), buffer, buffer.readBoolean());
    }

    public static void handle(SyncGUIMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && player.level.isLoaded(message.pos)) {
                BlockEntity tileEntity = player.level.getBlockEntity(message.pos);
                if (tileEntity instanceof ISyncableGUI) {
                    if (message.initPacket) {
                        ((ISyncableGUI) tileEntity).handleInitGUIPacket(message.buffer);
                    } else {
                        ((ISyncableGUI) tileEntity).handleGUINetworkPacket(message.buffer);
                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
