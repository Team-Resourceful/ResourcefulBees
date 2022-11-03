package com.teamresourceful.resourcefulbees.common.network.packets.client;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkHooks;

public record SwitchGuiPacket(BlockPos newGuiPos)
    implements Packet<SwitchGuiPacket> {

    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "go_back");
    public static final Handler HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<SwitchGuiPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<SwitchGuiPacket> {

        @Override
        public void encode(SwitchGuiPacket message, FriendlyByteBuf buffer) {
            buffer.writeBlockPos(message.newGuiPos);
        }

        @Override
        public SwitchGuiPacket decode(FriendlyByteBuf buffer) {
            return new SwitchGuiPacket(buffer.readBlockPos());
        }

        @Override
        public PacketContext handle(SwitchGuiPacket message) {
            return ((player, level) -> {
                if (level.isLoaded(message.newGuiPos)) {
                    BlockEntity blockEntity = level.getBlockEntity(message.newGuiPos);
                    if (blockEntity instanceof AbstractGUICentrifugeEntity guiCentrifugeEntity) {
                        NetworkHooks.openScreen((ServerPlayer) player, (MenuProvider) blockEntity, guiCentrifugeEntity::getOpenGUIPacket);
                    }
                }
            });
        }
    }
}
