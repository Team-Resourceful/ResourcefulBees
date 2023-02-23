package com.teamresourceful.resourcefulbees.common.setup.data;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModPaths;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.registries.custom.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.registries.custom.LoadConditionRegistry;
import com.teamresourceful.resourcefullib.common.lib.Constants;
import com.teamresourceful.resourcefullib.common.utils.FileUtils;
import net.minecraft.util.GsonHelper;

import java.io.Reader;
import java.util.Locale;

public final class HoneySetup {

    private HoneySetup() {
        throw new UtilityClassError();
    }

    public static void setupHoney() {
        if (GeneralConfig.enableDevBees) {
            ModConstants.LOGGER.info("Loading Dev Honeys...");
            FileUtils.setupDevResources("/data/resourcefulbees/dev/dev_honey", HoneySetup::parseHoney, ModPaths.MOD_ROOT);
        }

        if (GeneralConfig.enableSupporterBees) {
            ModConstants.LOGGER.info("Loading Supporter Honeys...");
            FileUtils.setupDevResources("/data/resourcefulbees/supporter/honey", HoneySetup::parseHoney, ModPaths.MOD_ROOT);
        }

        if (GeneralConfig.generateDefaults) {
            ModConstants.LOGGER.info("Copying Default Honeys...");
            FileUtils.copyDefaultFiles("/data/resourcefulbees/defaults/default_honey", ModPaths.HONEY, ModPaths.MOD_ROOT);
        }

        ModConstants.LOGGER.info("Loading Custom Honeys...");
        FileUtils.streamFilesAndParse(ModPaths.HONEY, HoneySetup::parseHoney);
    }

    private static void parseHoney(Reader reader, String name) {
        JsonObject jsonObject = GsonHelper.fromJson(Constants.GSON, reader, JsonObject.class);
        if (LoadConditionRegistry.canLoad(jsonObject)) {
            HoneyRegistry.getRegistry().cacheRawHoneyData(name.toLowerCase(Locale.ENGLISH).replace(" ", "_"), jsonObject);
        }
    }
}
