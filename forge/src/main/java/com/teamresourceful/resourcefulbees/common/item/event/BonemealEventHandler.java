package com.teamresourceful.resourcefulbees.common.item.event;

import com.teamresourceful.resourcefulbees.common.lib.tags.ModBiomeTags;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Optional;

public class BonemealEventHandler {

    private static final ResourceLocation GOLD_FLOWER_FEATURE = new ResourceLocation("resourcefulbees:gold_flower");

    private BonemealEventHandler() {
        throw new UtilityClassError();
    }

    @SubscribeEvent
    public static void onBoneMealEvent(BonemealEvent event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        if (!event.isCanceled() && level instanceof ServerLevel serverLevel && event.getBlock().is(Blocks.GRASS_BLOCK) && level.getBiome(pos).is(ModBiomeTags.ALLOWS_GOLD_FLOWER)) {
            RandomSource random = serverLevel.getRandom();
            if (random.nextInt(10) == 0) {
                Optional<? extends Registry<ConfiguredFeature<?, ?>>> registry = level.registryAccess().registry(Registry.CONFIGURED_FEATURE_REGISTRY);
                if (registry.isPresent()) {
                    ConfiguredFeature<?, ?> feature = registry.get().get(GOLD_FLOWER_FEATURE);
                    if (feature != null) {
                        feature.place(serverLevel, serverLevel.getChunkSource().getGenerator(), random, pos);
                    }
                }
            }
        }
    }
}
