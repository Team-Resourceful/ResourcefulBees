package com.teamresourceful.resourcefulbees.platform.common.events;

import com.teamresourceful.resourcefulbees.platform.common.events.base.EventHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.function.BiConsumer;

public record RegisterEntityAttributesEvent(BiConsumer<EntityType<? extends LivingEntity>, AttributeSupplier> registry) {

    public static final EventHelper<RegisterEntityAttributesEvent> EVENT = new EventHelper<>();

    public void register(EntityType<? extends LivingEntity> entity, AttributeSupplier map) {
        registry.accept(entity, map);
    }
}
