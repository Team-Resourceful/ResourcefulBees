package com.teamresourceful.resourcefulbees.common.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBiomeModifiers;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

public record BeeNestBiomeModifier(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features) implements BiomeModifier {

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase.equals(Phase.AFTER_EVERYTHING) && !builder.getMobSpawnSettings().getSpawner(ModConstants.BEE_CATEGORY).isEmpty() && biomes.contains(biome)) {
            features.forEach(feature -> builder.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, feature));
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return ModBiomeModifiers.NEST_MODIFIER.get();
    }

    public static Codec<BeeNestBiomeModifier> makeCodec() {
        return RecordCodecBuilder.create(instance -> instance.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(BeeNestBiomeModifier::biomes),
            PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(BeeNestBiomeModifier::features)
        ).apply(instance, BeeNestBiomeModifier::new));
    }
}
