package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.RegistryEntry;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.ResourcefulRegistries;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.ResourcefulRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public final class ModEntities {

    private ModEntities() {
        throw new UtilityClassError();
    }

    public static final ResourcefulRegistry<EntityType<?>> ENTITY_TYPES = ResourcefulRegistries.create(Registry.ENTITY_TYPE, ResourcefulBees.MOD_ID);
    private static final Map<String, RegistryEntry<EntityType<? extends CustomBeeEntity>>> MOD_BEES = new HashMap<>();

    public static Map<String, RegistryEntry<EntityType<? extends CustomBeeEntity>>> getModBees() {
        return MOD_BEES;
    }
}
