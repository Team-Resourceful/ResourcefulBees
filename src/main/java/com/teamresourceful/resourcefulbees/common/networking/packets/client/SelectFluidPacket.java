package com.teamresourceful.resourcefulbees.common.networking.packets.client;

import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefulbees.common.blockentities.base.SelectableFluidContainerHandler;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.util.bytecodecs.ByteCodecExtras;
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs;
import com.teamresourceful.resourcefullib.common.networking.base.CodecPacketHandler;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import net.minecraft.core.BlockPos;
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

    @SuppressWarnings("UnstableApiUsage")
    private static class Handler extends CodecPacketHandler<SelectFluidPacket> {

        public Handler() {
            super(ObjectByteCodec.create(
                    ExtraByteCodecs.BLOCK_POS.fieldOf(SelectFluidPacket::pos),
                    ByteCodecExtras.FLUID_HOLDER.fieldOf(SelectFluidPacket::holder),
                    SelectFluidPacket::new
            ));
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
