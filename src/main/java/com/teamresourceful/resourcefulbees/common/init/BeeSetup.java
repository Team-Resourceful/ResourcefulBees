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
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
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
        JsonObject jsonObject = GsonHelper.fromJson(ModConstants.GSON, reader, JsonObject.class);
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
        Biome.BiomeCategory category = event.getCategory();
        if (category == Biome.BiomeCategory.NETHER) {
            event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.ConfiguredFeatures.NETHER_NESTS);
        } else if (category == Biome.BiomeCategory.THEEND) {
            event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.ConfiguredFeatures.THE_END_NESTS);
        } else {
            event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.ConfiguredFeatures.OVERWORLD_NESTS);
        }
    }

    public static void registerBeePlacements() {
        ModEntities.getModBees().forEach((s, entityType) -> {
            SpawnData spawnData = BeeRegistry.getRegistry().getBeeData(s).getSpawnData();
            boolean canSpawnInWorld = spawnData.canSpawnInWorld();
            if (canSpawnInWorld) {
                SpawnPlacements.register(entityType.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, createPredicate(spawnData));
            }
        });
    }

    public static <T extends Mob> SpawnPlacements.SpawnPredicate<T> createPredicate(SpawnData spawnData) {
        return (type, level, reason, pos, random) -> CustomBeeEntity.canBeeSpawn(spawnData, level, reason, pos);
    }
}