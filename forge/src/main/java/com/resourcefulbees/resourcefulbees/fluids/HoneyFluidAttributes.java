package com.resourcefulbees.resourcefulbees.fluids;

import com.resourcefulbees.resourcefulbees.api.honeydata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.utils.color.RainbowColor;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.BiFunction;

public class HoneyFluidAttributes extends FluidAttributes {

    private final HoneyBottleData honeyData;

    protected HoneyFluidAttributes(FluidAttributes.Builder builder, Fluid fluid, HoneyBottleData honeyData) {
        super(builder, fluid);
        this.honeyData = honeyData;
    }

    public static class Builder extends FluidAttributes.Builder {
        protected Builder(ResourceLocation stillTexture, ResourceLocation flowingTexture, BiFunction<FluidAttributes.Builder, Fluid, FluidAttributes> factory) {
            super(stillTexture, flowingTexture, factory);
        }
    }

    @Override
    public int getColor(IBlockDisplayReader world, BlockPos pos) {
        return getColor();
    }

    @Override
    public int getColor(FluidStack stack){
        return getColor();
    }

    @Override
    public int getColor(){
        return honeyData.isRainbow() ? RainbowColor.getRGB() : honeyData.getHoneyColorInt() | 0xff000000;
    }

    public static Builder builder(ResourceLocation stillTexture, ResourceLocation flowingTexture, HoneyBottleData honeyData) {
        return new Builder(stillTexture, flowingTexture, (builder, fluid) -> new HoneyFluidAttributes(builder, fluid, honeyData));
    }
}
