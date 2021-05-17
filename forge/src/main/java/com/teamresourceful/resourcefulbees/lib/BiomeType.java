package com.teamresourceful.resourcefulbees.lib;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public class BiomeType {
    public static final Codec<BiomeType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CodecUtils.createSetCodec(ResourceLocation.CODEC).fieldOf("biomes").forGetter(BiomeType::getBiomes)
    ).apply(instance, BiomeType::new));

    private final Set<ResourceLocation> biomes;

    private BiomeType(Set<ResourceLocation> biomes) {
        this.biomes = biomes;
    }

    public Set<ResourceLocation> getBiomes() {
        return biomes;
    }
}
