package com.teamresourceful.resourcefulbees.common.setup.data;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModPaths;
import com.teamresourceful.resourcefulbees.common.registries.custom.HoneycombRegistry;
import com.teamresourceful.resourcefulbees.common.setup.ModDataDiscovery;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.lib.Constants;
import com.teamresourceful.resourcefullib.common.utils.FileUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;

import java.io.Reader;
import java.util.Locale;

public final class HoneycombSetup {

    private HoneycombSetup() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static void setupHoneycombs() {
        if (GeneralConfig.enableDevBees) {
            ModConstants.LOGGER.info("Loading Dev Honeycombs...");
            FileUtils.setupDevResources("/data/resourcefulbees/dev/dev_honeycombs", HoneycombSetup::parseHoneycomb, ModPaths.MOD_ROOT, FileUtils::isJson);
        }

        if (GeneralConfig.enableSupporterBees) {
            ModConstants.LOGGER.info("Loading Supporter Honeycombs...");
            FileUtils.setupDevResources("/data/resourcefulbees/supporter/honeycombs", HoneycombSetup::parseHoneycomb, ModPaths.MOD_ROOT, FileUtils::isJson);
        }

        if (GeneralConfig.generateDefaults) {
            ModConstants.LOGGER.info("Copying Default Honeycombs...");
            FileUtils.copyDefaultFiles("/data/resourcefulbees/defaults/default_honeycombs", ModPaths.HONEYCOMBS, ModPaths.MOD_ROOT, FileUtils::isJson);
        }

        ModConstants.LOGGER.info("Loading Custom Honeycombs...");
        FileUtils.streamFilesAndParse(ModPaths.HONEYCOMBS, HoneycombSetup::parseHoneycomb, FileUtils::isJson);

        ModConstants.LOGGER.info("Discovering External Mod Honeycombs...");
        ModDataDiscovery.discover("honeycombs", HoneycombSetup::parseHoneycomb);

        //if (!HoneycombRegistry.areItemsRegistered()) {
        HoneycombRegistry.registerHoneycombItems();
        //} else {
        //    HoneycombRegistry.getRegistry().regenerateVariationData();
        //}
    }

    private static void parseHoneycomb(Reader reader, String name) {
        Identifier id = ModIdentifier.of(name.toLowerCase(Locale.ROOT).replace(" ", "_"));
        parseHoneycomb(id, reader);
    }

    private static void parseHoneycomb(Identifier identifier, Reader reader) {
        try {
            JsonObject jsonObject = GsonHelper.fromJson(Constants.GSON, reader, JsonObject.class);
            HoneycombRegistry.getRegistry().cacheRawHoneycombData(identifier, jsonObject);
        } catch (Exception e) {
            ModConstants.LOGGER.error("Error parsing honeycomb: {}", identifier, e);
            throw e;
        }
    }
}
