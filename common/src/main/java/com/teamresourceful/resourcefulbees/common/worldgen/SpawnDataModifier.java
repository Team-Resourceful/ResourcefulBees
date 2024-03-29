package com.teamresourceful.resourcefulbees.common.worldgen;

import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

public interface SpawnDataModifier {

    EntityType<?> getEntityType();

    Optional<LocationPredicate> getSpawnPredicate();
}
