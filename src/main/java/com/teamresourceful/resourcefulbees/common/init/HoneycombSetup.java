package com.teamresourceful.resourcefulbees.common.init;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.common.config.Config;
import com.teamresourceful.resourcefulbees.common.lib.ModPaths;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registry.custom.HoneycombRegistry;
import com.teamresourceful.resourcefulbees.common.utils.FileUtils;
import net.minecraft.util.JSONUtils;

import java.io.Reader;
import java.util.Locale;

import static com.teamresourceful.resourcefulbees.ResourcefulBees.LOGGER;

public class HoneycombSetup {

    private HoneycombSetup() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void setupHoneycombs() {
/*        if (Boolean.TRUE.equals(Config.ENABLE_EASTER_EGG_BEES.get())) {
            setupDevCombs();
        }*/

        if (Boolean.TRUE.equals(Config.GENERATE_DEFAULTS.get())) {
            FileUtils.setupDefaultFiles("/data/resourcefulbees/default_honeycombs", ModPaths.HONEYCOMBS);
        }
        LOGGER.info("Loading Custom Honeycombs...");
        FileUtils.streamFilesAndParse(ModPaths.HONEYCOMBS, HoneycombSetup::parseHoneycomb, "Could not stream honeycombs!!");

        if (!HoneycombRegistry.areItemsRegistered()) {
            HoneycombRegistry.registerHoneycombItems();
        } else {
            HoneycombRegistry.getRegistry().regenerateVariationData();
        }
    }

    private static void parseHoneycomb(Reader reader, String name) {
        JsonObject jsonObject = JSONUtils.fromJson(ModConstants.GSON, reader, JsonObject.class);
        name = Codec.STRING.fieldOf("name").orElse(name).codec().fieldOf("honeycomb").codec().parse(JsonOps.INSTANCE, jsonObject).get().orThrow();
        HoneycombRegistry.getRegistry().cacheRawHoneycombData(name.toLowerCase(Locale.ENGLISH).replace(" ", "_"), jsonObject);
    }

/*    private static void setupDevCombs() {
        if (Files.isRegularFile(FileUtils.MOD_ROOT)) {
            try(FileSystem fileSystem = FileSystems.newFileSystem(FileUtils.MOD_ROOT, null)) {
                Path path = fileSystem.getPath("/data/resourcefulbees/dev_bees");
                if (Files.exists(path)) {
                    FileUtils.streamFilesAndParse(path, BeeSetup::parseBee, "Could not stream dev bees!!");
                }
            } catch (IOException e) {
                LOGGER.error("Could not load source {}!!", FileUtils.MOD_ROOT);
                e.printStackTrace();
            }
        } else if (Files.isDirectory(FileUtils.MOD_ROOT)) {
            FileUtils.streamFilesAndParse(Paths.get(FileUtils.MOD_ROOT.toString(), "/data/resourcefulbees/dev_bees"), BeeSetup::parseBee, "Could not stream dev bees!!");
        }
    }*/

}
