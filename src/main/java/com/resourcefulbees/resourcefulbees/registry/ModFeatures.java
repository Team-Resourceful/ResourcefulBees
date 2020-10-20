package com.resourcefulbees.resourcefulbees.registry;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.world.BeeNestFeature;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, ResourcefulBees.MOD_ID);

    public static final RegistryObject<Feature<NoFeatureConfig>> BEE_NEST_FEATURE = FEATURES.register("bee_nest_feature", () -> new BeeNestFeature(NoFeatureConfig.CODEC));

    public static class ConfiguredFeatures {

        public static final ConfiguredFeature<?, ?> OVERWORLD_NESTS = BEE_NEST_FEATURE.get()
                .configure(IFeatureConfig.NO_FEATURE_CONFIG)
                .decorate(Placement.HEIGHTMAP_WORLD_SURFACE.configure(new NoPlacementConfig()))
                .applyChance(Config.OVERWORLD_NEST_GENERATION_CHANCE.get())
                .spreadHorizontally()
                .repeatRandomly(3);

        public static final ConfiguredFeature<?, ?> THE_END_NESTS = BEE_NEST_FEATURE.get()
                .configure(IFeatureConfig.NO_FEATURE_CONFIG)
                .decorate(Placement.HEIGHTMAP_WORLD_SURFACE.configure(new NoPlacementConfig()))
                .applyChance(Config.END_NEST_GENERATION_CHANCE.get())
                .spreadHorizontally()
                .repeatRandomly(2);

        public static final ConfiguredFeature<?, ?> NETHER_NESTS = BEE_NEST_FEATURE.get()
                .configure(IFeatureConfig.NO_FEATURE_CONFIG)
                .decorate(Features.Placements.NETHER_ORE)
                .applyChance(Config.NETHER_NEST_GENERATION_CHANCE.get())
                .spreadHorizontally()
                .repeatRandomly(4);

        public static void registerConfiguredFeatures() {
            Registry<ConfiguredFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_FEATURE;

            Registry.register(registry, new ResourceLocation(ResourcefulBees.MOD_ID, "overworld_nests"), OVERWORLD_NESTS);
            Registry.register(registry, new ResourceLocation(ResourcefulBees.MOD_ID, "the_end_nests"), THE_END_NESTS);
            Registry.register(registry, new ResourceLocation(ResourcefulBees.MOD_ID, "nether_nests"), NETHER_NESTS);
        }
    }
}
