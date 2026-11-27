package com.teamresourceful.resourcefulbees.common.networking.packets.client;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.menus.ColonyControllerMenu;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public record SetColonyControllerTabPacket(int tab)
        implements Packet<SetColonyControllerTabPacket> {

    public static final ServerboundPacketType<SetColonyControllerTabPacket> PACKET_TYPE =
            new Type();

    @Override
    public PacketType<SetColonyControllerTabPacket> type() {
        return PACKET_TYPE;
    }

    private static class Type
            extends CodecPacketType<SetColonyControllerTabPacket>
            implements ServerboundPacketType<SetColonyControllerTabPacket> {

        public Type() {
            super(
                    ModIdentifier.of("set_colony_controller_tab"),
                    ObjectByteCodec.create(
                            ByteCodec.VAR_INT.fieldOf(SetColonyControllerTabPacket::tab),
                            SetColonyControllerTabPacket::new
                    )
            );
        }

        @Override
        public Consumer<Player> handle(SetColonyControllerTabPacket message) {
            return player -> {
                if (player.containerMenu instanceof ColonyControllerMenu menu
                        && menu.stillValid(player)) {
                    menu.setActiveTab(message.tab());
                }
            };
        }
    }
}