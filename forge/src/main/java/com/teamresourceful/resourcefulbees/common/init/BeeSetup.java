package com.teamresourceful.resourcefulbees.common.init;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModPaths;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.registries.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModEntities;
import com.teamresourceful.resourcefullib.common.lib.Constants;
import com.teamresourceful.resourcefullib.common.utils.FileUtils;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;

import java.io.Reader;
import java.util.Locale;

import static com.teamresourceful.resourcefulbees.ResourcefulBees.LOGGER;

public final class BeeSetup {

    private BeeSetup() {
        throw new UtilityClassError();
    }

    public static void setupBees() {
        if (GeneralConfig.enableDevBees) {
            LOGGER.info("Loading Dev Bees...");
            FileUtils.setupDevResources("/data/resourcefulbees/dev/dev_bees", BeeSetup::parseBee, ModPaths.MOD_ROOT);
        }

        if (GeneralConfig.generateDefaults) {
            LOGGER.info("Copying Default Bees...");
            FileUtils.copyDefaultFiles("/data/resourcefulbees/defaults/default_bees", ModPaths.BEES, ModPaths.MOD_ROOT);
        }

        LOGGER.info("Loading Custom Bees...");
        FileUtils.streamFilesAndParse(ModPaths.BEES, BeeSetup::parseBee);

        BeeRegistry.getRegistry().regenerateCustomBeeData(null);
    }


    private static void parseBee(Reader reader, String name) {
        JsonObject jsonObject = GsonHelper.fromJson(Constants.GSON, reader, JsonObject.class);
        BeeRegistry.getRegistry().cacheRawBeeData(name.toLowerCase(Locale.ENGLISH).replace(" ", "_"), jsonObject);
    }

    public static void registerBeePlacements() {
        ModEntities.getModBees().forEach((s, entityType) ->
                SpawnPlacements.register(entityType.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CustomBeeEntity::canBeeSpawn));
    }
}