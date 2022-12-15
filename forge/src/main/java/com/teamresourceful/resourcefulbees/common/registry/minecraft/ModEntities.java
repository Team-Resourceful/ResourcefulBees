package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.entity.projectile.ThrownMutatedPollen;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.RegistryEntry;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.ResourcefulRegistries;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.ResourcefulRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.HashMap;
import java.util.Map;

public final class ModEntities {

    private ModEntities() {
        throw new UtilityClassError();
    }
    public static final ResourcefulRegistry<EntityType<?>> ENTITY_TYPES = ResourcefulRegistries.create(Registry.ENTITY_TYPE, ModConstants.MOD_ID);
    public static final ResourcefulRegistry<EntityType<?>> BEES = ResourcefulRegistries.create(ENTITY_TYPES);
    private static final Map<String, RegistryEntry<EntityType<? extends CustomBeeEntity>>> MOD_BEES = new HashMap<>();
    public static Map<String, RegistryEntry<EntityType<? extends CustomBeeEntity>>> getModBees() {
        return MOD_BEES;
    }

    public static final RegistryEntry<EntityType<? extends ThrownMutatedPollen>> THROWN_MUTATED_POLLEN = ENTITY_TYPES.register("thrown_mutated_pollen",() ->
            EntityType.Builder.of((EntityType.EntityFactory<ThrownMutatedPollen>) ThrownMutatedPollen::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("resourcefulbees:thrown_mutated_pollen"));

}
