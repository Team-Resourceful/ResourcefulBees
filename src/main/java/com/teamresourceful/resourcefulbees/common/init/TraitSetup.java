package com.teamresourceful.resourcefulbees.common.init;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.api.beedata.traits.BeeTrait;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.lib.ModPaths;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registry.custom.TraitRegistry;
import com.teamresourceful.resourcefulbees.common.utils.FileUtils;
import net.minecraft.util.GsonHelper;

import java.io.Reader;
import java.util.Locale;

import static com.teamresourceful.resourcefulbees.ResourcefulBees.LOGGER;

public final class TraitSetup {

    private TraitSetup() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void buildCustomTraits() {
        if (Boolean.TRUE.equals(CommonConfig.ENABLE_DEV_BEES.get())) {
            LOGGER.info("Loading Dev Traits...");
            FileUtils.setupDevResources("/data/resourcefulbees/dev/dev_traits", TraitSetup::parseTrait);
        }

        if (Boolean.TRUE.equals(CommonConfig.GENERATE_DEFAULTS.get())) {
            LOGGER.info("Copying Default Traits...");
            FileUtils.copyDefaultFiles("/data/resourcefulbees/defaults/default_traits", ModPaths.BEE_TRAITS);
        }

        LOGGER.info("Loading Custom Traits...");
        FileUtils.streamFilesAndParse(ModPaths.BEE_TRAITS, TraitSetup::parseTrait);
    }

    private static void parseTrait(Reader reader, String name) {
        JsonObject jsonObject = GsonHelper.fromJson(ModConstants.GSON, reader, JsonObject.class);
        name = Codec.STRING.fieldOf("name").orElse(name).codec().parse(JsonOps.INSTANCE, jsonObject).get().orThrow().toLowerCase(Locale.ENGLISH).replace(" ", "_");
        BeeTrait beeTrait = BeeTrait.getCodec(name).parse(JsonOps.INSTANCE, jsonObject).getOrThrow(false, s -> LOGGER.error("Could not Create Bee Trait"));
        TraitRegistry.getRegistry().register(name, beeTrait);
    }
}
