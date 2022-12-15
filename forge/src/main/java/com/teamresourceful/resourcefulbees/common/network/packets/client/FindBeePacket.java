package com.teamresourceful.resourcefulbees.common.network.packets.client;

import com.teamresourceful.resourcefulbees.common.item.locator.BeeLocatorItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;

public record FindBeePacket(String bee, InteractionHand hand) implements Packet<FindBeePacket> {

    public static final ResourceLocation ID = new ResourceLocation(ModConstants.MOD_ID, "find_bee");
    public static final Handler HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<FindBeePacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<FindBeePacket> {

        @Override
        public void encode(FindBeePacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.bee);
            buffer.writeEnum(message.hand);
        }

        @Override
        public FindBeePacket decode(FriendlyByteBuf buffer) {
            return new FindBeePacket(buffer.readUtf(), buffer.readEnum(InteractionHand.class));
        }

        @Override
        public PacketContext handle(FindBeePacket message) {
            return (player, level) -> BeeLocatorItem.run(player, message.bee(), message.hand());
        }
    }
}
