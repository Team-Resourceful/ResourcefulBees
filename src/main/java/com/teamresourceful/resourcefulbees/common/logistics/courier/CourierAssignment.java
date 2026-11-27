
package com.teamresourceful.resourcefulbees.common.logistics.courier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;

import java.util.UUID;

public record CourierAssignment(
        UUID colonyId,
        UUID routeId
) {

    public static final Codec<CourierAssignment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("colony_id")
                    .forGetter(CourierAssignment::colonyId),
            UUIDUtil.CODEC.fieldOf("route_id")
                    .forGetter(CourierAssignment::routeId)
    ).apply(instance, CourierAssignment::new));
}
