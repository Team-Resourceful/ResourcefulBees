package com.resourcefulbees.resourcefulbees.init;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.resourcefulbees.resourcefulbees.api.beedata.BeeTrait;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.registry.TraitRegistry;
import net.minecraft.util.GsonHelper;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

import static com.resourcefulbees.resourcefulbees.ResourcefulBees.LOGGER;

public class TraitSetup {

    private TraitSetup() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    private static Path dictionaryPath;
    private static final Gson GSON = new Gson();

    public static void buildCustomTraits() {
        LOGGER.info("Registering Custom Traits...");
        addTraits();
    }

    private static void parseTrait(Reader reader, String name) {
        name = name.toLowerCase(Locale.ENGLISH).replace(" ", "_");
        JsonObject jsonObject = GsonHelper.fromJson(GSON, reader, JsonObject.class);
        BeeTrait beeTrait = BeeTrait.getCodec(name).parse(JsonOps.INSTANCE, jsonObject).getOrThrow(false, s -> LOGGER.error("Could not Create Bee Trait"));
        TraitRegistry.getRegistry().register(name, beeTrait);
    }

    private static void addTraits() {
        try (Stream<Path> zipStream = Files.walk(dictionaryPath);
             Stream<Path> jsonStream = Files.walk(dictionaryPath)) {
            zipStream.filter(f -> f.getFileName().toString().endsWith(".zip"))
                    .forEach(TraitSetup::addZippedType);
            jsonStream.filter(f -> f.getFileName().toString().endsWith(".json"))
                    .forEach(TraitSetup::addTrait);
        } catch (IOException e) {
            LOGGER.error("Could not stream custom traits!!", e);
        }
    }

    private static void addTrait(Path file) {
        File f = file.toFile();
        try {
            ModSetup.parseType(f, TraitSetup::parseTrait);
        } catch (IOException e) {
            LOGGER.error("File not found when parsing biome types");
        }
    }

    private static void addZippedType(Path file) {
        try (ZipFile zf = new ZipFile(file.toString())) {
            zf.stream().forEach(zipEntry -> {
                if (zipEntry.getName().endsWith(".json")) {
                    try {
                        ModSetup.parseType(zf, zipEntry, TraitSetup::parseTrait);
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

    public static void setDictionaryPath(Path dictionaryPath) {
        TraitSetup.dictionaryPath = dictionaryPath;
    }
}
