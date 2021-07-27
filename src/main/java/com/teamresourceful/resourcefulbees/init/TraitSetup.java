package com.teamresourceful.resourcefulbees.init;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.api.beedata.traits.BeeTrait;
import com.teamresourceful.resourcefulbees.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.registry.TraitRegistry;
import com.teamresourceful.resourcefulbees.utils.FileUtils;
import net.minecraft.util.JSONUtils;

import java.io.Reader;
import java.nio.file.Path;
import java.util.Locale;

import static com.teamresourceful.resourcefulbees.ResourcefulBees.LOGGER;

public class TraitSetup {

    private TraitSetup() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    private static Path path;

    public static void buildCustomTraits() {
        LOGGER.info("Registering Custom Traits...");
        FileUtils.streamFilesAndParse(path, TraitSetup::parseTrait, "Could not stream custom traits!!");
    }

    private static void parseTrait(Reader reader, String name) {
        JsonObject jsonObject = JSONUtils.fromJson(ModConstants.GSON, reader, JsonObject.class);
        name = Codec.STRING.fieldOf("name").orElse(name).codec().parse(JsonOps.INSTANCE, jsonObject).get().orThrow().toLowerCase(Locale.ENGLISH).replace(" ", "_");
        BeeTrait beeTrait = BeeTrait.getCodec(name).parse(JsonOps.INSTANCE, jsonObject).getOrThrow(false, s -> LOGGER.error("Could not Create Bee Trait"));
        TraitRegistry.getRegistry().register(name, beeTrait);
    }

    public static void setPath(Path path) {
        TraitSetup.path = path;
    }
}
