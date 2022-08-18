package com.teamresourceful.resourcefulbees.common.network.packets.server;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.capabilities.base.CapabilityInformation;
import com.teamresourceful.resourcefulbees.common.capabilities.base.SyncableCapability;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

public record SyncCapabilityPacket(ResourceLocation id, CompoundTag data) implements Packet<SyncCapabilityPacket> {

    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "sync_capability");
    public static final Handler HANDLER = new Handler();

    public static SyncCapabilityPacket of(Player player, Capability<? extends INBTSerializable<CompoundTag>> capability) {
        ResourceLocation id = CapabilityInformation.get(capability);
        if (id == null) throw new IllegalArgumentException("Capability " + capability + " is not registered as a syncable capability");
        var data = player.getCapability(capability).resolve().orElse(null);
        if (data == null) return null;
        return new SyncCapabilityPacket(id, data.serializeNBT());
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<SyncCapabilityPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<SyncCapabilityPacket> {

        @Override
        public void encode(SyncCapabilityPacket message, FriendlyByteBuf buffer) {
            buffer.writeResourceLocation(message.id);
            buffer.writeNbt(message.data);
        }

        @Override
        public SyncCapabilityPacket decode(FriendlyByteBuf buffer) {
            return new SyncCapabilityPacket(buffer.readResourceLocation(), buffer.readNbt());
        }

        @Override
        public PacketContext handle(SyncCapabilityPacket message) {
            return (player, level) -> {
                var capability = CapabilityInformation.get(message.id);
                if (capability == null) {
                    ResourcefulBees.LOGGER.error("Received capability sync packet for capability " + message.id + " that is not registered as a syncable capability.");
                } else {
                    player.getCapability(capability).ifPresent(data -> {
                        data.deserializeNBT(message.data);
                        if (data instanceof SyncableCapability syncableCapability) {
                            syncableCapability.onSynced(player);
                        }
                    });
                }
            };
        }
    }
}
