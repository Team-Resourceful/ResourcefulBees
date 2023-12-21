package com.teamresourceful.resourcefulbees.common.setup.data;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModPaths;
import com.teamresourceful.resourcefulbees.common.registries.custom.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.registries.custom.LoadConditionRegistry;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.lib.Constants;
import com.teamresourceful.resourcefullib.common.utils.FileUtils;
import net.minecraft.util.GsonHelper;

import java.io.Reader;
import java.util.Locale;

public final class HoneySetup {

    private HoneySetup() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static void setupHoney() {
        if (GeneralConfig.enableDevBees) {
            ModConstants.LOGGER.info("Loading Dev Honeys...");
            FileUtils.setupDevResources("/data/resourcefulbees/dev/dev_honey", HoneySetup::parseHoney, ModPaths.MOD_ROOT, FileUtils::isJson);
        }

        if (GeneralConfig.enableSupporterBees) {
            ModConstants.LOGGER.info("Loading Supporter Honeys...");
            FileUtils.setupDevResources("/data/resourcefulbees/supporter/honey", HoneySetup::parseHoney, ModPaths.MOD_ROOT, FileUtils::isJson);
        }

        if (GeneralConfig.generateDefaults) {
            ModConstants.LOGGER.info("Copying Default Honeys...");
            FileUtils.copyDefaultFiles("/data/resourcefulbees/defaults/default_honey", ModPaths.HONEY, ModPaths.MOD_ROOT, FileUtils::isJson);
        }

        ModConstants.LOGGER.info("Loading Custom Honeys...");
        FileUtils.streamFilesAndParse(ModPaths.HONEY, HoneySetup::parseHoney, FileUtils::isJson);
    }

    private static void parseHoney(Reader reader, String name) {
        JsonObject jsonObject = GsonHelper.fromJson(Constants.GSON, reader, JsonObject.class);
        if (LoadConditionRegistry.canLoad(jsonObject)) {
            HoneyRegistry.getRegistry().cacheRawHoneyData(name.toLowerCase(Locale.ENGLISH).replace(" ", "_"), jsonObject);
        }
    }
}
