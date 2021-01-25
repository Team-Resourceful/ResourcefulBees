package com.resourcefulbees.resourcefulbees.init;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.utils.INBTInstanceCreator;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.api.beedata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.entity.passive.KittenBee;
import com.resourcefulbees.resourcefulbees.entity.passive.OreoBee;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.ModFeatures;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.INBT;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.resourcefulbees.resourcefulbees.ResourcefulBees.LOGGER;
import static com.resourcefulbees.resourcefulbees.config.Config.ENABLE_EASTER_EGG_BEES;
import static com.resourcefulbees.resourcefulbees.config.Config.GENERATE_DEFAULTS;

public class BeeSetup {


    public static Path BEE_PATH;
    public static Path RESOURCE_PATH;
    public static Path HONEY_PATH;

    public static void setupBees() {
        if (ENABLE_EASTER_EGG_BEES.get()) {
            OreoBee.register();
            KittenBee.register();
        }
        if (GENERATE_DEFAULTS.get()) {
            setupDefaultBees();
            setupDefaultHoney();
        }
        addBees();
        addHoney();
    }

    private static void parseBee(File file) throws IOException {
        String name = file.getName();
        name = name.substring(0, name.indexOf('.')).toLowerCase();

        Reader r = Files.newBufferedReader(file.toPath());

        parseBee(r, name);
    }

    private static void parseBee(ZipFile zf, ZipEntry zipEntry) throws IOException {
        String name = zipEntry.getName();
        name = name.substring(name.lastIndexOf("/") + 1, name.indexOf('.'));

        InputStream input = zf.getInputStream(zipEntry);
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));

        parseBee(reader, name);
    }

    private static void parseBee(Reader reader, String name) {
        name = name.toLowerCase().replace(" ", "_");
        Gson gson = new Gson();
        CustomBeeData bee = gson.fromJson(reader, CustomBeeData.class);
        bee.setName(name);
        bee.shouldResourcefulBeesDoForgeRegistration = true;
        BeeRegistry.getRegistry().registerBee(name.toLowerCase(Locale.ENGLISH), bee);
    }

    private static void parseHoney(File file) throws IOException {
        String name = file.getName();
        name = name.substring(0, name.indexOf('.'));

        Reader r = Files.newBufferedReader(file.toPath());

        parseHoney(r, name);
    }

    private static void parseHoney(ZipFile zf, ZipEntry zipEntry) throws IOException {
        String name = zipEntry.getName();
        name = name.substring(name.lastIndexOf("/") + 1, name.indexOf('.'));

        InputStream input = zf.getInputStream(zipEntry);
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));

        parseHoney(reader, name);
    }

    private static void parseHoney(Reader reader, String name) {
        name = name.toLowerCase().replace(" ", "_");
        Gson gson = new Gson();
        HoneyBottleData honey = gson.fromJson(reader, HoneyBottleData.class);
        if (honey.getName() == null) honey.setName(name);
        honey.shouldResourcefulBeesDoForgeRegistration = true;
        BeeRegistry.getRegistry().registerHoney(honey.getName().toLowerCase(), honey);
    }

    private static void addBees() {
        try {
            Files.walk(BEE_PATH)
                    .filter(f -> f.getFileName().toString().endsWith(".zip"))
                    .forEach(BeeSetup::addZippedBee);
            Files.walk(BEE_PATH)
                    .filter(f -> f.getFileName().toString().endsWith(".json"))
                    .forEach(BeeSetup::addBee);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addHoney() {
        try {
            Files.walk(HONEY_PATH)
                    .filter(f -> f.getFileName().toString().endsWith(".zip"))
                    .forEach(BeeSetup::addZippedHoney);
            Files.walk(HONEY_PATH)
                    .filter(f -> f.getFileName().toString().endsWith(".json"))
                    .forEach(BeeSetup::addHoney);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addHoney(Path file) {
        File f = file.toFile();
        try {
            parseHoney(f);
        } catch (IOException e) {
            LOGGER.error("File not found when parsing honey");
        }
    }

    private static void addZippedHoney(Path file) {
        try {
            ZipFile zf = new ZipFile(file.toString());
            zf.stream().forEach(zipEntry -> {
                if (zipEntry.getName().endsWith(".json")) {
                    try {
                        parseHoney(zf, zipEntry);
                    } catch (IOException e) {
                        String name = zipEntry.getName();
                        name = name.substring(name.lastIndexOf("/") + 1, name.indexOf('.'));
                        LOGGER.error("Could not parse {} honey from ZipFile", name);
                    }
                }
            });
        } catch (IOException e) {
            LOGGER.warn("Could not read ZipFile! ZipFile: " + file.getFileName());
        }
    }

    private static void addBee(Path file) {
        File f = file.toFile();
        try {
            parseBee(f);
        } catch (IOException e) {
            LOGGER.error("File not found when parsing bees");
        }
    }

    private static void addZippedBee(Path file) {
        try {
            ZipFile zf = new ZipFile(file.toString());
            zf.stream().forEach(zipEntry -> {
                if (zipEntry.getName().endsWith(".json")) {
                    try {
                        parseBee(zf, zipEntry);
                    } catch (IOException e) {
                        String name = zipEntry.getName();
                        name = name.substring(name.lastIndexOf("/") + 1, name.indexOf('.'));
                        LOGGER.error("Could not parse {} bee from ZipFile", name);
                    }
                }
            });
        } catch (IOException e) {
            LOGGER.warn("Could not read ZipFile! ZipFile: " + file.getFileName());
        }
    }

    private static void setupDefaultBees() {

        ModFileInfo mod = ModList.get().getModFileById(ResourcefulBees.MOD_ID);
        Path source = mod.getFile().getFilePath();

        try {
            if (Files.isRegularFile(source)) {
                createFileSystem(source);
            } else if (Files.isDirectory(source)) {
                copyDefaultBees(Paths.get(source.toString(), "/data/resourcefulbees/default_bees"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setupDefaultHoney() {
        ModFileInfo mod = ModList.get().getModFileById(ResourcefulBees.MOD_ID);
        Path source = mod.getFile().getFilePath();

        try {
            if (Files.isRegularFile(source)) {
                createFileSystem(source);
            } else if (Files.isDirectory(source)) {
                copyDefaultHoney(Paths.get(source.toString(), "/data/resourcefulbees/default_honey"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copyDefaultHoney(Path source) throws IOException {
        Files.walk(source)
                .filter(f -> f.getFileName().toString().endsWith(".json"))
                .forEach(path -> {
                    File targetFile = new File(String.valueOf(Paths.get(HONEY_PATH.toString(), "/", path.getFileName().toString())));
                    try {
                        Files.copy(path, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    private static void createFileSystem(Path source) throws IOException {
        try (FileSystem fileSystem = FileSystems.newFileSystem(source, null)) {
            Path defaultBees = fileSystem.getPath("/data/resourcefulbees/default_bees");
            if (Files.exists(defaultBees)) {
                copyDefaultBees(defaultBees);
            }
        }
    }

    private static void copyDefaultBees(Path source) throws IOException {
        Files.walk(source)
                .filter(f -> f.getFileName().toString().endsWith(".json"))
                .forEach(path -> {
                    File targetFile = new File(String.valueOf(Paths.get(BEE_PATH.toString(), "/", path.getFileName().toString())));
                    try {
                        Files.copy(path, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    public static void onBiomeLoad(BiomeLoadingEvent event) {
        if (BeeRegistry.SPAWNABLE_BIOMES.containsKey(event.getName())) {
            BeeRegistry.SPAWNABLE_BIOMES.get(event.getName()).forEach(customBeeData -> {
                EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(customBeeData.getEntityTypeRegistryID());
                assert entityType != null;
                event.getSpawns().getSpawner(EntityClassification.CREATURE)
                        .add(new MobSpawnInfo.Spawners(entityType,
                                customBeeData.getSpawnData().getSpawnWeight() + (event.getName().getPath().contains("flower_forest") ? Config.BEE_FLOWERFOREST_MULTIPLIER.get() : 0),
                                customBeeData.getSpawnData().getMinGroupSize(),
                                customBeeData.getSpawnData().getMaxGroupSize()));
            });

            if (Config.GENERATE_BEE_NESTS.get()) {
                addNestFeature(event);
            }
        }
    }

    private static void addNestFeature(BiomeLoadingEvent event) {
        Biome.Category category = event.getCategory();
        if (category == Biome.Category.NETHER) {
            event.getGeneration().feature(GenerationStage.Decoration.VEGETAL_DECORATION, ModFeatures.ConfiguredFeatures.NETHER_NESTS);
        } else if (category == Biome.Category.THEEND) {
            event.getGeneration().feature(GenerationStage.Decoration.VEGETAL_DECORATION, ModFeatures.ConfiguredFeatures.THE_END_NESTS);
        } else {
            event.getGeneration().feature(GenerationStage.Decoration.VEGETAL_DECORATION, ModFeatures.ConfiguredFeatures.OVERWORLD_NESTS);
        }
    }

    public static void registerBeePlacements() {
        BeeRegistry.MOD_BEES.forEach((s, entityTypeRegistryObject) -> {
            CustomBeeData beeData = BeeRegistry.getRegistry().getBeeData(s);
            if (beeData.getSpawnData().canSpawnInWorld()) {
                EntitySpawnPlacementRegistry.register(entityTypeRegistryObject.get(),
                        EntitySpawnPlacementRegistry.PlacementType.ON_GROUND,
                        Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                        CustomBeeEntity::canBeeSpawn);
            }
        });
    }
}