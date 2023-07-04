package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.registries.RegistryHelper;
import com.teamresourceful.resourcefulbees.common.worldgen.BeeNestFeature;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public final class ModFeatures {

    private ModFeatures() {
        throw new UtilityClassError();
    }

    public static final ResourcefulRegistry<Feature<?>> FEATURES = RegistryHelper.create(BuiltInRegistries.FEATURE, ModConstants.MOD_ID);

    public static final RegistryEntry<Feature<NoneFeatureConfiguration>> BEE_NEST_FEATURE = FEATURES.register("bee_nest_feature", () -> new BeeNestFeature(NoneFeatureConfiguration.CODEC));
}
