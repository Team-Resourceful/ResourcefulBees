package com.teamresourceful.resourcefulbees.common.network.packets;

import com.teamresourceful.resourcefulbees.common.tileentity.EnderBeeconTileEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class BeeconChangeMessage {

    private final Option option;
    private final int value;
    private final BlockPos pos;

    public BeeconChangeMessage(Option option, boolean value, BlockPos pos) {
        this(option, value ? 1 : 0, pos);
    }

    public BeeconChangeMessage(Option option, int value, BlockPos pos) {
        this.option = option;
        this.value = value;
        this.pos = pos;
    }

    public static void encode(BeeconChangeMessage message, PacketBuffer buffer) {
        buffer.writeEnum(message.option);
        buffer.writeInt(message.value);
        buffer.writeBlockPos(message.pos);
    }

    public static BeeconChangeMessage decode(PacketBuffer buffer) {
        return new BeeconChangeMessage(buffer.readEnum(Option.class), buffer.readInt(), buffer.readBlockPos());
    }

    public static void handle(BeeconChangeMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayerEntity player = context.get().getSender();
            if (player != null && player.level.isLoaded(message.pos)){
                TileEntity tileEntity = player.level.getBlockEntity(message.pos);
                if (tileEntity instanceof EnderBeeconTileEntity) {
                    EnderBeeconTileEntity beecon = (EnderBeeconTileEntity) tileEntity;
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
