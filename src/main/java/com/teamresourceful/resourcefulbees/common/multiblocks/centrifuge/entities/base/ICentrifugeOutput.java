package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base;

import com.teamresourceful.resourcefulbees.api.beedata.outputs.AbstractOutput;
import com.teamresourceful.resourcefulbees.common.recipe.CentrifugeRecipe;

public interface ICentrifugeOutput<T extends AbstractOutput> {
    boolean depositResult(CentrifugeRecipe.Output<T> recipeOutput);
}
