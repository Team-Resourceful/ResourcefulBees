package com.teamresourceful.resourcefulbees.centrifuge.common.network.client;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.base.AbstractCentrifugeOutputEntity;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public record VoidExcessPacket(BlockPos pos, boolean value) implements Packet<VoidExcessPacket> {

    public static final ResourceLocation ID = new ResourceLocation(ModConstants.MOD_ID, "void_excess");
    public static final Handler HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<VoidExcessPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<VoidExcessPacket> {

        @Override
        public void encode(VoidExcessPacket message, FriendlyByteBuf buffer) {
            buffer.writeBlockPos(message.pos);
            buffer.writeBoolean(message.value);
        }

        @Override
        public VoidExcessPacket decode(FriendlyByteBuf buffer) {
            return new VoidExcessPacket(buffer.readBlockPos(), buffer.readBoolean());
        }

        @Override
        public PacketContext handle(VoidExcessPacket message) {
            return ((player, level) -> {
                if (level.isLoaded(message.pos)) {
                    BlockEntity blockEntity = level.getBlockEntity(message.pos);
                    if (blockEntity instanceof AbstractCentrifugeOutputEntity<?, ?> outputEntity) {
                        outputEntity.setVoidExcess(message.value);
                    }
                }
            });
        }
    }
}
