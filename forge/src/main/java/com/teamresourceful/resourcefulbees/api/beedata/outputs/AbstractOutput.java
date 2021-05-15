package com.teamresourceful.resourcefulbees.api.beedata.outputs;

public class AbstractOutput {

    private final double weight;
    private final double chance;

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
}
