package com.teamresourceful.resourcefulbees.common.recipes.centrifuge.outputs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.recipes.base.RecipeFluid;

public record FluidOutput(RecipeFluid fluid, double weight) implements AbstractOutput<RecipeFluid> {

    public static final Codec<FluidOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RecipeFluid.CODEC.fieldOf("fluid").orElse(RecipeFluid.EMPTY).forGetter(FluidOutput::fluid),
            Codec.doubleRange(1.0d, Double.MAX_VALUE).fieldOf("weight").orElse(1.0d).forGetter(FluidOutput::weight)
    ).apply(instance, FluidOutput::new));

    public static final FluidOutput EMPTY = new FluidOutput(RecipeFluid.EMPTY, 0);

    public RecipeFluid multiply(int factor) {
        return new RecipeFluid(this.fluid.fluid(), this.fluid.amount() * factor, this.fluid.tag());
    }
}
