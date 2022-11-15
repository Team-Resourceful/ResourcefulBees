package com.teamresourceful.resourcefulbees.common.world;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefulbees.common.lib.ModConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBiomeModifiers;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModFeatures;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

public class BeeNestBiomeModifier implements BiomeModifier {

    public static BeeNestBiomeModifier INSTANCE = new BeeNestBiomeModifier();

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase.equals(Phase.AFTER_EVERYTHING) && !builder.getMobSpawnSettings().getSpawner(ModConstants.BEE_MOB_CATEGORY).isEmpty()) {
            if (biome.is(BiomeTags.IS_OVERWORLD)) {
                builder.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.ConfiguredFeatures.OVERWORLD_NESTS);
            } else if (biome.is(BiomeTags.IS_NETHER)) {
                builder.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.ConfiguredFeatures.NETHER_NESTS);
            } else if (biome.is(BiomeTags.IS_END)) {
                builder.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.ConfiguredFeatures.THE_END_NESTS);
            }
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return ModBiomeModifiers.NEST_MODIFIER.get();
    }

    public static Codec<BeeNestBiomeModifier> makeCodec() {
        return Codec.unit(INSTANCE);
    }
}
