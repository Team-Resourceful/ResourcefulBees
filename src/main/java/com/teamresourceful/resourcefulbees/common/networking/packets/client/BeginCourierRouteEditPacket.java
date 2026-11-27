
package com.teamresourceful.resourcefulbees.common.networking.packets.client;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefulbees.common.blockentities.ColonyControllerBlockEntity;
import com.teamresourceful.resourcefulbees.common.items.HoneyDipperItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.logistics.CourierRouteEndpointEdit;
import com.teamresourceful.resourcefulbees.common.menus.ColonyControllerMenu;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;
import java.util.function.Consumer;

public record BeginCourierRouteEditPacket(
        BlockPos pos,
        UUID routeId,
        CourierRouteEndpointEdit.EndpointType endpointType
) implements Packet<BeginCourierRouteEditPacket> {

    private static final ByteCodec<UUID> UUID_BYTE_CODEC = ByteCodec.STRING.map(UUID::fromString, UUID::toString);
    private static final ByteCodec<CourierRouteEndpointEdit.EndpointType> ENDPOINT_TYPE_BYTE_CODEC = ByteCodec.ofEnum(CourierRouteEndpointEdit.EndpointType.class);


    public static final ServerboundPacketType<BeginCourierRouteEditPacket>
            PACKET_TYPE = new Type();

    @Override
    public PacketType<BeginCourierRouteEditPacket> type() {
        return PACKET_TYPE;
    }

    private static class Type
            extends CodecPacketType<BeginCourierRouteEditPacket>
            implements ServerboundPacketType<BeginCourierRouteEditPacket> {

        public Type() {
            super(
                    ModIdentifier.of("begin_courier_route_edit"),
                    ObjectByteCodec.create(
                            ExtraByteCodecs.BLOCK_POS.fieldOf(BeginCourierRouteEditPacket::pos),
                            UUID_BYTE_CODEC.fieldOf(BeginCourierRouteEditPacket::routeId),
                            ENDPOINT_TYPE_BYTE_CODEC.fieldOf(BeginCourierRouteEditPacket::endpointType),
                            BeginCourierRouteEditPacket::new
                    )
            );
        }

        @Override
        public Consumer<Player> handle(BeginCourierRouteEditPacket message) {
            return player -> {
                if (!(player.level() instanceof ServerLevel level)
                        || !(player.containerMenu instanceof ColonyControllerMenu menu)
                        || !menu.stillValid(player)
                        || !menu.getEntity().getBlockPos().equals(message.pos())) {
                    return;
                }

                if (!(level.getBlockEntity(message.pos())
                        instanceof ColonyControllerBlockEntity controller)
                        || controller != menu.getEntity()) {
                    return;
                }

                if (!controller.getRoutes().containsKey(message.routeId())) {
                    return;
                }

                ItemStack dipper = player.getMainHandItem();

                if (!(dipper.getItem() instanceof HoneyDipperItem)) {
                    dipper = player.getOffhandItem();
                }

                if (!(dipper.getItem() instanceof HoneyDipperItem)) {
                    player.sendSystemMessage(Component.literal(
                            "Hold a Honey Dipper to edit route endpoints."
                    ));
                    return;
                }

                CourierRouteEndpointEdit edit =
                        new CourierRouteEndpointEdit(
                                level.dimension(),
                                controller.getBlockPos(),
                                controller.getColonyId(),
                                message.routeId(),
                                message.endpointType()
                        );

                dipper.remove(ModDataComponents.DIPPER_ENTITY.get());
                dipper.remove(ModDataComponents.DIPPER_LOGISTICS.get());
                dipper.set(ModDataComponents.DIPPER_ROUTE_EDIT, edit);

                player.sendSystemMessage(Component.literal(
                        "Route edit selected. Click the new "
                                + message.endpointType().name().toLowerCase()
                                + " with your Honey Dipper."
                ));
            };
        }
    }
}
