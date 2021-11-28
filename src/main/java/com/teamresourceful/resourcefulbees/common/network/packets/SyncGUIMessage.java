package com.teamresourceful.resourcefulbees.common.network.packets;

import com.teamresourceful.resourcefulbees.common.tileentity.ISyncableGUI;
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
    private final boolean initPacket;

    public SyncGUIMessage(BlockPos pos, PacketBuffer buffer){
        this.pos = pos;
        this.buffer = buffer;
        this.initPacket = false;
    }

    public SyncGUIMessage(BlockPos pos, PacketBuffer buffer, boolean initPacket){
        this.pos = pos;
        this.buffer = buffer;
        this.initPacket = initPacket;
    }

    public static void encode(SyncGUIMessage message, PacketBuffer buffer){
        buffer.writeBlockPos(message.pos);
        buffer.writeBoolean(message.initPacket);
        buffer.writeBytes(message.buffer);
    }

    public static SyncGUIMessage decode(PacketBuffer buffer){
        return new SyncGUIMessage(buffer.readBlockPos(), buffer, buffer.readBoolean());
    }

    public static void handle(SyncGUIMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if (player != null && player.level.isLoaded(message.pos)) {
                TileEntity tileEntity = player.level.getBlockEntity(message.pos);
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
