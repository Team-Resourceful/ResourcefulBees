package com.teamresourceful.resourcefulbees.common.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModBiomeTags;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBiomeModifiers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Optional;

public record GoldFlowerBiomeModifier(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features) implements BiomeModifier {

    private static final ResourceLocation GOLD_FLOWER_FEATURE = new ResourceLocation("resourcefulbees:gold_flower");

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase.equals(Phase.ADD) && biomes.contains(biome)) {
            features.forEach(feature -> builder.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, feature));
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return ModBiomeModifiers.GOLD_FLOWER_MODIFIER.get();
    }

    public static Codec<GoldFlowerBiomeModifier> makeCodec() {
        return RecordCodecBuilder.create(instance -> instance.group(
                Biome.LIST_CODEC.fieldOf("biomes").forGetter(GoldFlowerBiomeModifier::biomes),
                PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(GoldFlowerBiomeModifier::features)
        ).apply(instance, GoldFlowerBiomeModifier::new));
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
