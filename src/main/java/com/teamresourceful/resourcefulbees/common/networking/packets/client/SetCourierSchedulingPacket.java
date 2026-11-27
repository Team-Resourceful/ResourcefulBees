
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
import org.jspecify.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

public record SetCourierSchedulingPacket(
        String courierId,
        boolean manualMode,
        String preferredRouteId
) implements Packet<SetCourierSchedulingPacket> {

    public SetCourierSchedulingPacket(UUID courierId, boolean manualMode, @Nullable UUID preferredRouteId) {
        this(courierId.toString(), manualMode, preferredRouteId == null ? "" : preferredRouteId.toString());
    }

    public static final ServerboundPacketType<SetCourierSchedulingPacket> PACKET_TYPE = new Type();

    @Override
    public PacketType<SetCourierSchedulingPacket> type() {
        return PACKET_TYPE;
    }

    private static class Type extends CodecPacketType<SetCourierSchedulingPacket> implements ServerboundPacketType<SetCourierSchedulingPacket> {

        public Type() {
            super(
                    ModIdentifier.of("set_courier_scheduling"),
                    ObjectByteCodec.create(
                            ByteCodec.STRING.fieldOf(SetCourierSchedulingPacket::courierId),
                            ByteCodec.BOOLEAN.fieldOf(SetCourierSchedulingPacket::manualMode),
                            ByteCodec.STRING.fieldOf(SetCourierSchedulingPacket::preferredRouteId),
                            SetCourierSchedulingPacket::new
                    )
            );
        }

        @Override
        public Consumer<Player> handle(SetCourierSchedulingPacket message) {
            return player -> {
                if (!(player.containerMenu instanceof ColonyControllerMenu menu) || !menu.stillValid(player)) {
                    return;
                }

                UUID courierId;
                UUID routeId = null;

                try {
                    courierId = UUID.fromString(message.courierId());

                    if (!message.preferredRouteId().isEmpty()) {
                        routeId = UUID.fromString(message.preferredRouteId());
                    }
                } catch (IllegalArgumentException _) {
                    return;
                }

                if (!message.manualMode() && routeId != null) {
                    return;
                }

                menu.getEntity().setCourierScheduling(
                        courierId,
                        message.manualMode(),
                        routeId
                );
            };
        }
    }
}
