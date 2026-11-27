package com.teamresourceful.resourcefulbees.common.networking.packets.client;

import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefulbees.common.blockentities.ColonyControllerBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.menus.ColonyControllerMenu;
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;
import java.util.function.Consumer;

public record UpdateColonyRoutePacket(
        BlockPos pos,
        CompoundTag data
) implements Packet<UpdateColonyRoutePacket> {

    private static final int SET_ENABLED = 0;
    private static final int SET_PRIORITY = 1;
    private static final int SET_DELETE = 2;

    public static final ServerboundPacketType<UpdateColonyRoutePacket> PACKET_TYPE = new Type();

    public static UpdateColonyRoutePacket enabled(BlockPos pos, UUID routeId, boolean enabled) {
        CompoundTag data = new CompoundTag();
        data.putString("RouteId", routeId.toString());
        data.putInt("Action", SET_ENABLED);
        data.putBoolean("Enabled", enabled);

        return new UpdateColonyRoutePacket(pos, data);
    }

    public static UpdateColonyRoutePacket priority(BlockPos pos, UUID routeId, int priority) {
        CompoundTag data = new CompoundTag();
        data.putString("RouteId", routeId.toString());
        data.putInt("Action", SET_PRIORITY);
        data.putInt("Priority", priority);

        return new UpdateColonyRoutePacket(pos, data);
    }

    public static UpdateColonyRoutePacket delete(BlockPos pos, UUID routeId) {
        CompoundTag data = new CompoundTag();

        data.putString("RouteId", routeId.toString());
        data.putInt("Action", 2);

        return new UpdateColonyRoutePacket(pos, data);
    }

    @Override
    public PacketType<UpdateColonyRoutePacket> type() {
        return PACKET_TYPE;
    }

    private static class Type extends CodecPacketType<UpdateColonyRoutePacket> implements ServerboundPacketType<UpdateColonyRoutePacket> {

        public Type() {
            super(
                    ModIdentifier.of("update_colony_route"),
                    ObjectByteCodec.create(
                            ExtraByteCodecs.BLOCK_POS.fieldOf(UpdateColonyRoutePacket::pos),
                            ExtraByteCodecs.NONNULL_COMPOUND_TAG.fieldOf(UpdateColonyRoutePacket::data),
                            UpdateColonyRoutePacket::new
                    )
            );
        }

        @Override
        public Consumer<Player> handle(UpdateColonyRoutePacket message) {
            return player -> {
                if (!(player.containerMenu instanceof ColonyControllerMenu menu)
                        || !menu.stillValid(player)
                        || !menu.getEntity().getBlockPos().equals(message.pos())) {
                    return;
                }

                if (!(player.level().getBlockEntity(message.pos())
                        instanceof ColonyControllerBlockEntity controller)
                        || controller != menu.getEntity()) {
                    return;
                }

                String routeIdString =
                        message.data().getStringOr("RouteId", "");

                UUID routeId;

                try {
                    routeId = UUID.fromString(routeIdString);
                } catch (IllegalArgumentException _) {
                    return;
                }

                if (!controller.getRoutes().containsKey(routeId)) {
                    return;
                }

                int action = message.data().getIntOr("Action", -1);

                switch (action) {
                    case SET_ENABLED -> {
                        boolean enabled = message.data().getBooleanOr("Enabled", false);
                        controller.setRouteEnabled(routeId, enabled);
                    }

                    case SET_PRIORITY -> {
                        int priority = message.data().getIntOr("Priority", -1);

                        if (priority >= 0 && priority <= 100) {
                            controller.setRoutePriority(routeId, priority);
                        }
                    }

                    case SET_DELETE -> controller.removeRoute(routeId);

                    default -> {
                        // Ignore unknown actions.
                    }
                }
            };
        }
    }
}