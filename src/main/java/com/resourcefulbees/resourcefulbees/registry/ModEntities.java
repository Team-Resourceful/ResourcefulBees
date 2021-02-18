package com.resourcefulbees.resourcefulbees.registry;

import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;

import java.util.HashMap;
import java.util.Map;

public class ModEntities {
    public static final Map<String, RegistryObject<EntityType<? extends CustomBeeEntity>>> MOD_BEES = new HashMap<>();
}
