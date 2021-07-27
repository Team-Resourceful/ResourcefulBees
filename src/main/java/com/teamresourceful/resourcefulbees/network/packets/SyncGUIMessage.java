package com.teamresourceful.resourcefulbees.network.packets;

import com.teamresourceful.resourcefulbees.tileentity.ISyncableGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncGUIMessage {
    private final BlockPos pos;
    private final PacketBuffer buffer;

    public SyncGUIMessage(BlockPos pos, PacketBuffer buffer){
        this.pos = pos;
        this.buffer = buffer;
    }

    public static void encode(SyncGUIMessage message, PacketBuffer buffer){
        buffer.writeBlockPos(message.pos);
        buffer.writeBytes(message.buffer);
    }

    public static SyncGUIMessage decode(PacketBuffer buffer){
        return new SyncGUIMessage(buffer.readBlockPos(), buffer);
    }

    public static void handle(SyncGUIMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if (player != null && player.level.isLoaded(message.pos)) {
                TileEntity tileEntity = player.level.getBlockEntity(message.pos);
                if (tileEntity instanceof ISyncableGUI) {
                    ((ISyncableGUI) tileEntity).handleGUINetworkPacket(message.buffer);
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
