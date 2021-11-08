package com.teamresourceful.resourcefulbees.common.init;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.beedata.spawning.SpawnData;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.lib.ModPaths;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModEntities;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModFeatures;
import com.teamresourceful.resourcefulbees.common.utils.FileUtils;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.util.JSONUtils;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.io.Reader;
import java.util.Locale;

import static com.teamresourceful.resourcefulbees.ResourcefulBees.LOGGER;

public class BeeSetup {

    private BeeSetup() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void setupBees() {
        if (Boolean.TRUE.equals(CommonConfig.ENABLE_EASTER_EGG_BEES.get())) {
            FileUtils.setupDevResources("/data/resourcefulbees/dev/dev_bees", BeeSetup::parseBee, "Could not stream dev bees!");
        }

        if (Boolean.TRUE.equals(CommonConfig.GENERATE_DEFAULTS.get())) {
            FileUtils.setupDefaultFiles("/data/resourcefulbees/defaults/default_bees", ModPaths.BEES);
        }

        LOGGER.info("Loading Custom Bees...");
        FileUtils.streamFilesAndParse(ModPaths.BEES, BeeSetup::parseBee, "Could not stream bees!");

        BeeRegistry.getRegistry().regenerateCustomBeeData();
    }

    private static void parseBee(Reader reader, String name) {
        JsonObject jsonObject = JSONUtils.fromJson(ModConstants.GSON, reader, JsonObject.class);
        name = Codec.STRING.fieldOf("name").orElse(name).codec().fieldOf("CoreData").codec().parse(JsonOps.INSTANCE, jsonObject).get().orThrow();
        BeeRegistry.getRegistry().cacheRawBeeData(name.toLowerCase(Locale.ENGLISH).replace(" ", "_"), jsonObject);
    }

    public static void onBiomeLoad(BiomeLoadingEvent event) {
        if (event.getName() != null && BeeRegistry.isSpawnableBiome(event.getName())) {
            boolean isFlowerForest = event.getName().equals(Biomes.FLOWER_FOREST.getRegistryName());
            BeeRegistry.getSpawnableBiome(event.getName()).forEach(customBeeData -> addSpawnSetting(customBeeData, event, isFlowerForest));
            if (Boolean.TRUE.equals(CommonConfig.GENERATE_BEE_NESTS.get())) {
                addNestFeature(event);
            }
        }
    }

    private static void addSpawnSetting(CustomBeeData customBeeData, BiomeLoadingEvent event, boolean isFlowerForest) {
        EntityType<?> entityType = customBeeData.getEntityType();
        if (event.getName() != null) {
            SpawnData spawnData = customBeeData.getSpawnData();
            event.getSpawns().getSpawner(ModConstants.BEE_MOB_CATEGORY)
                    .add(spawnData.getSpawnerData(entityType, isFlowerForest));
        }
    }

    private static void addNestFeature(BiomeLoadingEvent event) {
        Biome.Category category = event.getCategory();
        if (category == Biome.Category.NETHER) {
            event.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModFeatures.ConfiguredFeatures.NETHER_NESTS);
        } else if (category == Biome.Category.THEEND) {
            event.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModFeatures.ConfiguredFeatures.THE_END_NESTS);
        } else {
            event.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModFeatures.ConfiguredFeatures.OVERWORLD_NESTS);
        }
    }

    public static void registerBeePlacements() {
        ModEntities.getModBees().forEach((s, entityType) -> {
            boolean canSpawnInWorld = BeeRegistry.getRegistry().getBeeData(s).getSpawnData().canSpawnInWorld();
            if (canSpawnInWorld) {
                EntitySpawnPlacementRegistry.register(entityType,
                        EntitySpawnPlacementRegistry.PlacementType.ON_GROUND,
                        Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                        CustomBeeEntity::canBeeSpawn);
            }
        });
    }
}