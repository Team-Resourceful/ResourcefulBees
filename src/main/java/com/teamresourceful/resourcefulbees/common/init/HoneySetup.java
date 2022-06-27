package com.teamresourceful.resourcefulbees.common.init;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.lib.ModPaths;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registry.custom.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.utils.FileUtils;
import net.minecraft.util.GsonHelper;

import java.io.Reader;
import java.util.Locale;

import static com.teamresourceful.resourcefulbees.ResourcefulBees.LOGGER;

public final class HoneySetup {

    private HoneySetup() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void setupHoney() {
        if (Boolean.TRUE.equals(CommonConfig.ENABLE_DEV_BEES.get())) {
            LOGGER.info("Loading Dev Honeys...");
            FileUtils.setupDevResources("/data/resourcefulbees/dev/dev_honey", HoneySetup::parseHoney);
        }

        if (Boolean.TRUE.equals(CommonConfig.GENERATE_DEFAULTS.get())) {
            LOGGER.info("Copying Default Honeys...");
            FileUtils.copyDefaultFiles("/data/resourcefulbees/defaults/default_honey", ModPaths.HONEY);
        }

        LOGGER.info("Loading Custom Honeys...");
        FileUtils.streamFilesAndParse(ModPaths.HONEY, HoneySetup::parseHoney);
    }

    private static void parseHoney(Reader reader, String name) {
        JsonObject jsonObject = GsonHelper.fromJson(ModConstants.GSON, reader, JsonObject.class);
        HoneyRegistry.getRegistry().cacheRawHoneyData(name.toLowerCase(Locale.ENGLISH).replace(" ", "_"), jsonObject);
    }
}
