package com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.outputs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.utils.CodecUtils;
import net.minecraftforge.fluids.FluidStack;

public record FluidOutput(FluidStack fluid, double weight) implements AbstractOutput<FluidStack> {

    public static final Codec<FluidOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CodecUtils.FLUID_STACK_CODEC.fieldOf("fluid").orElse(FluidStack.EMPTY).forGetter(FluidOutput::fluid),
            Codec.doubleRange(1.0d, Double.MAX_VALUE).fieldOf("weight").orElse(1.0d).forGetter(FluidOutput::weight)
    ).apply(instance, FluidOutput::new));

    public static final FluidOutput EMPTY = new FluidOutput(FluidStack.EMPTY, 0);

    public FluidStack fluid() {
        return fluid.copy();
    }

    public FluidStack multiply(int factor) {
        var fluidCopy = fluid.copy();
        fluidCopy.setAmount(fluidCopy.getAmount() * factor);
        return fluidCopy;
    }
}
