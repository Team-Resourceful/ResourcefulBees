package com.teamresourceful.resourcefulbees.api.beedata.outputs;

import com.mojang.serialization.DataResult;
import com.teamresourceful.resourcefulbees.common.utils.RandomCollection;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashSet;
import java.util.Set;

@Unmodifiable
public class AbstractOutput {

    protected final double weight;
    protected final double chance;

    AbstractOutput(double weight, double chance) {
        this.weight = weight;
        this.chance = chance;
    }

    public double getWeight() {
        return weight;
    }

    public double getChance() {
        return chance;
    }

    protected static <A extends AbstractOutput> DataResult<RandomCollection<A>> convertOutputSetToRandCol(Set<A> set) {
        RandomCollection<A> randomCollection = new RandomCollection<>();
        set.forEach(a -> randomCollection.add(a.getWeight(), a));
        return DataResult.success(randomCollection);
    }

    protected static <A extends AbstractOutput> Set<A> convertOutputRandColToSet(RandomCollection<A> randomCollection) {
        Set<A> set = new HashSet<>();
        randomCollection.forEach(set::add);
        return set;
    }
}
