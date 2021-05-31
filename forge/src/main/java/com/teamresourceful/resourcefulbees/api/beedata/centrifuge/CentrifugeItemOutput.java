package com.teamresourceful.resourcefulbees.api.beedata.centrifuge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.utils.RandomCollection;

public class CentrifugeItemOutput {
    public static final Codec<CentrifugeItemOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.doubleRange(0d, 1.0d).fieldOf("chance").orElse(1.0d).forGetter(CentrifugeItemOutput::getChance),
            ItemOutput.RANDOM_COLLECTION_CODEC.fieldOf("pool").orElse(new RandomCollection<>()).forGetter(CentrifugeItemOutput::getPool)
    ).apply(instance, CentrifugeItemOutput::new));

    private final RandomCollection<ItemOutput> pool;
    private final double chance;

    public CentrifugeItemOutput(double chance, RandomCollection<ItemOutput> pool) {
        this.chance = chance;
        this.pool = pool;
    }

    public RandomCollection<ItemOutput> getPool() {
        return pool;
    }

    public double getChance() {
        return chance;
    }

}
