package com.teamresourceful.resourcefulbees.common.network.packets.client;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.blockentity.base.SelectableFluidContainerHandler;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public record SelectFluidPacket(BlockPos pos, int tank, FluidStack stack) implements Packet<SelectFluidPacket> {

    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "select_fluid");
    public static final Handler HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<SelectFluidPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<SelectFluidPacket> {

        @Override
        public void encode(SelectFluidPacket message, FriendlyByteBuf buffer) {
            buffer.writeBlockPos(message.pos);
            buffer.writeVarInt(message.tank);
            buffer.writeFluidStack(message.stack);
        }

        @Override
        public SelectFluidPacket decode(FriendlyByteBuf buffer) {
            return new SelectFluidPacket(buffer.readBlockPos(), buffer.readVarInt(), buffer.readFluidStack());
        }

        @Override
        public PacketContext handle(SelectFluidPacket message) {
            return (player, level) -> {
                if (level.isLoaded(message.pos)){
                    if (level.getBlockEntity(message.pos) instanceof SelectableFluidContainerHandler tankBlock) {
                        tankBlock.setFluid(message.tank(), message.stack());
                    }
                }
            };
        }
    }
}
