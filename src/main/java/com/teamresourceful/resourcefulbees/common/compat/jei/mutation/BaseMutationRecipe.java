package com.teamresourceful.resourcefulbees.common.compat.jei.mutation;

import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;

public abstract class BaseMutationRecipe implements IMutationRecipe {

    private final CustomBeeData beeData;
    private final double chance;
    private final double weight;

    protected BaseMutationRecipe(CustomBeeData beeData, double chance, double weight) {
        this.beeData = beeData;
        this.chance = chance;
        this.weight = weight;
    }

    @Override
    public CustomBeeData getBeeData() {
        return beeData;
    }

    @Override
    public double chance() {
        return chance;
    }

    @Override
    public double weight() {
        return weight;
    }
}
