package com.teamresourceful.resourcefulbees.common.recipes.centrifuge.outputs;

public interface AbstractOutput<T> {

    double weight();

    T multiply(int factor);
}
