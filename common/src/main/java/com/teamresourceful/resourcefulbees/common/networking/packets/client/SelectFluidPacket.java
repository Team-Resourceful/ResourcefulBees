package com.teamresourceful.resourcefulbees.common.networking.packets.client;

import com.teamresourceful.resourcefulbees.common.blockentities.base.SelectableFluidContainerHandler;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.util.FluidUtils;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record SelectFluidPacket(BlockPos pos, FluidHolder holder) implements Packet<SelectFluidPacket> {

    public static final ResourceLocation ID = new ResourceLocation(ModConstants.MOD_ID, "select_fluid");
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
            FluidUtils.writeToBuffer(message.holder, buffer);
        }

        @Override
        public SelectFluidPacket decode(FriendlyByteBuf buffer) {
            return new SelectFluidPacket(buffer.readBlockPos(), FluidUtils.readFromBuffer(buffer));
        }

        @Override
        public PacketContext handle(SelectFluidPacket message) {
            return (player, level) -> {
                if (level.isLoaded(message.pos)){
                    if (level.getBlockEntity(message.pos) instanceof SelectableFluidContainerHandler tankBlock) {
                        tankBlock.setFluid(message.holder());
                    }
                }
            };
        }
    }
}
