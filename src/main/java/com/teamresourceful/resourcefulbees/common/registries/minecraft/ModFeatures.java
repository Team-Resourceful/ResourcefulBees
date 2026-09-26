package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.mojang.serialization.MapCodec;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registries.RegistryHelper;
import com.teamresourceful.resourcefulbees.common.world.gen.BeeNestFeature;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;

public final class ModFeatures {

    private ModFeatures() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final ResourcefulRegistry<MapCodec<? extends Feature>> FEATURE_TYPES = RegistryHelper.create(BuiltInRegistries.FEATURE_TYPE, ModConstants.MOD_ID);

    public static final RegistryEntry<MapCodec<? extends Feature>> BEE_NEST_FEATURE = FEATURE_TYPES.register("bee_nest_feature", () -> BeeNestFeature.CODEC);
}
