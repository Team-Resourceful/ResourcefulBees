package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModFeatures {

    private ModFeatures() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, ResourcefulBees.MOD_ID);

    //public static final RegistryObject<Feature<NoneFeatureConfiguration>> BEE_NEST_FEATURE = FEATURES.register("bee_nest_feature", () -> new BeeNestFeature(NoFeatureConfig.CODEC));

    public static class ConfiguredFeatures {

        private ConfiguredFeatures() {
            throw new IllegalStateException(ModConstants.UTILITY_CLASS);
        }

        /* TODO FIX
        public static final ConfiguredFeature<?, ?> OVERWORLD_NESTS = BEE_NEST_FEATURE.get()
                .configured(FeatureConfiguration.NONE)
                .decorated(Placement.HEIGHTMAP_WORLD_SURFACE.configured(NoneFeatureConfiguration.INSTANCE))
                .chance(CommonConfig.OVERWORLD_NEST_GENERATION_CHANCE.get())
                .squared()
                .countRandom(3);

        public static final ConfiguredFeature<?, ?> THE_END_NESTS = BEE_NEST_FEATURE.get()
                .configured(FeatureConfiguration.NONE)
                .decorated(Placement.HEIGHTMAP_WORLD_SURFACE.configured(NoneFeatureConfiguration.INSTANCE))
                .chance(CommonConfig.END_NEST_GENERATION_CHANCE.get())
                .squared()
                .countRandom(2);

        public static final ConfiguredFeature<?, ?> NETHER_NESTS = BEE_NEST_FEATURE.get()
                .configured(FeatureConfiguration.NONE)
                .decorated(Features.Placements.RANGE_10_20_ROOFED)
                .chance(CommonConfig.NETHER_NEST_GENERATION_CHANCE.get())
                .squared()
                .countRandom(4);

         */

        public static void registerConfiguredFeatures() {
            Registry<ConfiguredFeature<?, ?>> registry = BuiltinRegistries.CONFIGURED_FEATURE;

            //Registry.register(registry, new ResourceLocation(ResourcefulBees.MOD_ID, "overworld_nests"), OVERWORLD_NESTS);
            //Registry.register(registry, new ResourceLocation(ResourcefulBees.MOD_ID, "the_end_nests"), THE_END_NESTS);
            //Registry.register(registry, new ResourceLocation(ResourcefulBees.MOD_ID, "nether_nests"), NETHER_NESTS);
        }
    }
}
