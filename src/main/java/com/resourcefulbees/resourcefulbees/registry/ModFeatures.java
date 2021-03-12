package com.resourcefulbees.resourcefulbees.registry;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.world.BeeNestFeature;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModFeatures {

    private ModFeatures() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, ResourcefulBees.MOD_ID);

    public static final RegistryObject<Feature<NoFeatureConfig>> BEE_NEST_FEATURE = FEATURES.register("bee_nest_feature", () -> new BeeNestFeature(NoFeatureConfig.CODEC));

    public static class ConfiguredFeatures {

        private ConfiguredFeatures() {
            throw new IllegalStateException(ModConstants.UTILITY_CLASS);
        }

        public static final ConfiguredFeature<?, ?> OVERWORLD_NESTS = BEE_NEST_FEATURE.get()
                .configured(IFeatureConfig.NONE)
                .decorated(Placement.HEIGHTMAP_WORLD_SURFACE.configured(NoPlacementConfig.INSTANCE))
                .chance(Config.OVERWORLD_NEST_GENERATION_CHANCE.get())
                .squared()
                .countRandom(3);

        public static final ConfiguredFeature<?, ?> THE_END_NESTS = BEE_NEST_FEATURE.get()
                .configured(IFeatureConfig.NONE)
                .decorated(Placement.HEIGHTMAP_WORLD_SURFACE.configured(NoPlacementConfig.INSTANCE))
                .chance(Config.END_NEST_GENERATION_CHANCE.get())
                .squared()
                .countRandom(2);

        public static final ConfiguredFeature<?, ?> NETHER_NESTS = BEE_NEST_FEATURE.get()
                .configured(IFeatureConfig.NONE)
                .decorated(Features.Placements.RANGE_10_20_ROOFED)
                .chance(Config.NETHER_NEST_GENERATION_CHANCE.get())
                .squared()
                .countRandom(4);

        public static void registerConfiguredFeatures() {
            Registry<ConfiguredFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_FEATURE;

            Registry.register(registry, new ResourceLocation(ResourcefulBees.MOD_ID, "overworld_nests"), OVERWORLD_NESTS);
            Registry.register(registry, new ResourceLocation(ResourcefulBees.MOD_ID, "the_end_nests"), THE_END_NESTS);
            Registry.register(registry, new ResourceLocation(ResourcefulBees.MOD_ID, "nether_nests"), NETHER_NESTS);
        }
    }
}
