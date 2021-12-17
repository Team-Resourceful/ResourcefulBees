package com.resourcefulbees.resourcefulbees.network.packets;

import com.resourcefulbees.resourcefulbees.tileentity.ISyncableGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class NewSyncGUIMessage {
    private final BlockPos pos;
    private final PacketBuffer buffer;
    private final boolean initPacket;

    public NewSyncGUIMessage(BlockPos pos, PacketBuffer buffer){
        this.pos = pos;
        this.buffer = buffer;
        this.initPacket = false;
    }

    public NewSyncGUIMessage(BlockPos pos, PacketBuffer buffer, boolean initPacket){
        this.pos = pos;
        this.buffer = buffer;
        this.initPacket = initPacket;
    }

    public static void encode(NewSyncGUIMessage message, PacketBuffer buffer){
        buffer.writeBlockPos(message.pos);
        buffer.writeBoolean(message.initPacket);
        buffer.writeBytes(message.buffer);
    }

    public static NewSyncGUIMessage decode(PacketBuffer buffer){
        return new NewSyncGUIMessage(buffer.readBlockPos(), buffer, buffer.readBoolean());
    }

    public static void handle(NewSyncGUIMessage message, Supplier<NetworkEvent.Context> context) {
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