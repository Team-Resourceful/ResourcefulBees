package com.teamresourceful.resourcefulbees.api.beedata.centrifuge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.FluidOutput;
import com.teamresourceful.resourcefulbees.common.utils.RandomCollection;

public class CentrifugeFluidOutput {
    public static final Codec<CentrifugeFluidOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.doubleRange(0d, 1.0d).fieldOf("chance").orElse(1.0d).forGetter(CentrifugeFluidOutput::getChance),
            FluidOutput.RANDOM_COLLECTION_CODEC.fieldOf("pool").orElse(new RandomCollection<>()).forGetter(CentrifugeFluidOutput::getPool)
    ).apply(instance, CentrifugeFluidOutput::new));

    private final RandomCollection<FluidOutput> pool;
    private final double chance;

    public CentrifugeFluidOutput(double chance, RandomCollection<FluidOutput> pool) {
        this.chance = chance;
        this.pool = pool;
    }

    public RandomCollection<FluidOutput> getPool() {
        return pool;
    }

    public double getChance() {
        return chance;
    }
}
