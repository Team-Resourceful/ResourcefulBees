package com.teamresourceful.resourcefulbees.common.worldgen;

import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModBiomeTags;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.platform.common.events.BlockBonemealedEvent;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.Optional;

public final class GoldenFlower {

    private static final ResourceLocation GOLD_FLOWER_FEATURE = new ResourceLocation(BeeConstants.MOD_ID, "gold_flower");

    private GoldenFlower() {
        throw new UtilityClassError();
    }

    public static void onBlockBonemealed(BlockBonemealedEvent event) {
        if (!event.isCanceled() && event.level() instanceof ServerLevel level && event.state().is(Blocks.GRASS_BLOCK) && level.getBiome(event.pos()).is(ModBiomeTags.ALLOWS_GOLD_FLOWER)) {
            RandomSource random = level.getRandom();
            if (random.nextInt(10) == 0) {
                Optional<? extends Registry<ConfiguredFeature<?, ?>>> registry = level.registryAccess().registry(Registry.CONFIGURED_FEATURE_REGISTRY);
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
