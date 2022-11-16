package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.registry.api.RegistryEntry;
import com.teamresourceful.resourcefulbees.common.registry.api.ResourcefulRegistries;
import com.teamresourceful.resourcefulbees.common.registry.api.ResourcefulRegistry;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ModEntities {

    private ModEntities() {
        throw new UtilityClassError();
    }

    public static final ResourcefulRegistry<EntityType<?>> ENTITY_TYPES = ResourcefulRegistries.create(Registry.ENTITY_TYPE, ResourcefulBees.MOD_ID);
    private static final Map<String, RegistryEntry<EntityType<? extends CustomBeeEntity>>> MOD_BEES = new HashMap<>();

    public static Map<String, RegistryEntry<EntityType<? extends CustomBeeEntity>>> getModBees() {
        return MOD_BEES;
    }

    public static Set<RegistryEntry<EntityType<? extends CustomBeeEntity>>> getSetOfModBees() {
        return new HashSet<>(MOD_BEES.values());
    }
}
