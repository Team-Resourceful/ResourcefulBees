package com.resourcefulbees.resourcefulbees.registry;

import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

import java.util.HashMap;
import java.util.Map;

public class ModEntities {

    private ModEntities() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    private static final Map<String, RegistryObject<EntityType<? extends CustomBeeEntity>>> MOD_BEES = new HashMap<>();

    public static Map<String, RegistryObject<EntityType<? extends CustomBeeEntity>>> getModBees() {
        return MOD_BEES;
    }
}
