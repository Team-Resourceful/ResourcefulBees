package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.world.BeeNestFeature;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFeatures {

    private ModFeatures() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, ResourcefulBees.MOD_ID);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> BEE_NEST_FEATURE = FEATURES.register("bee_nest_feature", () -> new BeeNestFeature(NoneFeatureConfiguration.CODEC));

    public static class ConfiguredFeatures {

        private ConfiguredFeatures() {
            throw new IllegalStateException(ModConstants.UTILITY_CLASS);
        }

        public static final Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> OVERWORLD_NESTS_HOLDER =
                FeatureUtils.register("resourcefulbees:overworld_nests", BEE_NEST_FEATURE.get());

        public static final Holder<PlacedFeature> OVERWORLD_NESTS = PlacementUtils.register("resourcefulbees:overworld_nests", OVERWORLD_NESTS_HOLDER,
                CountPlacement.of(3),
                PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                InSquarePlacement.spread(),
                RarityFilter.onAverageOnceEvery(CommonConfig.OVERWORLD_NEST_GENERATION_CHANCE.get()));

        public static final Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> THE_END_NESTS_HOLDER =
                FeatureUtils.register("resourcefulbees:the_end_nests", BEE_NEST_FEATURE.get());

        public static final Holder<PlacedFeature> THE_END_NESTS = PlacementUtils.register("resourcefulbees:the_end_nests", THE_END_NESTS_HOLDER,
                CountPlacement.of(2),
                PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                InSquarePlacement.spread(),
                RarityFilter.onAverageOnceEvery(CommonConfig.END_NEST_GENERATION_CHANCE.get()));

        public static final Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> NETHER_NESTS_HOLDER =
                FeatureUtils.register("resourcefulbees:nether_nests", BEE_NEST_FEATURE.get());

        public static final Holder<PlacedFeature> NETHER_NESTS = PlacementUtils.register("resourcefulbees:nether_nests", NETHER_NESTS_HOLDER,
                CountPlacement.of(3),
                PlacementUtils.RANGE_10_10,
                InSquarePlacement.spread(),
                RarityFilter.onAverageOnceEvery(CommonConfig.NETHER_NEST_GENERATION_CHANCE.get()));

        public static void registerConfiguredFeatures() {
            //IDK
        }
    }
}
