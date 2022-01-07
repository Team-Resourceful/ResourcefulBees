package com.resourcefulbees.resourcefulbees.init;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.api.honeydata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.entity.passive.*;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.ModEntities;
import com.resourcefulbees.resourcefulbees.registry.ModFeatures;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
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
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.resourcefulbees.resourcefulbees.ResourcefulBees.LOGGER;
import static com.resourcefulbees.resourcefulbees.config.Config.ENABLE_EASTER_EGG_BEES;
import static com.resourcefulbees.resourcefulbees.config.Config.GENERATE_DEFAULTS;

public class BeeSetup {

    private BeeSetup() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    private static final String JSON = ".json";
    private static final String ZIP = ".zip";

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
        if (Boolean.TRUE.equals(ENABLE_EASTER_EGG_BEES.get())) {
            OreoBee.register();
            KittenBee.register();
            YetiBee.register();
            StarryBee.register();
            StarryBee.register();
        }
        if (Boolean.TRUE.equals(Config.ENABLE_CUSTOM_PATREON_BEES.get())) {
            AbBee.register();
        }
        if (Boolean.TRUE.equals(GENERATE_DEFAULTS.get())) {
            setupDefaultBees();
            setupDefaultHoney();
        }
        addBees();
        addHoney();
    }

    private static void parseBee(File file) throws IOException {
        String name = file.getName();
        name = name.substring(0, name.indexOf('.'));

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
        name = name.toLowerCase(Locale.ENGLISH).replace(" ", "_");
        Gson gson = new Gson();
        try {
            CustomBeeData bee = gson.fromJson(reader, CustomBeeData.class);
            bee.setName(name);
            bee.setShouldResourcefulBeesDoForgeRegistration(true);
            BeeRegistry.getRegistry().registerBee(name, bee);
        } catch (JsonSyntaxException e) {
            String exception = String.format("Error was found trying to parse bee: %s. Json is invalid, validate it here : https://jsonlint.com/", name);
            throw new JsonSyntaxException(exception);
        }
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
        name = name.toLowerCase(Locale.ENGLISH).replace(" ", "_");
        Gson gson = new Gson();
        try {
            HoneyBottleData honey = gson.fromJson(reader, HoneyBottleData.class);
            if (honey.getName() == null) honey.setName(name);
            honey.setShouldResourcefulBeesDoForgeRegistration(true);
            BeeRegistry.getRegistry().registerHoney(honey.getName(), honey);
        } catch (JsonSyntaxException e) {
            String exception = String.format("Error was found trying to parse honey: %s. Json is invalid, validate it here : https://jsonlint.com/", name);
            throw new JsonSyntaxException(exception);
        }
    }

    private static void addBees() {
        LOGGER.info("Registering Custom Bees...");
        try (Stream<Path> zipStream = Files.walk(beePath);
             Stream<Path> jsonStream = Files.walk(beePath)) {
            zipStream.filter(f -> f.getFileName().toString().endsWith(ZIP)).forEach(BeeSetup::addZippedBee);
            jsonStream.filter(f -> f.getFileName().toString().endsWith(JSON)).forEach(BeeSetup::addBee);
        } catch (IOException e) {
            LOGGER.error("Could not stream bees!!", e);
        }
    }

    private static void addHoney() {
        LOGGER.info("Registering Custom Honeys..");
        try (Stream<Path> zipStream = Files.walk(honeyPath);
             Stream<Path> jsonStream = Files.walk(honeyPath)) {
            zipStream.filter(f -> f.getFileName().toString().endsWith(ZIP)).forEach(BeeSetup::addZippedHoney);
            jsonStream.filter(f -> f.getFileName().toString().endsWith(JSON)).forEach(BeeSetup::addHoney);
        } catch (IOException e) {
            LOGGER.error("Could not stream honey!!", e);
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
        try (ZipFile zf = new ZipFile(file.toString())) {
            zf.stream().forEach(zipEntry -> {
                if (zipEntry.getName().endsWith(JSON)) {
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
            LOGGER.warn("Could not read ZipFile! ZipFile: {}", file.getFileName());
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
        try (ZipFile zf = new ZipFile(file.toString())) {
            zf.stream().forEach(zipEntry -> {
                if (zipEntry.getName().endsWith(JSON)) {
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
            LOGGER.warn("Could not read ZipFile! ZipFile: {}", file.getFileName());
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
            LOGGER.error("Could not setup default bees!!", e);
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
            LOGGER.error("Could not setup default honey!!", e);
        }
    }

    private static void copyDefaultHoney(Path source) {
        try (Stream<Path> sourceStream = Files.walk(source)) {
            sourceStream.filter(f -> f.getFileName().toString().endsWith(JSON))
                    .forEach(path -> {
                        File targetFile = new File(String.valueOf(Paths.get(honeyPath.toString(), "/", path.getFileName().toString())));
                        try {
                            Files.copy(path, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            LOGGER.error("Could not copy default honey!!", e);
                        }
                    });
        } catch (IOException e) {
            LOGGER.error("Could not stream honey!!", e);
        }
    }

    private static void createFileSystem(Path source) throws IOException {
        try (FileSystem fileSystem = FileSystems.newFileSystem(source, null)) {
            Path defaultBees = fileSystem.getPath("/data/resourcefulbees/default_bees");
            Path defaultHoney = fileSystem.getPath("/data/resourcefulbees/default_honey");
            if (Files.exists(defaultBees)) {
                copyDefaultBees(defaultBees);
            }
            if (Files.exists(defaultHoney)) {
                copyDefaultHoney(defaultHoney);
            }
        }
    }

    private static void copyDefaultBees(Path source) {
        try (Stream<Path> sourceStream = Files.walk(source)) {
            sourceStream.filter(f -> f.getFileName().toString().endsWith(JSON))
                    .forEach(path -> {
                        File targetFile = new File(String.valueOf(Paths.get(beePath.toString(), "/", path.getFileName().toString())));
                        try {
                            Files.copy(path, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            LOGGER.error("Could not copy default bees!!", e);
                        }
                    });
        } catch (IOException e) {
            LOGGER.error("Could not stream bees!!", e);
        }
    }

    public static void onBiomeLoad(BiomeLoadingEvent event) {
        if (event.getName() != null && BeeRegistry.getSpawnableBiomes().containsKey(event.getName())) {
            BeeRegistry.getSpawnableBiomes().get(event.getName()).forEach(customBeeData -> {
                EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(customBeeData.getEntityTypeRegistryID());
                if (entityType != null) {
                    event.getSpawns().getSpawner(EntityClassification.CREATURE)
                            .add(new MobSpawnInfo.Spawners(entityType,
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
        ModEntities.getModBees().forEach((s, entityTypeRegistryObject) -> {
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