package com.teamresourceful.resourcefulbees.common.init;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.ModPaths;
import com.teamresourceful.resourcefulbees.common.registry.custom.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.utils.FileUtils;
import net.minecraft.util.JSONUtils;

import java.io.Reader;
import java.util.Locale;

import static com.teamresourceful.resourcefulbees.ResourcefulBees.LOGGER;

public class HoneySetup {

    private HoneySetup() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void setupHoney() {
        if (Boolean.TRUE.equals(CommonConfig.GENERATE_DEFAULTS.get())) {
            FileUtils.setupDefaultFiles("/data/resourcefulbees/default_honey", ModPaths.HONEY);
        }

        LOGGER.info("Loading Custom Honeys..");
        FileUtils.streamFilesAndParse(ModPaths.HONEY, HoneySetup::parseHoney, "Could not stream honey!!");
    }

    static void parseHoney(Reader reader, String name) {
        JsonObject jsonObject = JSONUtils.fromJson(ModConstants.GSON, reader, JsonObject.class);
        HoneyRegistry.getRegistry().cacheRawHoneyData(name.toLowerCase(Locale.ENGLISH).replace(" ", "_"), jsonObject);
    }
}
