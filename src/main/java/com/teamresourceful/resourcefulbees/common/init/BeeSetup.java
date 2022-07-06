package com.teamresourceful.resourcefulbees.common.init;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.lib.ModPaths;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModEntities;
import com.teamresourceful.resourcefullib.common.utils.FileUtils;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;

import java.io.Reader;
import java.util.Locale;

import static com.teamresourceful.resourcefulbees.ResourcefulBees.LOGGER;

public final class BeeSetup {

    private BeeSetup() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void setupBees() {
        if (Boolean.TRUE.equals(CommonConfig.ENABLE_DEV_BEES.get())) {
            LOGGER.info("Loading Dev Bees...");
            FileUtils.setupDevResources("/data/resourcefulbees/dev/dev_bees", BeeSetup::parseBee, ModConstants.MOD_ROOT);
        }

        if (Boolean.TRUE.equals(CommonConfig.GENERATE_DEFAULTS.get())) {
            LOGGER.info("Copying Default Bees...");
            FileUtils.copyDefaultFiles("/data/resourcefulbees/defaults/default_bees", ModPaths.BEES, ModConstants.MOD_ROOT);
        }

        LOGGER.info("Loading Custom Bees...");
        FileUtils.streamFilesAndParse(ModPaths.BEES, BeeSetup::parseBee);

        BeeRegistry.getRegistry().regenerateCustomBeeData(null);
    }


    private static void parseBee(Reader reader, String name) {
        JsonObject jsonObject = GsonHelper.fromJson(ModConstants.GSON, reader, JsonObject.class);
        name = Codec.STRING.fieldOf("name").orElse(name).codec().fieldOf("CoreData").codec().parse(JsonOps.INSTANCE, jsonObject).get().orThrow();
        BeeRegistry.getRegistry().cacheRawBeeData(name.toLowerCase(Locale.ENGLISH).replace(" ", "_"), jsonObject);
    }

    public static void registerBeePlacements() {
        ModEntities.getModBees().forEach((s, entityType) ->
                SpawnPlacements.register(entityType.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, createPredicate(s)));
    }

    public static <T extends Mob> SpawnPlacements.SpawnPredicate<T> createPredicate(String beeType) {
        return (type, level, reason, pos, random) -> CustomBeeEntity.canBeeSpawn(beeType, level, reason, pos);
    }
}