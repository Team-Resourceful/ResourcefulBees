package com.teamresourceful.resourcefulbees.common.setup.data;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModPaths;
import com.teamresourceful.resourcefulbees.common.registries.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.setup.ModDataDiscovery;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.lib.Constants;
import com.teamresourceful.resourcefullib.common.utils.FileUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;

import java.io.Reader;
import java.util.Locale;

public final class BeeSetup {

    private BeeSetup() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static void setupBees() {
        if (GeneralConfig.enableDevBees) {
            ModConstants.LOGGER.info("Loading Dev Bees...");
            FileUtils.setupDevResources("/data/resourcefulbees/dev/dev_bees", BeeSetup::parseBee, ModPaths.MOD_ROOT, FileUtils::isJson);
        }

        if (GeneralConfig.enableSupporterBees) {
            ModConstants.LOGGER.info("Loading Supporter Bees...");
            FileUtils.setupDevResources("/data/resourcefulbees/supporter/bees", BeeSetup::parseBee, ModPaths.MOD_ROOT, FileUtils::isJson);
        }

        if (GeneralConfig.generateDefaults) {
            ModConstants.LOGGER.info("Copying Default Bees...");
            FileUtils.copyDefaultFiles("/data/resourcefulbees/defaults/default_bees", ModPaths.BEES, ModPaths.MOD_ROOT, FileUtils::isJson);
        }

        ModConstants.LOGGER.info("Loading Custom Bees...");
        FileUtils.streamFilesAndParse(ModPaths.BEES, BeeSetup::parseBee, FileUtils::isJson);

        ModConstants.LOGGER.info("Discovering External Mod Bees...");
        ModDataDiscovery.discover("bees", BeeSetup::parseBee);

        BeeRegistry.getRegistry().regenerateCustomBeeData(null);
    }


    private static void parseBee(Reader reader, String name) {
        parseBee(ModIdentifier.of(name.toLowerCase(Locale.ENGLISH).replace(" ", "_")), reader);
    }

    private static void parseBee(Identifier id, Reader reader) {
        try {
            JsonObject json = GsonHelper.fromJson(Constants.GSON, reader, JsonObject.class);
            BeeRegistry.getRegistry().cacheRawBeeData(id.withSuffix("_bee"), json);
        } catch (Exception e) {
            ModConstants.LOGGER.error("Error parsing bee: {}", id);
            throw e;
        }






    }
}