
package com.teamresourceful.resourcefulbees.common.networking.packets.server;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefulbees.client.screen.OpenCourierRouteSelectorClientHandler;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.logistics.CourierEndpoint;
import com.teamresourceful.resourcefulbees.common.logistics.CourierResourceType;
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;

import java.util.EnumSet;
import java.util.Set;

public record OpenCourierRouteSelectorPacket(
        BlockPos destinationPos,
        Direction destinationSide,
        int availableMask,
        InteractionHand hand
) implements Packet<OpenCourierRouteSelectorPacket> {

    public static final ClientboundPacketType<OpenCourierRouteSelectorPacket>
            PACKET_TYPE = new Type();

    public static OpenCourierRouteSelectorPacket of(
            CourierEndpoint destination,
            Set<CourierResourceType> available,
            InteractionHand hand
    ) {
        int mask = 0;

        for (CourierResourceType type : available) {
            mask |= 1 << type.ordinal();
        }

        return new OpenCourierRouteSelectorPacket(
                destination.pos(),
                destination.side(),
                mask,
                hand
        );
    }

    public Set<CourierResourceType> availableTypes() {
        EnumSet<CourierResourceType> types =
                EnumSet.noneOf(CourierResourceType.class);

        for (CourierResourceType type : CourierResourceType.values()) {
            if ((availableMask & (1 << type.ordinal())) != 0) {
                types.add(type);
            }
        }

        return types;
    }

    @Override
    public PacketType<OpenCourierRouteSelectorPacket> type() {
        return PACKET_TYPE;
    }

    private static class Type extends CodecPacketType<OpenCourierRouteSelectorPacket> implements ClientboundPacketType<OpenCourierRouteSelectorPacket> {

        public Type() {
            super(
                    ModIdentifier.of("open_courier_route_selector"),
                    ObjectByteCodec.create(
                            ExtraByteCodecs.BLOCK_POS.fieldOf(
                                    OpenCourierRouteSelectorPacket::destinationPos
                            ),
                            ByteCodec.ofEnum(Direction.class).fieldOf(
                                    OpenCourierRouteSelectorPacket::destinationSide
                            ),
                            ByteCodec.VAR_INT.fieldOf(
                                    OpenCourierRouteSelectorPacket::availableMask
                            ),
                            ByteCodec.ofEnum(InteractionHand.class).fieldOf(
                                    OpenCourierRouteSelectorPacket::hand
                            ),
                            OpenCourierRouteSelectorPacket::new
                    )
            );
        }

        @Override
        public Runnable handle(OpenCourierRouteSelectorPacket message) {
            return () -> OpenCourierRouteSelectorClientHandler.handle(message);
        }
    }
}
