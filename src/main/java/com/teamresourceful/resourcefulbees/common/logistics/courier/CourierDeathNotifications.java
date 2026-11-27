package com.teamresourceful.resourcefulbees.common.logistics.courier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public final class CourierDeathNotifications extends SavedData {

    private static final String DATA_NAME = "courier_death_notifications";

    public record Notification(
            UUID courierId,
            UUID colonyId,
            BlockPos controllerPos
    ) {
        public static final Codec<Notification> CODEC = RecordCodecBuilder.create(instance -> instance.group(UUIDUtil.CODEC.fieldOf("courier_id").forGetter(Notification::courierId), UUIDUtil.CODEC.fieldOf("colony_id").forGetter(Notification::colonyId), BlockPos.CODEC.fieldOf("controller_pos").forGetter(Notification::controllerPos)).apply(instance, Notification::new));
    }

    private static final Codec<CourierDeathNotifications> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Notification.CODEC.listOf().fieldOf("notifications").forGetter(data -> data.notifications)
    ).apply(instance, CourierDeathNotifications::new));

    public static final SavedDataType<CourierDeathNotifications> TYPE = new SavedDataType<>(ModIdentifier.of(DATA_NAME), CourierDeathNotifications::new, CODEC);

    private final List<Notification> notifications;

    public CourierDeathNotifications() {
        this.notifications = new ArrayList<>();
    }

    private CourierDeathNotifications(List<Notification> notifications) {
        this.notifications = new ArrayList<>(notifications);
    }

    public static CourierDeathNotifications get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public void recordDeath(UUID courierId, UUID colonyId, BlockPos controllerPos) {
        Notification notification = new Notification(courierId, colonyId, controllerPos.immutable());

        if (!notifications.contains(notification)) {
            notifications.add(notification);
            setDirty();
        }
    }

    public void process(UUID colonyId, BlockPos controllerPos, Consumer<UUID> handler) {
        List<Notification> matching = notifications.stream().filter(notification -> notification.colonyId().equals(colonyId) && notification.controllerPos().equals(controllerPos)).toList();

        if (matching.isEmpty()) {
            return;
        }

        // Process before removing. A failed handler must not
        // cause its notification to be silently discarded.
        for (Notification notification : matching) {
            handler.accept(notification.courierId());
        }

        notifications.removeAll(matching);
        setDirty();
    }
}
