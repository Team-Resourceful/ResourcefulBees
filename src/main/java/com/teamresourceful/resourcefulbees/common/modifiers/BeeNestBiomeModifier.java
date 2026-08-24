package com.teamresourceful.resourcefulbees.common.modifiers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBiomeModifiers;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import org.jspecify.annotations.NonNull;

public record BeeNestBiomeModifier(
        HolderSet<Biome> biomes,
        HolderSet<PlacedFeature> features
) implements BiomeModifier {

    @Override
    public void modify(
            @NonNull Holder<Biome> biome,
            @NonNull Phase phase,
            ModifiableBiomeInfo.BiomeInfo.@NonNull Builder builder
    ) {
        if (phase != Phase.ADD) {
            return;
        }

        if (!biomes.contains(biome)) {
            return;
        }
        var spawnerData = builder.getMobSpawnSettings()
                .getSpawner(ModConstants.RESOURCEFUL_BEE_CATEGORY)
                .build();
        if (spawnerData.isEmpty()) {
            return;
        }


        features.forEach(feature ->
                builder.getGenerationSettings().addFeature(
                        GenerationStep.Decoration.VEGETAL_DECORATION,
                        feature
                )
        );
    }

    @Override
    public @NonNull MapCodec<? extends BiomeModifier> codec() {
        return ModBiomeModifiers.NEST_MODIFIER.get();
    }

    public static MapCodec<BeeNestBiomeModifier> makeCodec() {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Biome.LIST_CODEC
                        .fieldOf("biomes")
                        .forGetter(BeeNestBiomeModifier::biomes),

                PlacedFeature.LIST_CODEC
                        .fieldOf("features")
                        .forGetter(BeeNestBiomeModifier::features)
        ).apply(instance, BeeNestBiomeModifier::new));
    }
}