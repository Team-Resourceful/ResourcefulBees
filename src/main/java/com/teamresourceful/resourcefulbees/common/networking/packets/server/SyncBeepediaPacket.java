package com.teamresourceful.resourcefulbees.common.networking.packets.server;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.resources.storage.beepedia.BeepediaData;
import com.teamresourceful.resourcefulbees.common.resources.storage.beepedia.BeepediaSavedData;
import com.teamresourceful.resourcefulbees.common.util.BeepediaUtils;
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record SyncBeepediaPacket(@NotNull CompoundTag data) implements Packet<SyncBeepediaPacket> {

    public static final ClientboundPacketType<SyncBeepediaPacket> PACKET_TYPE = new Type();

    @Override
    public PacketType<SyncBeepediaPacket> type() {
        return PACKET_TYPE;
    }

    private static class Type extends CodecPacketType<SyncBeepediaPacket> implements ClientboundPacketType<SyncBeepediaPacket> {


        public Type() {
            super(
                    Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "sync_beepedia"),
                    ExtraByteCodecs.NONNULL_COMPOUND_TAG.map(SyncBeepediaPacket::new, SyncBeepediaPacket::data)
            );
        }

        @Override
        public Runnable handle(SyncBeepediaPacket message) {
            return () -> {
                assert Minecraft.getInstance().player != null;
                BeepediaData data = BeepediaSavedData.getBeepediaData(Minecraft.getInstance().player);
                data.load(message.data);
                BeepediaUtils.onClientUpdated(data);
            };
        }
    }

    /*public static final ResourceLocation ID = new ResourceLocation(ModConstants.MOD_ID, "sync_beepedia");
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

    @SuppressWarnings("UnstableApiUsage")
    private static class Handler extends CodecPacketHandler<SyncBeepediaPacket> {

        public Handler() {
            super(ExtraByteCodecs.NONNULL_COMPOUND_TAG.map(SyncBeepediaPacket::new, SyncBeepediaPacket::data));
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
    }*/
}
