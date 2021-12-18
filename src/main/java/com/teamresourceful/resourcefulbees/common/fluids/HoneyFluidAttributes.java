package com.teamresourceful.resourcefulbees.common.fluids;

import com.teamresourceful.resourcefulbees.api.honeydata.HoneyFluidData;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.BiFunction;

public class HoneyFluidAttributes extends FluidAttributes {

    private final HoneyFluidData data;

    protected HoneyFluidAttributes(FluidAttributes.Builder builder, Fluid fluid, HoneyFluidData data) {
        super(builder, fluid);
        this.data = data;
    }

    public static class Builder extends FluidAttributes.Builder {
        protected Builder(ResourceLocation stillTexture, ResourceLocation flowingTexture, BiFunction<FluidAttributes.Builder, Fluid, FluidAttributes> factory) {
            super(stillTexture, flowingTexture, factory);
        }
    }

    @Override
    public int getColor(BlockAndTintGetter world, BlockPos pos) {
        return getColor();
    }

    @Override
    public int getColor(FluidStack stack){
        return getColor();
    }

    @Override
    public int getColor(){
        return data.color().getValue() | 0xff000000;
    }

    public static Builder builder(ResourceLocation stillTexture, ResourceLocation flowingTexture, HoneyFluidData data) {
        return new Builder(stillTexture, flowingTexture, (builder, fluid) -> new HoneyFluidAttributes(builder, fluid, data));
    }
}
