package com.teamresourceful.resourcefulbees.common.networking.packets.server;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.resources.storage.beepedia.BeepediaData;
import com.teamresourceful.resourcefulbees.common.resources.storage.beepedia.BeepediaSavedData;
import com.teamresourceful.resourcefulbees.common.util.BeepediaUtils;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public record SyncBeepediaPacket(CompoundTag data) implements Packet<SyncBeepediaPacket> {

    public static final ResourceLocation ID = new ResourceLocation(ModConstants.MOD_ID, "sync_beepedia");
    public static final Handler HANDLER = new Handler();

    public static SyncBeepediaPacket of(Player player) {
        return new SyncBeepediaPacket(BeepediaSavedData.getBeepediaData(player).save());
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<SyncBeepediaPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<SyncBeepediaPacket> {

        @Override
        public void encode(SyncBeepediaPacket message, FriendlyByteBuf buffer) {
            buffer.writeNbt(message.data);
        }

        @Override
        public SyncBeepediaPacket decode(FriendlyByteBuf buffer) {
            return new SyncBeepediaPacket(buffer.readNbt());
        }

        @Override
        public PacketContext handle(SyncBeepediaPacket message) {
            return (player, level) -> {
                BeepediaData data = BeepediaSavedData.getBeepediaData(player);
                data.load(message.data);
                if (level.isClientSide) {
                    BeepediaUtils.onClientUpdated(data);
                }
            };
        }
    }
}
