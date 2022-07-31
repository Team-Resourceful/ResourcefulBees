package com.teamresourceful.resourcefulbees.common.network.packets;

import com.teamresourceful.resourcefulbees.common.blockentity.base.ISelectableTankBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SelectFluidMessage(BlockPos pos, int tank, FluidStack stack) {

    public static void encode(SelectFluidMessage message, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.pos);
        buffer.writeVarInt(message.tank);
        buffer.writeFluidStack(message.stack);
    }

    public static SelectFluidMessage decode(FriendlyByteBuf buffer) {
        return new SelectFluidMessage(buffer.readBlockPos(), buffer.readVarInt(), buffer.readFluidStack());
    }

    public static void handle(SelectFluidMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null && player.level.isLoaded(message.pos)){
                BlockEntity tileEntity = player.level.getBlockEntity(message.pos);
                if (tileEntity instanceof ISelectableTankBlock tankBlock) {
                    tankBlock.setFluid(message.tank(), message.stack());
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
