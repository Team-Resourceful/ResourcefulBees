package com.teamresourceful.resourcefulbees.platform.common.events;

import com.teamresourceful.resourcefulbees.platform.common.events.base.EventHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.NotNull;

public record RegisterSpawnPlacementsEvent(Registry registry) {

    public static final EventHelper<RegisterSpawnPlacementsEvent> EVENT = new EventHelper<>();

    public <T extends Mob> void register(EntityType<T> entityType, @NotNull SpawnPlacements.Type placementType, @NotNull Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> predicate) {
        registry.register(entityType, placementType, heightmap, predicate);
    }

    @FunctionalInterface
    public interface Registry {

        <T extends Mob> void register(EntityType<T> entityType, @NotNull SpawnPlacements.Type placementType, @NotNull Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> predicate);
    }
}
