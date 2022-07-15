package com.teamresourceful.resourcefulbees.api.spawndata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import com.teamresourceful.resourcefulbees.common.lib.enums.LightLevel;
import net.minecraft.util.InclusiveRange;

import java.util.Optional;

public record SpawnData(LightLevel lightLevel, Optional<InclusiveRange<Integer>> yLevel) {

    public static final SpawnData DEFAULT = new SpawnData(LightLevel.ANY, Optional.empty());

    public static final Codec<SpawnData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LightLevel.CODEC.fieldOf("lightLevel").orElse(LightLevel.ANY).forGetter(SpawnData::lightLevel),
            CodecUtils.Y_LEVEL.optionalFieldOf("yLevel").forGetter(SpawnData::yLevel)
    ).apply(instance, SpawnData::new));

    public boolean canSpawnAtY(int y) {
        return yLevel.map(range -> range.isValueInRange(y)).orElse(true);
    }
}
