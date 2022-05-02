package com.teamresourceful.resourcefulbees.common.network.packets.centrifuge;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.commands.CentrifugeCommandHolder;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.commands.CentrifugeCommandSource;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractCentrifugeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record CommandMessage(BlockPos pos, String command) {

    public static void encode(CommandMessage message, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.pos);
        buffer.writeUtf(message.command);
    }

    public static CommandMessage decode(FriendlyByteBuf buffer) {
        return new CommandMessage(buffer.readBlockPos(), buffer.readUtf());
    }

    public static void handle(CommandMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null && player.level.isLoaded(message.pos)) {
                if (player.level.getBlockEntity(message.pos) instanceof AbstractCentrifugeEntity centrifuge) {
                    CentrifugeCommandHolder.callDispatcher(message.command, new CentrifugeCommandSource(centrifuge.controller(), player));
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
