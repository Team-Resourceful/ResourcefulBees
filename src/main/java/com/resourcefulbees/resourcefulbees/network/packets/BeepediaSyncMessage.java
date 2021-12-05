package com.resourcefulbees.resourcefulbees.network.packets;

import com.resourcefulbees.resourcefulbees.capabilities.BeepediaData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class BeepediaSyncMessage {
    CompoundNBT data;
    private final PacketBuffer buffer;

    public BeepediaSyncMessage(CompoundNBT data, PacketBuffer buffer) {
        this.data = data;
        this.buffer = buffer;
    }

    public static void encode(BeepediaSyncMessage message, PacketBuffer buffer) {
        buffer.writeNbt(message.data);
        buffer.writeBytes(message.buffer);
    }

    public static BeepediaSyncMessage decode(PacketBuffer buffer) {
        return new BeepediaSyncMessage(buffer.readNbt(), buffer);
    }

    public static void handle(BeepediaSyncMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if (player == null) return;
            player.getCapability(BeepediaData.Provider.BEEPEDIA_DATA).ifPresent(d -> d.deserializeNBT(message.data));
        });
        context.get().setPacketHandled(true);
    }
}