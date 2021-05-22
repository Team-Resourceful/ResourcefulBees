package com.teamresourceful.resourcefulbees.init;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.config.Config;
import com.teamresourceful.resourcefulbees.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.lib.ModConstants;
import com.teamresourceful.resourcefulbees.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.registry.HoneyRegistry;
import com.teamresourceful.resourcefulbees.registry.ModEntities;
import com.teamresourceful.resourcefulbees.registry.ModFeatures;
import com.teamresourceful.resourcefulbees.utils.FileUtils;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.io.Reader;
import java.nio.file.Path;
import java.util.Locale;

import static com.teamresourceful.resourcefulbees.ResourcefulBees.LOGGER;

public class BeeSetup {

    private BeeSetup() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    private static Path beePath;
    private static Path resourcePath;
    private static Path honeyPath;

    public static void setBeePath(Path path) {
        beePath = path;
    }

    public static void setResourcePath(Path path) {
        resourcePath = path;
    }

    public static Path getResourcePath() {
        return resourcePath;
    }

    public static void setHoneyPath(Path path) {
        honeyPath = path;
    }

    public static void setupBees() {
        if (Config.GENERATE_DEFAULTS.get()) {
            FileUtils.setupDefaultFiles("/data/resourcefulbees/default_bees", beePath);
            FileUtils.setupDefaultFiles("/data/resourcefulbees/default_honey", honeyPath);
            // REQUIRED RE-ENABLE THIS BEFORE RELEASE!!!
            //Config.GENERATE_DEFAULTS.set(false);
            //Config.GENERATE_DEFAULTS.save();
        }
        LOGGER.info("Loading Custom Bees...");
        FileUtils.streamFilesAndParse(beePath, BeeSetup::parseBee, "Could not stream bees!!");
        BeeRegistry.getRegistry().regenerateCustomBeeData();

        LOGGER.info("Loading Custom Honeys..");
        FileUtils.streamFilesAndParse(honeyPath, BeeSetup::parseHoney, "Could not stream honey!!");
    }

    public static void parseBee(Reader reader, String name) {
        JsonObject jsonObject = GsonHelper.fromJson(ModConstants.GSON, reader, JsonObject.class);
        name = Codec.STRING.fieldOf("name").orElse(name).codec().fieldOf("CoreData").codec().parse(JsonOps.INSTANCE, jsonObject).get().orThrow();
        BeeRegistry.getRegistry().cacheRawBeeData(name.toLowerCase(Locale.ENGLISH).replace(" ", "_"), jsonObject);
    }

    public static void parseHoney(Reader reader, String name) {
        JsonObject jsonObject = GsonHelper.fromJson(ModConstants.GSON, reader, JsonObject.class);
        HoneyRegistry.getRegistry().cacheRawHoneyData(name.toLowerCase(Locale.ENGLISH).replace(" ", "_"), jsonObject);
    }

    public static void onBiomeLoad(BiomeLoadingEvent event) {
        if (event.getName() != null && BeeRegistry.getSpawnableBiomes().containsKey(event.getName())) {
            BeeRegistry.getSpawnableBiomes().get(event.getName()).forEach(customBeeData -> {
                EntityType<?> entityType = customBeeData.getEntityType();
                if (entityType != null) {
                    event.getSpawns().getSpawner(MobCategory.CREATURE)
                            .add(new MobSpawnSettings.SpawnerData(entityType,
                                    customBeeData.getSpawnData().getSpawnWeight() + (event.getName().getPath().contains("flower_forest") ? Config.BEE_FLOWER_FOREST_MULTIPLIER.get() : 0),
                                    customBeeData.getSpawnData().getMinGroupSize(),
                                    customBeeData.getSpawnData().getMaxGroupSize()));
                }
            });

            if (Boolean.TRUE.equals(Config.GENERATE_BEE_NESTS.get())) {
                addNestFeature(event);
            }
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
        ModEntities.getModBees().forEach((s, entityTypeRegistryObject) -> {
            boolean canSpawnInWorld = BeeRegistry.getRegistry().getBeeData(s).getSpawnData().canSpawnInWorld();
            if (canSpawnInWorld) {
                SpawnPlacements.register(entityTypeRegistryObject.get(),
                        SpawnPlacements.Type.ON_GROUND,
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        CustomBeeEntity::canBeeSpawn);
            }
        });
    }
}