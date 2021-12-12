package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.world.BeeNestFeature;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
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

        public static final PlacedFeature OVERWORLD_NESTS = BEE_NEST_FEATURE.get()
                .configured(FeatureConfiguration.NONE)
                .placed(
                        CountPlacement.of(3),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        InSquarePlacement.spread(),
                        RarityFilter.onAverageOnceEvery(CommonConfig.OVERWORLD_NEST_GENERATION_CHANCE.get())
                );

        public static final PlacedFeature THE_END_NESTS = BEE_NEST_FEATURE.get()
                .configured(FeatureConfiguration.NONE)
                .placed(
                        CountPlacement.of(2),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        InSquarePlacement.spread(),
                        RarityFilter.onAverageOnceEvery(CommonConfig.END_NEST_GENERATION_CHANCE.get())
                );

        public static final PlacedFeature NETHER_NESTS = BEE_NEST_FEATURE.get()
                .configured(FeatureConfiguration.NONE)
                .placed(
                        CountPlacement.of(3),
                        PlacementUtils.RANGE_10_10,
                        InSquarePlacement.spread(),
                        RarityFilter.onAverageOnceEvery(CommonConfig.NETHER_NEST_GENERATION_CHANCE.get())
                );


        public static void registerConfiguredFeatures() {
            Registry<PlacedFeature> registry = BuiltinRegistries.PLACED_FEATURE;

            Registry.register(registry, new ResourceLocation(ResourcefulBees.MOD_ID, "overworld_nests"), OVERWORLD_NESTS);
            Registry.register(registry, new ResourceLocation(ResourcefulBees.MOD_ID, "the_end_nests"), THE_END_NESTS);
            Registry.register(registry, new ResourceLocation(ResourcefulBees.MOD_ID, "nether_nests"), NETHER_NESTS);
        }
    }
}
