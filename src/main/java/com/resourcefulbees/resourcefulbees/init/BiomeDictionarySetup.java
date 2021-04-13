package com.resourcefulbees.resourcefulbees.init;

import com.google.gson.Gson;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.registry.BiomeDictionary;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Locale;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

import static com.resourcefulbees.resourcefulbees.ResourcefulBees.LOGGER;

public class BiomeDictionarySetup {

    private static final String JSON = ".json";
    private static final String ZIP = ".zip";

    private BiomeDictionarySetup() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    private static Path dictionaryPath;

    public static void buildDictionary() {
        LOGGER.info("Building Biome Dictionary...");
        if (Config.GENERATE_BIOME_DICTIONARIES.get()) {
            setupDefaultTypes();
        }
        addBiomeTypes();
    }

    private static void parseType(Reader reader, String name) {
        Gson gson = new Gson();
        BiomeDictionary.BiomeType biomeType = gson.fromJson(reader, BiomeDictionary.BiomeType.class);

        for (String biome: biomeType.getBiomes()) {
            BiomeDictionary.getTypes().computeIfAbsent(name.toLowerCase(Locale.ENGLISH), k -> new HashSet<>()).add(new ResourceLocation(biome.toLowerCase(Locale.ENGLISH)));
        }
    }

    private static void addBiomeTypes() {
        try (Stream<Path> zipStream = Files.walk(dictionaryPath);
             Stream<Path> jsonStream = Files.walk(dictionaryPath)) {
            zipStream.filter(f -> f.getFileName().toString().endsWith(ZIP))
                    .forEach(BiomeDictionarySetup::addZippedType);
            jsonStream.filter(f -> f.getFileName().toString().endsWith(JSON))
                    .forEach(BiomeDictionarySetup::addType);
        } catch (IOException e) {
            LOGGER.error("Could not stream biome dictionary!!", e);
        }
    }

    private static void addType(Path file) {
        File f = file.toFile();
        try {
            ModSetup.parseType(f, BiomeDictionarySetup::parseType);
        } catch (IOException e) {
            LOGGER.error("File not found when parsing biome types");
        }
    }

    private static void addZippedType(Path file) {
        try (ZipFile zf = new ZipFile(file.toString())) {
            zf.stream().forEach(zipEntry -> {
                if (zipEntry.getName().endsWith(JSON)) {
                    try {
                        ModSetup.parseType(zf, zipEntry, BiomeDictionarySetup::parseType);
                    } catch (IOException e) {
                        String name = zipEntry.getName();
                        name = name.substring(name.lastIndexOf("/") + 1, name.indexOf('.'));
                        LOGGER.error("Could not parse {} biome type from ZipFile", name);
                    }
                }
            });
        } catch (IOException e) {
            LOGGER.warn("Could not read ZipFile! ZipFile: {}", file.getFileName());
        }
    }

    private static void setupDefaultTypes() {

        ModFileInfo mod = ModList.get().getModFileById(ResourcefulBees.MOD_ID);
        Path source = mod.getFile().getFilePath();

        try {
            if (Files.isRegularFile(source)) {
                createFileSystem(source);
            } else if (Files.isDirectory(source)) {
                copyDefaultTypes(Paths.get(source.toString(), "/data/resourcefulbees/biome_dictionary"));
            }
        } catch (IOException e) {
            LOGGER.error("Could not setup default types!!", e);
        }
    }

    private static void createFileSystem(Path source) throws IOException {
        try (FileSystem fileSystem = FileSystems.newFileSystem(source, null)) {
            Path defaultBees = fileSystem.getPath("/data/resourcefulbees/biome_dictionary");
            if (Files.exists(defaultBees)) {
                copyDefaultTypes(defaultBees);
            }
        }
    }

    private static void copyDefaultTypes(Path source) {
        try (Stream<Path> sourceStream = Files.walk(source)) {
            sourceStream.filter(f -> f.getFileName().toString().endsWith(JSON))
                    .forEach(path -> {
                        File targetFile = new File(String.valueOf(Paths.get(dictionaryPath.toString(), "/", path.getFileName().toString())));
                        try {
                            Files.copy(path, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            LOGGER.error("Could not copy default biome types!!", e);
                        }
                    });
        } catch (IOException e) {
            LOGGER.error("Could not stream default biome types!!", e);
        }
    }

    public static void setDictionaryPath(Path dictionaryPath) {
        BiomeDictionarySetup.dictionaryPath = dictionaryPath;
    }
}
