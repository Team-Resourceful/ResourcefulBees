package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.entity.EntityType;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ModEntities {

    private ModEntities() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, ResourcefulBees.MOD_ID);
    private static final Map<String, EntityType<? extends CustomBeeEntity>> MOD_BEES = new HashMap<>();

    public static Map<String, EntityType<? extends CustomBeeEntity>> getModBees() {
        return MOD_BEES;
    }

    public static Set<EntityType<? extends CustomBeeEntity>> getSetOfModBees() {
        return new HashSet<>(MOD_BEES.values());
    }
}
