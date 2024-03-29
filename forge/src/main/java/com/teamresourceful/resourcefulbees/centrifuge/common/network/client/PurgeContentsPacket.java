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

public record PurgeContentsPacket(BlockPos pos) implements Packet<PurgeContentsPacket> {

    public static final ResourceLocation ID=new ResourceLocation(ModConstants.MOD_ID,"purge");
    public static final Handler HANDLER=new Handler();

    @Override
    public ResourceLocation getID(){
            return ID;
            }

    @Override
    public PacketHandler<PurgeContentsPacket> getHandler(){
            return HANDLER;
            }

    private static class Handler implements PacketHandler<PurgeContentsPacket> {

        @Override
        public void encode(PurgeContentsPacket message, FriendlyByteBuf buffer) {
            buffer.writeBlockPos(message.pos);
        }

        @Override
        public PurgeContentsPacket decode(FriendlyByteBuf buffer) {
            return new PurgeContentsPacket(buffer.readBlockPos());
        }

        @Override
        public PacketContext handle(PurgeContentsPacket message) {
            return ((player, level) -> {
                if (level.isLoaded(message.pos)) {
                    BlockEntity blockEntity = level.getBlockEntity(message.pos);
                    if (blockEntity instanceof AbstractCentrifugeOutputEntity<?, ?> outputEntity) {
                        outputEntity.purgeContents();
                    }
                }
            });
        }
    }
}
