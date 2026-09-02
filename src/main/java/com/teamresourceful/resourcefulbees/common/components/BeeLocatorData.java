package com.teamresourceful.resourcefulbees.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record BeeLocatorData(
        BlockPos position,
        Identifier biome,
        Identifier bee,
        ResourceKey<Level> dimension
) {

    public static final Codec<BeeLocatorData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("position").forGetter(BeeLocatorData::position),
            Identifier.CODEC.fieldOf("biome").forGetter(BeeLocatorData::biome),
            Identifier.CODEC.fieldOf("bee").forGetter(BeeLocatorData::bee),
            Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(BeeLocatorData::dimension)
    ).apply(instance, BeeLocatorData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BeeLocatorData> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            BeeLocatorData::position,
            Identifier.STREAM_CODEC,
            BeeLocatorData::biome,
            Identifier.STREAM_CODEC,
            BeeLocatorData::bee,
            ResourceKey.streamCodec(Registries.DIMENSION),
            BeeLocatorData::dimension,
            BeeLocatorData::new
    );
}