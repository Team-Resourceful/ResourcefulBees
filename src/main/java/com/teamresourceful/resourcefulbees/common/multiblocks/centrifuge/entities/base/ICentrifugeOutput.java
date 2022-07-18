package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base;

import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.outputs.AbstractOutput;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.CentrifugeRecipe;

public interface ICentrifugeOutput<T extends AbstractOutput> {
    boolean depositResult(CentrifugeRecipe.Output<T> recipeOutput, int processQuantity);
}
