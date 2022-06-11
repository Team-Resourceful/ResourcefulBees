package com.teamresourceful.resourcefulbees.api.spawndata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import com.teamresourceful.resourcefulbees.common.lib.enums.LightLevel;
import net.minecraft.util.InclusiveRange;

public record SpawnData(LightLevel lightLevel, InclusiveRange<Integer> yLevel) {

    public static final Codec<SpawnData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LightLevel.CODEC.fieldOf("lightLevel").orElse(LightLevel.ANY).forGetter(SpawnData::lightLevel),
            CodecUtils.Y_LEVEL.fieldOf("yLevel").orElse(new InclusiveRange<>(50, 256)).forGetter(SpawnData::yLevel)
    ).apply(instance, SpawnData::new));
}
