package com.teamresourceful.resourcefulbees.common.worldgen;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModBiomeTags;
import com.teamresourceful.resourcefulbees.platform.common.events.BlockBonemealedEvent;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.Optional;

public final class GoldenFlower {

    private static final ResourceLocation GOLD_FLOWER_FEATURE = new ResourceLocation(ModConstants.MOD_ID, "gold_flower");

    private GoldenFlower() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static void onBlockBonemealed(BlockBonemealedEvent event) {
        if (!event.isCanceled() && event.level() instanceof ServerLevel level && event.state().is(Blocks.GRASS_BLOCK) && level.getBiome(event.pos()).is(ModBiomeTags.ALLOWS_GOLD_FLOWER)) {
            RandomSource random = level.getRandom();
            if (random.nextInt(10) == 0) {
                Optional<? extends Registry<ConfiguredFeature<?, ?>>> registry = level.registryAccess().registry(Registries.CONFIGURED_FEATURE);
                if (registry.isPresent()) {
                    ConfiguredFeature<?, ?> feature = registry.get().get(GOLD_FLOWER_FEATURE);
                    if (feature != null) {
                        feature.place(level, level.getChunkSource().getGenerator(), random, event.pos());
                    }
                }
            }
        }
    }
}
