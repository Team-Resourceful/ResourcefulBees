package com.teamresourceful.resourcefulbees.common.networking.packets.client;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefulbees.common.items.locator.BeeLocatorItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.common.networking.base.CodecPacketHandler;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import net.minecraft.resources.ResourceLocation;

public record FindBeePacket(String bee, int slot) implements Packet<FindBeePacket> {

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

    @SuppressWarnings("UnstableApiUsage")
    private static class Handler extends CodecPacketHandler<FindBeePacket> {

        public Handler() {
            super(ObjectByteCodec.create(
                ByteCodec.STRING.fieldOf(FindBeePacket::bee),
                ByteCodec.VAR_INT.fieldOf(FindBeePacket::slot),
                FindBeePacket::new
            ));
        }

        @Override
        public PacketContext handle(FindBeePacket message) {
            return (player, level) -> BeeLocatorItem.run(player, message.bee(), message.slot);
        }
    }
}
