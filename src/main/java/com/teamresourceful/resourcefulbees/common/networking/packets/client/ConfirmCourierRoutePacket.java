
package com.teamresourceful.resourcefulbees.common.networking.packets.client;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefulbees.common.items.HoneyDipperItem;
import com.teamresourceful.resourcefulbees.common.items.logistics.HoneyDipperLogisticsHandler;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.logistics.CourierEndpoint;
import com.teamresourceful.resourcefulbees.common.logistics.CourierResourceType;
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public record ConfirmCourierRoutePacket(
        BlockPos destinationPos,
        Direction destinationSide,
        CourierResourceType resourceType,
        InteractionHand hand
) implements Packet<ConfirmCourierRoutePacket> {

    public static final ServerboundPacketType<ConfirmCourierRoutePacket>
            PACKET_TYPE = new Type();

    public ConfirmCourierRoutePacket(
            CourierEndpoint destination,
            CourierResourceType resourceType,
            InteractionHand hand
    ) {
        this(
                destination.pos(),
                destination.side(),
                resourceType,
                hand
        );
    }

    @Override
    public PacketType<ConfirmCourierRoutePacket> type() {
        return PACKET_TYPE;
    }

    private static class Type
            extends CodecPacketType<ConfirmCourierRoutePacket>
            implements ServerboundPacketType<ConfirmCourierRoutePacket> {

        public Type() {
            super(
                    ModIdentifier.of("confirm_courier_route"),
                    ObjectByteCodec.create(
                            ExtraByteCodecs.BLOCK_POS.fieldOf(
                                    ConfirmCourierRoutePacket::destinationPos
                            ),
                            ByteCodec.ofEnum(Direction.class).fieldOf(
                                    ConfirmCourierRoutePacket::destinationSide
                            ),
                            ByteCodec.ofEnum(CourierResourceType.class).fieldOf(
                                    ConfirmCourierRoutePacket::resourceType
                            ),
                            ByteCodec.ofEnum(InteractionHand.class).fieldOf(
                                    ConfirmCourierRoutePacket::hand
                            ),
                            ConfirmCourierRoutePacket::new
                    )
            );
        }

        @Override
        public Consumer<Player> handle(ConfirmCourierRoutePacket message) {
            return player -> {
                if (!(player.level() instanceof ServerLevel level)) {
                    return;
                }

                ItemStack stack = player.getItemInHand(message.hand());

                if (!(stack.getItem() instanceof HoneyDipperItem)) {
                    return;
                }

                if (!HoneyDipperLogisticsHandler.hasSelection(stack)) {
                    return;
                }

                CourierEndpoint destination = new CourierEndpoint(
                        message.destinationPos(),
                        message.destinationSide()
                );

                HoneyDipperLogisticsHandler.EndpointResult result =
                        HoneyDipperLogisticsHandler.confirmRouteType(
                                level,
                                stack,
                                destination,
                                message.resourceType()
                        );

                HoneyDipperLogisticsHandler.sendConfirmationMessage(
                        player,
                        result,
                        destination
                );
            };
        }
    }
}
