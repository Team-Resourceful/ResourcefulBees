package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.entities.entity.ThrownPollenItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.HashMap;
import java.util.Map;

public final class ModEntities {

    private ModEntities() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final ResourcefulRegistry<EntityType<?>> ENTITY_TYPES = ResourcefulRegistries.create(BuiltInRegistries.ENTITY_TYPE, ModConstants.MOD_ID);
    public static final ResourcefulRegistry<EntityType<?>> BEES = ResourcefulRegistries.create(ENTITY_TYPES);
    private static final Map<Identifier, RegistryEntry<EntityType<? extends CustomBeeEntity>>> MOD_BEES = new HashMap<>();
    public static Map<Identifier, RegistryEntry<EntityType<? extends CustomBeeEntity>>> getModBees() {
        return MOD_BEES;
    }

    public static final RegistryEntry<EntityType<? extends ThrownPollenItem>> THROWN_POLLEN = ENTITY_TYPES.register("thrown_pollen",() ->
            EntityType.Builder.of((EntityType.EntityFactory<ThrownPollenItem>) ThrownPollenItem::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, ModIdentifier.of("thrown_pollen"))));

}