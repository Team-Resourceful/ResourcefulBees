package com.teamresourceful.resourcefulbees.common.init;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModPaths;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.registries.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registries.custom.LoadConditionRegistry;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModEntities;
import com.teamresourceful.resourcefullib.common.lib.Constants;
import com.teamresourceful.resourcefullib.common.utils.FileUtils;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.Reader;
import java.util.Locale;

public final class BeeSetup {

    private BeeSetup() {
        throw new UtilityClassError();
    }

    public static void setupBees() {
        if (GeneralConfig.enableDevBees) {
            ModConstants.LOGGER.info("Loading Dev Bees...");
            FileUtils.setupDevResources("/data/resourcefulbees/dev/dev_bees", BeeSetup::parseBee, ModPaths.MOD_ROOT);
        }

        if (GeneralConfig.enableSupporterBees) {
            ModConstants.LOGGER.info("Loading Supporter Bees...");
            FileUtils.setupDevResources("/data/resourcefulbees/supporter/bees", BeeSetup::parseBee, ModPaths.MOD_ROOT);
        }

        if (GeneralConfig.generateDefaults) {
            ModConstants.LOGGER.info("Copying Default Bees...");
            FileUtils.copyDefaultFiles("/data/resourcefulbees/defaults/default_bees", ModPaths.BEES, ModPaths.MOD_ROOT);
        }

        ModConstants.LOGGER.info("Loading Custom Bees...");
        FileUtils.streamFilesAndParse(ModPaths.BEES, BeeSetup::parseBee);

        BeeRegistry.getRegistry().regenerateCustomBeeData(null);
    }


    private static void parseBee(Reader reader, String name) {
        try {
            JsonObject jsonObject = GsonHelper.fromJson(Constants.GSON, reader, JsonObject.class);
            if (LoadConditionRegistry.canLoad(jsonObject)) {
                BeeRegistry.getRegistry().cacheRawBeeData(name.toLowerCase(Locale.ENGLISH).replace(" ", "_"), jsonObject);
            }
        } catch (Exception e) {
            ModConstants.LOGGER.error("Error parsing bee: {}", name);
            throw e;
        }
    }

    @SubscribeEvent
    public static void onSpawnPlacementRegisterEvent(SpawnPlacementRegisterEvent event) {
        ModEntities.getModBees().forEach((s, entityType) ->
                event.register(entityType.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CustomBeeEntity::canBeeSpawn, SpawnPlacementRegisterEvent.Operation.REPLACE));
    }
}