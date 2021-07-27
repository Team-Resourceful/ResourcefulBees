package com.teamresourceful.resourcefulbees.registry;

import com.teamresourceful.resourcefulbees.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.lib.constants.ModConstants;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ModEntities {

    private ModEntities() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    private static final Map<String, RegistryObject<EntityType<? extends CustomBeeEntity>>> MOD_BEES = new HashMap<>();

    public static Map<String, RegistryObject<EntityType<? extends CustomBeeEntity>>> getModBees() {
        return MOD_BEES;
    }

    public static Set<RegistryObject<EntityType<? extends CustomBeeEntity>>> getSetofModBees() {
        return new HashSet<>(MOD_BEES.values());
    }
}
