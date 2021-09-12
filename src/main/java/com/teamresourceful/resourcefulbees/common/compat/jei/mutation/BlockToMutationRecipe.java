package com.teamresourceful.resourcefulbees.common.compat.jei.mutation;

import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.BlockOutput;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.ItemOutput;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Optional;

public class BlockToMutationRecipe implements IMutationRecipe {

    private final CustomBeeData beeData;
    private final double chance;
    private final double weight;
    private ItemStack inputItem = null;
    private ItemStack outputItem = null;
    private FluidStack inputFluid = null;
    private FluidStack outputFluid = null;

    public BlockToMutationRecipe(CustomBeeData beeData, double chance, double weight, Block input, ItemOutput output){
        this.beeData = beeData;
        this.chance = chance;
        this.weight = weight;
        if (input instanceof FlowingFluidBlock) {
            inputFluid = new FluidStack(((FlowingFluidBlock) input).getFluid(), 1000);
        }else {
            inputItem = input.asItem().getDefaultInstance();
        }
        this.outputItem = output.getItemStack();
    }

    public BlockToMutationRecipe(CustomBeeData beeData, double chance, double weight, Block input, BlockOutput output){
        this.beeData = beeData;
        this.chance = chance;
        this.weight = weight;
        if (input instanceof FlowingFluidBlock) {
            inputFluid = new FluidStack(((FlowingFluidBlock) input).getFluid(), 1000);
        }else {
            inputItem = input.asItem().getDefaultInstance();
        }
        if (output.getBlock() instanceof FlowingFluidBlock){
            outputFluid = new FluidStack(((FlowingFluidBlock) output.getBlock()).getFluid(), 1000);
            output.getCompoundNBT().ifPresent(nbt -> {
                if (!nbt.isEmpty()) outputFluid.setTag(nbt);
            });
        }else {
            outputItem = output.getBlock().asItem().getDefaultInstance();
            output.getCompoundNBT().ifPresent(nbt -> {
                if (!nbt.isEmpty()) outputItem.setTag(nbt);
            });
        }
    }

    @Override
    public CustomBeeData getBeeData() {
        return beeData;
    }

    @Override
    public double chance() {
        return chance;
    }

    @Override
    public double weight() {
        return weight;
    }

    @Override
    public Optional<ItemStack> getInputItem() {
        return Optional.ofNullable(inputItem);
    }

    @Override
    public Optional<ItemStack> getOutputItem() {
        return Optional.ofNullable(outputItem);
    }

    @Override
    public Optional<FluidStack> getInputFluid() {
        return Optional.ofNullable(inputFluid);
    }

    @Override
    public Optional<FluidStack> getOutputFluid() {
        return Optional.ofNullable(outputFluid);
    }
}
