package com.teamresourceful.resourcefulbees.platform.common.util.forge;

import com.teamresourceful.resourcefulbees.common.entities.entity.ThrownMutatedPollen;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModEntities;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.world.entity.EntityType;

public class TempPlatformUtilsImpl {

    public static RegistryEntry<EntityType<? extends ThrownMutatedPollen>> getThrownMutatedPollenType() {
        return ModEntities.THROWN_MUTATED_POLLEN;
    }
}
