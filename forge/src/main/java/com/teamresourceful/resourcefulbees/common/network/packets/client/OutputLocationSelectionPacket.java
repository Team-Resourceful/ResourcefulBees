package com.teamresourceful.resourcefulbees.common.network.packets.client;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.enums.CentrifugeOutputType;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeInputEntity;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public record OutputLocationSelectionPacket(CentrifugeOutputType outputType, int recipeOutputSlot, BlockPos outputLocation, BlockPos inputLocation)
    implements Packet<OutputLocationSelectionPacket> {

    public static final ResourceLocation ID = new ResourceLocation(ModConstants.MOD_ID, "output_location_selection");
    public static final Handler HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<OutputLocationSelectionPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<OutputLocationSelectionPacket> {

        @Override
        public void encode(OutputLocationSelectionPacket message, FriendlyByteBuf buffer) {
            buffer.writeEnum(message.outputType);
            buffer.writeVarInt(message.recipeOutputSlot);
            buffer.writeBlockPos(message.outputLocation);
            buffer.writeBlockPos(message.inputLocation);
        }

        @Override
        public OutputLocationSelectionPacket decode(FriendlyByteBuf buffer) {
            return new OutputLocationSelectionPacket(buffer.readEnum(CentrifugeOutputType.class), buffer.readVarInt(), buffer.readBlockPos(), buffer.readBlockPos());
        }

        @Override
        public PacketContext handle(OutputLocationSelectionPacket message) {
            return ((player, level) -> {
                if (level.isLoaded(message.inputLocation())) {
                    BlockEntity blockEntity = level.getBlockEntity(message.inputLocation());
                    if (blockEntity instanceof CentrifugeInputEntity inputEntity) {
                        inputEntity.linkOutput(message.outputType(), message.recipeOutputSlot(), message.outputLocation());
                    }
                }
            });
        }
    }
}
