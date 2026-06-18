package com.teamresourceful.resourcefulbees.common.world.gen;


import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

public interface SpawnDataModifier {

    EntityType<?> getEntityType();

    Optional<LocationPredicate> getSpawnPredicate();
}
