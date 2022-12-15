package com.teamresourceful.resourcefulbees.common.network.packets.client;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.commands.CentrifugeCommandHolder;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.commands.CentrifugeCommandSource;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractCentrifugeEntity;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record CommandPacket(BlockPos pos, String command) implements Packet<CommandPacket> {

    public static final ResourceLocation ID = new ResourceLocation(ModConstants.MOD_ID, "centrifuge_command");
    public static final Handler HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<CommandPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<CommandPacket> {

        @Override
        public void encode(CommandPacket message, FriendlyByteBuf buffer) {
            buffer.writeBlockPos(message.pos);
            buffer.writeUtf(message.command);
        }

        @Override
        public CommandPacket decode(FriendlyByteBuf buffer) {
            return new CommandPacket(buffer.readBlockPos(), buffer.readUtf());
        }

        @Override
        public PacketContext handle(CommandPacket message) {
            return (player, level) -> {
                if (player instanceof ServerPlayer serverPlayer && level.isLoaded(message.pos)) {
                    if (level.getBlockEntity(message.pos) instanceof AbstractCentrifugeEntity centrifuge) {
                        CentrifugeCommandHolder.callDispatcher(message.command, new CentrifugeCommandSource(centrifuge.controller(), serverPlayer));
                    }
                }
            };
        }
    }
}
