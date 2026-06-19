package com.teamresourceful.resourcefulbees.common.world.gen;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModBiomeTags;
import com.teamresourceful.resourcefulbees.events.BlockBonemealedEvent;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.Optional;

public final class GoldenFlower {

    private static final Identifier GOLD_FLOWER_FEATURE = Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "gold_flower");

    private GoldenFlower() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static void onBlockBonemealed(BlockBonemealedEvent event) {
        if (!event.isCanceled() && event.level() instanceof ServerLevel level && event.state().is(Blocks.GRASS_BLOCK) && level.getBiome(event.pos()).is(ModBiomeTags.ALLOWS_GOLD_FLOWER)) {
            RandomSource random = level.getRandom();
            if (random.nextInt(10) == 0) {
                Optional<? extends Registry<ConfiguredFeature<?, ?>>> registry = level.registryAccess().lookup(Registries.CONFIGURED_FEATURE);
                if (registry.isPresent()) {
                    ConfiguredFeature<?, ?> feature = registry.get().get(GOLD_FLOWER_FEATURE).get().value();
                    feature.place(level, level.getChunkSource().getGenerator(), random, event.pos());
                }
            }
        }
    }
}
