
package com.teamresourceful.resourcefulbees.common.logistics;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;

import java.util.UUID;

public record StoredManualCourierRoute(
        UUID courierId,
        UUID routeId
) {
    public static final Codec<StoredManualCourierRoute> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("courier_id").forGetter(StoredManualCourierRoute::courierId),
            UUIDUtil.CODEC.fieldOf("route_id").forGetter(StoredManualCourierRoute::routeId)
    ).apply(instance, StoredManualCourierRoute::new));
}
