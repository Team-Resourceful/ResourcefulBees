package com.teamresourceful.resourcefulbees.common.network.packets;

import com.teamresourceful.resourcefulbees.common.item.locator.BeeLocator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record FindBeeMessage(String bee, InteractionHand hand) {

    public static void encode(FindBeeMessage message, FriendlyByteBuf buffer) {
        buffer.writeUtf(message.bee);
        buffer.writeEnum(message.hand);
    }

    public static FindBeeMessage decode(FriendlyByteBuf buffer) {
        return new FindBeeMessage(buffer.readUtf(), buffer.readEnum(InteractionHand.class));
    }

    public static void handle(FindBeeMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null) {
                BeeLocator.run(player, message.bee, message.hand);
            }
        });
        context.get().setPacketHandled(true);
    }
}
