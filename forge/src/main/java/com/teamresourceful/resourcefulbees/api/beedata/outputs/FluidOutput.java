package com.teamresourceful.resourcefulbees.api.beedata.outputs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class FluidOutput extends AbstractOutput{

    public static final Codec<FluidOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CodecUtils.FLUID_STACK_CODEC.fieldOf("id").orElse(FluidStack.EMPTY).forGetter(FluidOutput::getFluidStack),
            Codec.DOUBLE.fieldOf("weight").orElse(1.0d).forGetter(FluidOutput::getWeight),
            Codec.DOUBLE.fieldOf("chance").orElse(1.0).forGetter(FluidOutput::getChance)
    ).apply(instance, FluidOutput::new));

    public static final FluidOutput EMPTY = new FluidOutput(FluidStack.EMPTY, 0, 0);

    private final FluidStack fluid;

    public FluidOutput(FluidStack fluid, double weight, double chance) {
        super(weight, chance);
        this.fluid = fluid;
    }

    public CompoundTag getCompoundNBT() {
        return fluid.getTag();
    }

    public FluidStack getFluidStack() {
        return fluid.copy();
    }

    public int getAmount() {
        return fluid.getAmount();
    }

    public Fluid getFluid() {
        return fluid.getFluid();
    }
}
