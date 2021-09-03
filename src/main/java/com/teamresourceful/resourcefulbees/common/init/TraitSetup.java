package com.teamresourceful.resourcefulbees.common.init;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.api.beedata.traits.BeeTrait;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.lib.ModPaths;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registry.custom.HoneycombRegistry;
import com.teamresourceful.resourcefulbees.common.registry.custom.TraitRegistry;
import com.teamresourceful.resourcefulbees.common.utils.FileUtils;
import net.minecraft.util.JSONUtils;

import java.io.Reader;
import java.util.Locale;

import static com.teamresourceful.resourcefulbees.ResourcefulBees.LOGGER;

public class TraitSetup {

    private TraitSetup() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void buildCustomTraits() {
        if (Boolean.TRUE.equals(CommonConfig.ENABLE_EASTER_EGG_BEES.get())) {
            FileUtils.setupDevResources("/data/resourcefulbees/dev/dev_traits", TraitSetup::parseTrait, "Could not stream dev traits!");
        }

        if (Boolean.TRUE.equals(CommonConfig.GENERATE_DEFAULTS.get())) {
            FileUtils.setupDefaultFiles("/data/resourcefulbees/defaults/default_traits", ModPaths.BEE_TRAITS);
        }

        LOGGER.info("Registering Custom Traits...");
        FileUtils.streamFilesAndParse(ModPaths.BEE_TRAITS, TraitSetup::parseTrait, "Could not stream custom traits!");
    }

    private static void parseTrait(Reader reader, String name) {
        JsonObject jsonObject = JSONUtils.fromJson(ModConstants.GSON, reader, JsonObject.class);
        name = Codec.STRING.fieldOf("name").orElse(name).codec().parse(JsonOps.INSTANCE, jsonObject).get().orThrow().toLowerCase(Locale.ENGLISH).replace(" ", "_");
        BeeTrait beeTrait = BeeTrait.getCodec(name).parse(JsonOps.INSTANCE, jsonObject).getOrThrow(false, s -> LOGGER.error("Could not Create Bee Trait"));
        TraitRegistry.getRegistry().register(name, beeTrait);
    }
}
