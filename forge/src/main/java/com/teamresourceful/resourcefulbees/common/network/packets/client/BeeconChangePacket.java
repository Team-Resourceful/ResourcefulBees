package com.teamresourceful.resourcefulbees.common.network.packets.client;

import com.teamresourceful.resourcefulbees.common.blockentity.EnderBeeconBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.util.WorldUtils;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record BeeconChangePacket(Option option, int value, BlockPos pos) implements Packet<BeeconChangePacket> {

    public static final ResourceLocation ID = new ResourceLocation(ModConstants.MOD_ID, "beecon_change");
    public static final Handler HANDLER = new Handler();

    public BeeconChangePacket(Option option, boolean value, BlockPos pos) {
        this(option, value ? 1 : 0, pos);
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<BeeconChangePacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<BeeconChangePacket> {

        @Override
        public void encode(BeeconChangePacket message, FriendlyByteBuf buffer) {
            buffer.writeEnum(message.option);
            buffer.writeVarInt(message.value);
            buffer.writeBlockPos(message.pos);
        }

        @Override
        public BeeconChangePacket decode(FriendlyByteBuf buffer) {
            return new BeeconChangePacket(buffer.readEnum(Option.class), buffer.readVarInt(), buffer.readBlockPos());
        }

        @Override
        public PacketContext handle(BeeconChangePacket message) {
            return (player, level) -> {
                if (WorldUtils.getTileEntity(level, message.pos()) instanceof EnderBeeconBlockEntity beecon) {
                    beecon.handleBeeconUpdate(message.option(), message.value());
                }
            };
        }
    }

    public enum Option {
        EFFECT_ON,
        EFFECT_OFF,
        BEAM,
        SOUND,
        RANGE,
    }
}
