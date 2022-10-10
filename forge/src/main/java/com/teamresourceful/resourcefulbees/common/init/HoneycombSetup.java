package com.teamresourceful.resourcefulbees.common.init;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.lib.ModPaths;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registry.custom.HoneycombRegistry;
import com.teamresourceful.resourcefullib.common.utils.FileUtils;
import net.minecraft.util.GsonHelper;

import java.io.Reader;
import java.util.Locale;

import static com.teamresourceful.resourcefulbees.ResourcefulBees.LOGGER;

public final class HoneycombSetup {

    private HoneycombSetup() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void setupHoneycombs() {
        if (Boolean.TRUE.equals(CommonConfig.ENABLE_DEV_BEES.get())) {
            LOGGER.info("Loading Dev Honeycombs...");
            FileUtils.setupDevResources("/data/resourcefulbees/dev/dev_honeycombs", HoneycombSetup::parseHoneycomb, ModConstants.MOD_ROOT);
        }

        if (Boolean.TRUE.equals(CommonConfig.GENERATE_DEFAULTS.get())) {
            LOGGER.info("Copying Default Honeycombs...");
            FileUtils.copyDefaultFiles("/data/resourcefulbees/defaults/default_honeycombs", ModPaths.HONEYCOMBS, ModConstants.MOD_ROOT);
        }

        LOGGER.info("Loading Custom Honeycombs...");
        FileUtils.streamFilesAndParse(ModPaths.HONEYCOMBS, HoneycombSetup::parseHoneycomb);

        if (!HoneycombRegistry.areItemsRegistered()) {
            HoneycombRegistry.registerHoneycombItems();
        } else {
            HoneycombRegistry.getRegistry().regenerateVariationData();
        }
    }

    private static void parseHoneycomb(Reader reader, String name) {
        JsonObject jsonObject = GsonHelper.fromJson(ModConstants.GSON, reader, JsonObject.class);
        name = Codec.STRING.fieldOf("name").orElse(name).codec().fieldOf("honeycomb").codec().parse(JsonOps.INSTANCE, jsonObject).result().orElse(name);
        HoneycombRegistry.getRegistry().cacheRawHoneycombData(name.toLowerCase(Locale.ENGLISH).replace(" ", "_"), jsonObject);
    }

}
