package com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.outputs;

import org.jetbrains.annotations.Unmodifiable;

@Unmodifiable
public class AbstractOutput {

    protected final double weight;

    AbstractOutput(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }
}
