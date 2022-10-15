package com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.outputs;

public interface AbstractOutput<T> {

    double weight();

    T multiply(int factor);
}
