package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.entities.CustomBeeEntityType;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.entities.entity.ThrownPollenItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.registries.compat.NamespacedRegistry;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class ModEntities {

    private ModEntities() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final NamespacedRegistry<EntityType<?>> ENTITY_TYPES = new NamespacedRegistry<>(
            ResourcefulRegistries.create(
                    BuiltInRegistries.ENTITY_TYPE,
                    ModConstants.MOD_ID
            ),
            namespace -> ResourcefulRegistries.create(
                    BuiltInRegistries.ENTITY_TYPE,
                    namespace
            )
    );

    private static final Map<Identifier, RegistryEntry<CustomBeeEntityType<ResourcefulBee>>> MOD_BEES = new HashMap<>();

    public static Map<Identifier, RegistryEntry<CustomBeeEntityType<ResourcefulBee>>> getModBees() {
        return Collections.unmodifiableMap(MOD_BEES);
    }

    public static void addBee(Identifier id, RegistryEntry<CustomBeeEntityType<ResourcefulBee>> holder) {
        MOD_BEES.put(id, holder);
    }

    public static final RegistryEntry<EntityType<? extends ThrownPollenItem>> THROWN_POLLEN = ENTITY_TYPES.register("thrown_pollen",() ->
            EntityType.Builder.of((EntityType.EntityFactory<ThrownPollenItem>) ThrownPollenItem::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, ModIdentifier.of("thrown_pollen"))));

}