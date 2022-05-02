package com.teamresourceful.resourcefulbees.common.network.packets;

import com.teamresourceful.resourcefulbees.common.blockentity.EnderBeeconBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record BeeconChangeMessage(Option option, int value, BlockPos pos) {

    public BeeconChangeMessage(Option option, boolean value, BlockPos pos) {
        this(option, value ? 1 : 0, pos);
    }

    public static void encode(BeeconChangeMessage message, FriendlyByteBuf buffer) {
        buffer.writeEnum(message.option);
        buffer.writeInt(message.value);
        buffer.writeBlockPos(message.pos);
    }

    public static BeeconChangeMessage decode(FriendlyByteBuf buffer) {
        return new BeeconChangeMessage(buffer.readEnum(Option.class), buffer.readInt(), buffer.readBlockPos());
    }

    public static void handle(BeeconChangeMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null && player.level.isLoaded(message.pos)){
                BlockEntity tileEntity = player.level.getBlockEntity(message.pos);
                if (tileEntity instanceof EnderBeeconBlockEntity beecon) {
                    beecon.handleBeeconUpdate(message.option, message.value);
                }
            }
        });
        context.get().setPacketHandled(true);
    }

    public enum Option {
        EFFECT_ON,
        EFFECT_OFF,
        BEAM,
        SOUND,
        RANGE
    }
}
