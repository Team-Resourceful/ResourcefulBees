package com.teamresourceful.resourcefulbees.common.networking.packets.server;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.defaults.MapCodec;
import com.teamresourceful.resourcefulbees.common.items.locator.DimensionalBeeHolder;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs;
import com.teamresourceful.resourcefullib.common.networking.base.CodecPacketHandler;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;

public record DimensionalBeesPacket(Map<ResourceKey<Level>, List<String>> bees) implements Packet<DimensionalBeesPacket> {

    public static final ResourceLocation ID = new ResourceLocation(ModConstants.MOD_ID, "dimensional_bees");
    public static final Handler HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<DimensionalBeesPacket> getHandler() {
        return HANDLER;
    }

    @SuppressWarnings("UnstableApiUsage")
    private static class Handler extends CodecPacketHandler<DimensionalBeesPacket> {

        public Handler() {
            super(
                new MapCodec<>(ExtraByteCodecs.DIMENSION, ByteCodec.STRING.listOf()).map(DimensionalBeesPacket::new, DimensionalBeesPacket::bees)
            );
        }

        @Override
        public PacketContext handle(DimensionalBeesPacket message) {
            return (player, level) -> {
                DimensionalBeeHolder.DIMENSIONAL_BEES.clear();
                DimensionalBeeHolder.DIMENSIONAL_BEES.putAll(message.bees);
            };
        }
    }
}
