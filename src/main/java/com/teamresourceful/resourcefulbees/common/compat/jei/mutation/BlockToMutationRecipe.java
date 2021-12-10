package com.teamresourceful.resourcefulbees.common.compat.jei.mutation;

import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.BlockOutput;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.ItemOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraftforge.fluids.FluidStack;

import java.util.Optional;

public class BlockToMutationRecipe extends BaseMutationRecipe {

    private ItemStack inputItem = null;
    private ItemStack outputItem = null;
    private FluidStack inputFluid = null;
    private FluidStack outputFluid = null;
    private Optional<CompoundTag> nbt = Optional.empty();

    public BlockToMutationRecipe(CustomBeeData beeData, double chance, double weight, Block input, ItemOutput output){
        super(beeData, chance, weight);
        if (input instanceof LiquidBlock liquidBlock) {
            inputFluid = new FluidStack(liquidBlock.getFluid(), 1000);
        }else {
            inputItem = input.asItem().getDefaultInstance();
        }
        this.outputItem = output.getItemStack();
    }

    public BlockToMutationRecipe(CustomBeeData beeData, double chance, double weight, Block input, BlockOutput output){
        super(beeData, chance, weight);
        if (input instanceof LiquidBlock liquidBlock) {
            inputFluid = new FluidStack(liquidBlock.getFluid(), 1000);
        }else {
            inputItem = input.asItem().getDefaultInstance();
        }
        if (output.getBlock() instanceof LiquidBlock liquidBlock){
            outputFluid = new FluidStack(liquidBlock.getFluid(), 1000);
            output.getCompoundNBT().ifPresent(tag -> {
                if (!tag.isEmpty()) outputFluid.setTag(tag);
            });
        }else {
            outputItem = output.getBlock().asItem().getDefaultInstance();
            output.getCompoundNBT().ifPresent(tag -> {
                if (!tag.isEmpty()) outputItem.setTag(tag);
            });
        }
        this.nbt = output.getCompoundNBT();
    }

    @Override
    public Optional<CompoundTag> getNBT() {
        return nbt;
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
