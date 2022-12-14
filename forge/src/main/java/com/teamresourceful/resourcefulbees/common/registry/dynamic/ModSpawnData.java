package com.teamresourceful.resourcefulbees.common.registry.dynamic;

import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.world.SpawnDataModifier;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class ModSpawnData {

    private static final Map<EntityType<?>, LocationPredicate> SPAWN_PREDICATES = new HashMap<>();

    public static Optional<LocationPredicate> getPredicate(EntityType<?> type) {
        return Optional.ofNullable(SPAWN_PREDICATES.get(type));
    }

    public static boolean test(EntityType<?> type, ServerLevel level, BlockPos pos) {
        return ModSpawnData.getPredicate(type)
            .map(predicate -> predicate.matches(level, pos.getX(), pos.getY(), pos.getZ()))
            .orElse(true);
    }

    public static void initalize(MinecraftServer server) {
        SPAWN_PREDICATES.clear();
        server.registryAccess().registryOrThrow(ForgeRegistries.Keys.BIOME_MODIFIERS)
            .holders()
            .map(Holder::value)
            .filter(SpawnDataModifier.class::isInstance)
            .map(SpawnDataModifier.class::cast)
            .forEach(modifier -> modifier
                .getSpawnPredicate()
                .ifPresent(predicate -> SPAWN_PREDICATES.put(modifier.getEntityType(), predicate))
            );
    }

    private ModSpawnData() {
        throw new UtilityClassError();
    }
}
