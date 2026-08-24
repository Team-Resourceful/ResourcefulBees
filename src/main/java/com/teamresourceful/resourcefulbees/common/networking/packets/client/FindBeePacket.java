//package com.teamresourceful.resourcefulbees.common.networking.packets.client;
//
//import com.teamresourceful.bytecodecs.base.ByteCodec;
//import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
//import com.teamresourceful.resourcefulbees.common.items.locator.BeeLocatorItem;
//import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
//import com.teamresourceful.resourcefullib.common.network.Packet;
//import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
//import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
//import net.minecraft.resources.Identifier;
//import net.minecraft.world.entity.player.Player;
//
//import java.util.function.Consumer;
//
//public record FindBeePacket(String bee, int slot) implements Packet<FindBeePacket> {
//
//    public static final ServerboundPacketType<FindBeePacket> PACKET_TYPE = new PacketType();
//
//    @Override
//    public com.teamresourceful.resourcefullib.common.network.base.PacketType<FindBeePacket> type() {
//        return PACKET_TYPE;
//    }
//
//    private static class PacketType extends CodecPacketType<FindBeePacket> implements ServerboundPacketType<FindBeePacket> {
//        public PacketType() {
//            super(
//                    ModIdentifier.of("find_bee"),
//                    ObjectByteCodec.create(
//                            ByteCodec.STRING.fieldOf(FindBeePacket::bee),
//                            ByteCodec.VAR_INT.fieldOf(FindBeePacket::slot),
//                            FindBeePacket::new
//                    )
//            );
//        }
//
//        @Override
//        public Consumer<Player> handle(FindBeePacket message) {
//            return player-> BeeLocatorItem.run(player, message.bee(), message.slot);
//        }
//    }
//}
