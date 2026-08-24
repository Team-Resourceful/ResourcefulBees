package com.teamresourceful.resourcefulbees.common.world.gen;

import com.teamresourceful.resourcefulbees.common.config.WorldGenConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModBiomeTags;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModBlockTags;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;

public final class GoldenFlower {

    private static final ResourceKey<ConfiguredFeature<?, ?>> GOLD_FLOWER_FEATURE = ResourceKey.create(Registries.CONFIGURED_FEATURE, ModIdentifier.of("gold_flower"));

    private GoldenFlower() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static void onBonemeal(BonemealEvent event) {
        if (event.isCanceled()) {
            return;
        }

        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        if (!event.getState().is(ModBlockTags.GOLD_FLOWER_BONEMEALABLE)) {
            return;
        }

        if (!level.getBiome(event.getPos()).is(ModBiomeTags.ALLOWS_GOLD_FLOWER)) {
            return;
        }

        RandomSource random = level.getRandom();

        if (random.nextInt(WorldGenConfig.goldFlowerBonemealChance) != 0) {
            return;
        }

        ConfiguredFeature<?, ?> feature = level.registryAccess()
                .lookupOrThrow(Registries.CONFIGURED_FEATURE)
                .getOrThrow(GOLD_FLOWER_FEATURE)
                .value();

        placePatch(level, feature, event.getPos(), random);
    }

    private static void placePatch(ServerLevel level, ConfiguredFeature<?, ?> feature, BlockPos origin, RandomSource random) {
        int tries = WorldGenConfig.goldFlowerBonemealTries;
        int spread = WorldGenConfig.goldFlowerBonemealXZSpread;

        for (int i = 0; i < tries; i++) {
            int xOffset = random.nextInt(spread * 2 + 1) - spread;
            int zOffset = random.nextInt(spread * 2 + 1) - spread;

            BlockPos surface = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, origin.offset(xOffset, 0, zOffset));

            if (!level.isEmptyBlock(surface)) {
                continue;
            }

            feature.place(level, level.getChunkSource().getGenerator(), random, surface);
        }
    }
}